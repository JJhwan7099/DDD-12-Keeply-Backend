package com.keeply.api.folder.service

import com.keeply.api.folder.dto.FolderRequestDTO
import com.keeply.api.folder.dto.FolderRequestDTO.GetFoldersRequestDTO
import com.keeply.api.folder.dto.FolderResponseDTO
import com.keeply.api.folder.validator.FolderValidator
import com.keeply.domain.folder.entity.Folder
import com.keeply.domain.folder.repository.FolderRepository
import com.keeply.domain.image.entity.Image
import com.keeply.domain.image.repository.ImageRepository
import com.keeply.domain.image.service.ImageDomainService
import com.keeply.domain.user.entity.User
import com.keeply.domain.user.repository.UserRepository
import com.keeply.global.api.ApiResponse
import com.keeply.global.api.dto.Message
import com.keeply.global.api.exception.folder.FolderNotFoundException
import com.keeply.global.api.exception.user.UserNotFoundException
import com.keeply.global.aws.s3.S3Service
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class FolderService (
    private val userRepository: UserRepository,
    private val folderRepository: FolderRepository,
    private val imageRepository: ImageRepository,
    private val imageDomainService: ImageDomainService,
    private val folderValidator: FolderValidator,
    private val s3Service: S3Service
) {
    /**
     * Creates a new folder for the given user.
     *
     * Validates the request, ensures the folder name is unique for the user (appending a numeric suffix when needed),
     * persists the folder, and returns a folder DTO wrapped in an ApiResponse with HTTP 201 Created.
     *
     * The returned Folder DTO includes a flag and a message when the final stored name differs from the requested name.
     *
     * @param userId ID of the user who will own the new folder.
     * @param requestDTO Creation request containing the desired folder name and color.
     * @return ApiResponse containing the created Folder DTO (HTTP 201).
     * @throws UserNotFoundException if no user exists for the provided userId.
     */
    fun createFolder(userId: Long, requestDTO: FolderRequestDTO.CreateRequestDTO): ApiResponse<FolderResponseDTO.Folder> {
        folderValidator.validateCreate(requestDTO)

        val folderName = requestDTO.folderName
        val folderColor = requestDTO.folderColor

        val user = getUser(userId)

        var folder = getFolderByUserIdAndFolderName(userId, folderName)

        folder = Folder.builder()
            .name(setFolderName(folderName,userId))
            .color(folderColor)
            .user(user)
            .build()

        folder = folderRepository.save(folder)

        val response = FolderResponseDTO.Folder(
            folderId = folder.id!!,
            folderName = folder.name,
            folderColor = folder.color,
            imageCount = folder.images.size,
            updatedAt = folder.updatedAt,
            isDuplicate = folder.name != folderName,
            duplicatedMessage = if(folder.name != folderName) "이미 존재하는 폴더명 입니다. \'${folder.name}\'로 추가되었습니다."
            else "",
        )

        return ApiResponse.success(
            HttpStatus.CREATED,
            response
        )
    }

    /**
     * Retrieves a user's folders, optionally filtering by a keyword and applying sorting.
     *
     * Validates the request DTO, fetches the user's folders (sorting delegated to the helper),
     * filters names by `requestDTO.keyword` (case-insensitive) when provided, and maps each
     * Folder to a FolderResponseDTO.Folder (id, name, color, image count, updatedAt).
     *
     * @param userId The id of the user whose folders will be retrieved.
     * @param requestDTO Controls optional keyword filtering and sorting (sortBy, orderBy).
     * @return An ApiResponse wrapping a FolderResponseDTO.FolderList with the matching folders (HTTP 200).
     */
    fun getFolders(userId: Long, requestDTO: GetFoldersRequestDTO): ApiResponse<FolderResponseDTO.FolderList> {
        folderValidator.validateGetFolders(requestDTO)

        val keyword = requestDTO.keyword
        val sortBy = requestDTO.sortBy
        val orderBy = requestDTO.orderBy

        val folderList = getFolderListByUserId(userId, sortBy, orderBy)
        val result = folderList
            .filter { folder ->
                keyword?.let {
                    folder.name.contains(it, ignoreCase = true)
                } ?: true
            }
            .map { folder ->
                FolderResponseDTO.Folder(
                    folder.id!!,
                    folder.name,
                    folder.color,
                    folder.images.size,
                    folder.updatedAt
                )
            }

        return ApiResponse.success(
            HttpStatus.OK,
            FolderResponseDTO.FolderList(result)
        )
    }

    /**
     * Retrieves all images for a specific folder belonging to a user and returns their presentation DTOs.
     *
     * Each image is converted to an ImageInfo containing its id, a presigned S3 URL, insight, folder name/color,
     * categorization flag, scheduled deletion timestamp, and last-updated timestamp.
     *
     * @param userId The id of the user who owns the folder.
     * @param folderId The id of the folder whose images should be retrieved.
     * @return An ApiResponse wrapping a FolderResponseDTO.FolderImages containing the list of image DTOs (`HTTP 200 OK`).
     * @throws FolderNotFoundException if the folder with the given ids does not exist.
     */
    fun getFolderImages(userId: Long, folderId: Long): ApiResponse<FolderResponseDTO.FolderImages> {
        val folder = getFolderByUserIdAndFolderId(userId, folderId)

        val result = folder.images.map { image ->
            FolderResponseDTO.ImageInfo(
                imageId = image.id!!,
                presignedUrl = s3Service.generatePresignedUrl(image.s3Key),
                insight = image.insight,
                folderName = folder.name,
                folderColor = folder.color,
                isCategorized = image.isCategorized,
                scheduledDeleteAt = image.scheduledDeleteAt,
                updatedAt = image.updatedAt
            )
        }

        return ApiResponse.success(
            HttpStatus.OK,
            FolderResponseDTO.FolderImages(result)
        )
    }

    /**
     * Retrieves all images for the user that are not assigned to any folder and returns them as a FolderImages response.
     *
     * Each image is mapped to an ImageInfo containing a presigned S3 URL, insight, categorization flags, scheduled deletion timestamp,
     * and a computed `daysUntilDeletion` (number of days from now to `scheduledDeleteAt`, clamped to 0 if in the past).
     *
     * @param userId ID of the user whose uncategorized images are being retrieved.
     * @return ApiResponse wrapping a FolderResponseDTO.FolderImages containing the list of uncategorized images.
     */
    fun getUncategorizedImages(userId: Long): ApiResponse<FolderResponseDTO.FolderImages> {
        val images = getImages(userId)

        val result = images.map { image ->
            val scheduled = image.scheduledDeleteAt
            val daysUntilDeletion = scheduled?.let {
                val d = Duration.between(LocalDateTime.now(), it).toDays()
                if (d < 0) 0 else d
            }
            FolderResponseDTO.ImageInfo(
                imageId = image.id!!,
                presignedUrl = s3Service.generatePresignedUrl(image.s3Key),
                insight = image.insight,
                folderName = null,
                folderColor = null,
                isCategorized = image.isCategorized,
                scheduledDeleteAt = image.scheduledDeleteAt,
                daysUntilDeletion = daysUntilDeletion,
                updatedAt = image.updatedAt
            )
        }

        return ApiResponse.success(
            HttpStatus.OK,
            FolderResponseDTO.FolderImages(result)
        )
    }

    private fun getImages(userId: Long): List<Image> = imageRepository.findAllByUserIdAndFolderIsNull(userId)

    /**
     * Updates a folder's name and color for a given user and returns the updated folder representation.
     *
     * Validates the update request, ensures the new folder name is unique for the user (may be suffixed
     * if a collision exists), updates the folder color, and returns a FolderResponseDTO.Folder wrapped
     * in an ApiResponse. The response includes an `isDuplicate` flag and `duplicatedMessage` when the
     * final stored name differs from the requested name.
     *
     * @param userId ID of the user who owns the folder.
     * @param folderId ID of the folder to update.
     * @param requestDTO Update request containing the desired folderName and folderColor.
     * @return ApiResponse containing the updated FolderResponseDTO.Folder.
     * @throws FolderNotFoundException if the folder cannot be found for the given user and folderId.
     */
    fun updateFolder(userId: Long, folderId: Long, requestDTO: FolderRequestDTO.UpdateRequestDTO): ApiResponse<FolderResponseDTO.Folder> {
        folderValidator.validateUpdate(requestDTO)
        val folder = getFolderByUserIdAndFolderId(userId, folderId)
        folder.name = setFolderName(requestDTO.folderName, userId)
        folder.color = requestDTO.folderColor

        return ApiResponse.success(
            HttpStatus.OK,
            FolderResponseDTO.Folder(
                folderId = folder.id!!,
                folderName = folder.name,
                folderColor = folder.color,
                imageCount = folder.images.size,
                updatedAt = folder.updatedAt,
                isDuplicate = folder.name != requestDTO.folderName,
                duplicatedMessage = if(folder.name != requestDTO.folderName) "이미 존재하는 폴더명 입니다. \'${folder.name}\'로 추가되었습니다."
                else "",
            )
        )
    }

    /**
     * Deletes a folder and all images it contains, and returns a success message.
     *
     * Removes every image in the folder via the image domain service, deletes the folder from the repository,
     * and returns an ApiResponse with HTTP 200 and a Message confirming the deleted folder name.
     *
     * @return ApiResponse<Message> with HTTP 200 and a confirmation message.
     * @throws FolderNotFoundException if no folder exists for the given userId and folderId.
     */
    fun deleteFolder(userId: Long, folderId: Long): ApiResponse<Message> {
        val folder = getFolderByUserIdAndFolderId(userId, folderId)
        imageDomainService.deleteAllImagesInFolder(folder)
        folderRepository.delete(folder)

        return ApiResponse.success(
            HttpStatus.OK,
            Message(
                "${folder.name} 가 삭제되었습니다."
            )
        )
    }

    /**
             * Retrieves the folder with the given id belonging to the specified user.
             *
             * @param userId The owner's user id.
             * @param folderId The folder id to retrieve.
             * @return The matching Folder.
             * @throws FolderNotFoundException if no folder exists for the given userId and folderId.
             */
            private fun getFolderByUserIdAndFolderId(userId: Long, folderId: Long): Folder =
        folderRepository.findByUserIdAndId(userId, folderId)
            ?: throw FolderNotFoundException()

    /**
     * Retrieves all folders for a user and optionally sorts them.
     *
     * Supported sort keys:
     * - "updatedAt": sorts by folder.updatedAt.
     * - "imageCount": sorts by number of images in the folder.
     * Any other `sortBy` value returns folders in repository order.
     *
     * The `orderBy` parameter accepts "asc" for ascending; any other value yields descending order.
     *
     * @param userId ID of the user whose folders are fetched.
     * @param sortBy Sort key to apply ("updatedAt", "imageCount", or other).
     * @param orderBy Sort direction ("asc" for ascending; otherwise descending).
     * @return A list of Folder objects for the user, sorted according to the arguments.
     */
    private fun getFolderListByUserId(userId: Long, sortBy: String, orderBy: String): List<Folder> {
        val folderList = folderRepository.findAllByUserId(userId)
        var folders = when(sortBy) {
            "updatedAt" -> if(orderBy == "asc") folderList.sortedBy{ it.updatedAt } else folderList.sortedByDescending { it.updatedAt }
            "imageCount" -> if(orderBy == "asc") folderList.sortedBy{ it.images.size } else folderList.sortedByDescending { it.images.size }
            else -> folderList
        }
        return folders
    }

    private fun getFolderByUserIdAndFolderName(userId: Long, folderName: String): Folder? =
        folderRepository.findByUserIdAndName(userId, folderName)


    /**
         * Retrieves a User by id or throws UserNotFoundException if no such user exists.
         *
         * @param userId The id of the user to retrieve.
         * @return The found User.
         * @throws UserNotFoundException When no user with the given id exists.
         */
        private fun getUser(userId: Long): User =
        userRepository.findById(userId).orElse(
            throw UserNotFoundException()
        )

    /**
     * Returns a folder name that is unique for the given user by appending the smallest unused positive integer suffix when necessary.
     *
     * If no existing folder name for the user matches the provided base name, the base name is returned unchanged.
     * If an exact match exists or names of the form `baseNameN` (where N is a positive integer) exist, the function finds
     * the smallest positive integer not already used and returns `baseNameN`. For example, if `"Photos"` and `"Photos2"`
     * exist, this will return `"Photos3"`. The base name comparison is performed safely (special characters in the base
     * name are escaped before building the regex).
     *
     * @param folderName The desired base folder name.
     * @param userId The id of the user whose folder names should be considered.
     * @return A folder name unique for the user (either the original `folderName` or `folderName` concatenated with the
     * smallest unused positive integer suffix).
     */
    private fun setFolderName(folderName: String, userId: Long): String {
        val existFolderNames = folderRepository.findAllNamesByUserIdAndFolderName(userId, folderName)
        if (existFolderNames.isEmpty()) return folderName

        val usedIndexes = existFolderNames.mapNotNull {
            val regex = Regex("^${Regex.escape(folderName)}(\\d+)$")
            regex.find(it)?.groupValues?.get(1)?.toIntOrNull()
                ?: if (it == folderName) 1 else null
        }.toSet()

        var newIndex = 1
        while (usedIndexes.contains(newIndex)) {
            newIndex++
        }

        return if (newIndex == 1) folderName else "$folderName$newIndex"
    }
}