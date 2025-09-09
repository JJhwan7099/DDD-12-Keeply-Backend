package com.keeply.api.image.service

import com.keeply.api.image.dto.ImageRequestDTO
import com.keeply.api.image.dto.ImageResponseDTO
import com.keeply.api.image.validator.ImageValidator
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
import com.keeply.global.api.exception.image.ImageNotFoundException
import com.keeply.global.api.exception.user.UserNotFoundException
import com.keeply.global.aws.s3.S3Service
import com.keeply.global.redis.RedisService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Duration
import java.time.LocalDate
import java.util.*

@Service
@Transactional
class ImageService (
    private val imageRepository: ImageRepository,
    private val imageDomainService: ImageDomainService,
    private val folderRepository: FolderRepository,
    private val userRepository: UserRepository,
    private val redisService: RedisService,
    private val s3Service: S3Service,
    private val imageValidator: ImageValidator
) {
    /**
     * Saves an image previously cached in Redis as a persistent Image entity and returns its id.
     *
     * Validates the request, retrieves the target folder and user, reads the cached OCR image (base64 payload)
     * from Redis, and delegates to the domain service to persist the image.
     *
     * @param userId ID of the user performing the save; used to authorize and look up folder/user.
     * @param requestDTO Contains `cachedImageId`, optional `imageInsight`, and `folderId` for the new image.
     * @return ApiResponse wrapping ImageResponseDTO.SaveResponseDTO with the newly created imageId.
     * @throws com.keeply.global.api.exception.folder.FolderNotFoundException if the target folder does not exist for the user.
     * @throws com.keeply.global.api.exception.user.UserNotFoundException if the user does not exist.
     */
    fun saveCachedImage(userId: Long, requestDTO: ImageRequestDTO.SaveRequestDTO): ApiResponse<ImageResponseDTO.SaveResponseDTO> {
        imageValidator.validateSaveRequest(requestDTO)
        val cachedImageId = requestDTO.cachedImageId
        val imageInsight = requestDTO.imageInsight
        val folderId = requestDTO.folderId

        val folder = getFolder(userId, folderId)
        val user = getUser(userId)

        val cachedOcrImage = redisService.getCachedImage(cachedImageId)

        val base64Image = cachedOcrImage.base64Image
        val detectedText = cachedOcrImage.detectedText

        val image = imageDomainService.saveImage(
            insight = imageInsight,
            user = user,
            folder = folder,
            base64Image = base64Image,
        )

        return ApiResponse.success(
            HttpStatus.OK,
            ImageResponseDTO.SaveResponseDTO(
                imageId = image.id!!
            )
        )
    }

    /**
     * Assigns a folder and insight to an existing image, marks it categorized, and clears any scheduled deletion.
     *
     * Validates the request, verifies the target folder and image belong to the user, updates the image's
     * insight, folder, categorization flag, and scheduled deletion timestamp, then returns the saved image ID.
     *
     * @param userId ID of the user performing the operation.
     * @param requestDTO Request DTO containing `imageId`, optional `imageInsight`, and `folderId`.
     * @return ApiResponse containing ImageResponseDTO.SaveResponseDTO with the updated imageId.
     */
    fun setFolderOfImage(userId: Long, requestDTO: ImageRequestDTO.SaveRequestDTO): ApiResponse<ImageResponseDTO.SaveResponseDTO> {
        imageValidator.validateSaveRequest(requestDTO)
        val imageId = requestDTO.imageId
        val imageInsight = requestDTO.imageInsight
        val folderId = requestDTO.folderId

        val folder = getFolder(userId, folderId)

        val image = getImage(imageId!!, userId)

        image.insight = imageInsight
        image.folder = folder
        image.isCategorized = true
        image.scheduledDeleteAt = null

        return ApiResponse.success(
            HttpStatus.OK,
            ImageResponseDTO.SaveResponseDTO(
                imageId = imageId
            )
        )
    }

    /**
     * Saves an uploaded image as an uncategorized image for the given user.
     *
     * The image file is validated, encoded to Base64, persisted without a folder or insight,
     * marked as uncategorized, and scheduled to be deleted 30 days after creation.
     *
     * @param userId ID of the user who owns the image.
     * @param file The uploaded image file to save; will be stored uncategorized and scheduled for deletion in 30 days.
     * @return A SaveResponseDTO containing the persisted image's ID.
     * @throws UserNotFoundException if the user does not exist.
     */
    fun saveUncategorizedImage(userId: Long, file: MultipartFile): ApiResponse<ImageResponseDTO.SaveResponseDTO> {
        imageValidator.validateImage(file)
        val base64Image = Base64.getEncoder().encodeToString(file.bytes)
        val user = getUser(userId)
        val image = imageDomainService.saveImage(
            insight = null,
            user = user,
            folder = null,
            base64Image = base64Image,
        )
        image.isCategorized = false
        image.scheduledDeleteAt = image.createdAt!!.plusDays(30)
        return ApiResponse.success(
            HttpStatus.OK,
            ImageResponseDTO.SaveResponseDTO(
                imageId = image.id!!,
            )
        )
    }

    /**
     * Saves an uploaded image into the specified folder for the given user and returns the new image's ID.
     *
     * The image is validated, converted to Base64, and persisted with no extracted insight (insight = null).
     *
     * @param file The uploaded image file to save.
     * @return ApiResponse containing ImageResponseDTO.SaveResponseDTO with the persisted image's ID.
     * @throws UserNotFoundException if the userId does not correspond to an existing user.
     * @throws FolderNotFoundException if the folderId is not found for the given user.
     */
    fun saveImage(userId: Long, file: MultipartFile, folderId: Long): ApiResponse<ImageResponseDTO.SaveResponseDTO> {
        imageValidator.validateImage(file)
        val base64Image = Base64.getEncoder().encodeToString(file.bytes)
        val user = getUser(userId)
        val folder = getFolder(userId, folderId)
        val image = imageDomainService.saveImage(
            insight = null,
            user = user,
            folder = folder,
            base64Image = base64Image,
        )
        return ApiResponse.success(
            HttpStatus.OK,
            ImageResponseDTO.SaveResponseDTO(
                imageId = image.id!!
            )
        )
    }

    /**
     * Moves an image into a different folder for the given user.
     *
     * Validates the move request, verifies the target folder and image belong to the user,
     * updates the image's folder, and returns the moved image and folder identifiers.
     *
     * @param userId ID of the user performing the move.
     * @param imageId ID of the image to move.
     * @param requestDTO Request containing the target folderId.
     * @return ApiResponse containing a MoveImageResponseDTO with `imageId` and `folderId`.
     *
     * @throws FolderNotFoundException if the target folder does not exist for the user.
     * @throws ImageNotFoundException if the image does not exist for the user.
     */
    fun moveImage(userId: Long, imageId: Long, requestDTO: ImageRequestDTO.MoveImageRequestDTO): ApiResponse<ImageResponseDTO.MoveImageResponseDTO> {
        imageValidator.validateMoveRequest(requestDTO)
        val folder = getFolder(userId, requestDTO.folderId)
        val image = getImage(imageId, userId)
        image.folder = folder
        return ApiResponse.success(
            HttpStatus.OK,
            ImageResponseDTO.MoveImageResponseDTO(
                imageId = image.id!!,
                folderId = folder.id!!,
            )
        )
    }

    /**
     * Retrieves metadata and a presigned S3 URL for an image belonging to a user.
     *
     * Returns an ImageInfoDTO containing the image id, a presigned URL for access, OCR insight,
     * folder name and color (if any), categorization flag, scheduled deletion timestamp, and
     * daysUntilDeletion which is null for categorized images or the number of days until
     * scheduled deletion for uncategorized images.
     *
     * @return ImageResponseDTO.ImageInfoDTO wrapped in an ApiResponse with HTTP 200.
     * @throws ImageNotFoundException if the image with the given id does not exist for the user.
     */
    fun getImageInfo(userId: Long, imageId: Long): ApiResponse<ImageResponseDTO.ImageInfoDTO> {
        val image = getImage(imageId, userId)
        val daysUntilDeletion = if(image.isCategorized) null
        else Duration.between(LocalDate.now(), image.scheduledDeleteAt).toDays()
        return ApiResponse.success(
            HttpStatus.OK,
            ImageResponseDTO.ImageInfoDTO(
                imageId = image.id!!,
                presignedUrl = s3Service.generatePresignedUrl(image.s3Key),
                insight = image.insight,
                folderName = image.folder?.name,
                folderColor = image.folder?.color,
                isCategorized = image.isCategorized,
                scheduledDeleteAt = image.scheduledDeleteAt,
                daysUntilDeletion = daysUntilDeletion
            )
        )
    }

    /**
     * Deletes the specified image that belongs to the given user.
     *
     * Validates the delete request, ensures the user and image exist, and delegates deletion to the domain service.
     *
     * @param userId ID of the user who owns the image.
     * @param imageId ID of the image to delete.
     * @return ApiResponse containing a Message confirming deletion.
     * @throws com.keeply.global.api.exception.image.ImageNotFoundException if the image does not exist for the user.
     * @throws com.keeply.global.api.exception.user.UserNotFoundException if the user does not exist.
     */
    fun deleteImage(userId: Long, imageId: Long) : ApiResponse<Message> {
        imageValidator.validateDeleteRequest(userId,imageId)
        val image = getImage(imageId, userId)
        val user = getUser(userId)
        imageDomainService.deleteImage(user, image)

        return ApiResponse.success(
            HttpStatus.OK,
            Message(
                "$userId 유저의 $imageId 가 삭제되었습니다."
            )
        )
    }

    /**
         * Retrieves a User by ID.
         *
         * @param userId The ID of the user to retrieve.
         * @return The found User.
         * @throws UserNotFoundException if no user exists with the given ID.
         */
        private fun getUser(userId: Long): User = userRepository.findUserById(userId)
        ?: throw UserNotFoundException()


    /**
         * Retrieves an Image by its id belonging to the specified user.
         *
         * @param imageId The image's identifier.
         * @param userId The owner's user id; used to ensure the image belongs to this user.
         * @return The matching Image.
         * @throws ImageNotFoundException if no image with the given id exists for the user.
         */
        private fun getImage(imageId: Long, userId: Long): Image = imageRepository.findImageByIdAndUserId(imageId,userId)
        ?: throw ImageNotFoundException()


    /**
         * Retrieves a Folder belonging to the given user by ID.
         *
         * @param userId ID of the folder owner.
         * @param folderId ID of the folder to retrieve.
         * @return The Folder if found.
         * @throws FolderNotFoundException if no folder exists with the given `folderId` for `userId`.
         */
        private fun getFolder(userId: Long, folderId: Long): Folder = folderRepository.findByUserIdAndId(userId, folderId)
        ?: throw FolderNotFoundException()
}