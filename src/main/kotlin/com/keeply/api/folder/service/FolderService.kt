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
import com.keeply.global.api.dto.ApiResponse
import com.keeply.global.api.dto.Message
import com.keeply.global.aws.s3.S3Service
import com.keeply.global.exception.folder.FolderNotFoundException
import com.keeply.global.exception.user.UserNotFoundException
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

    private fun getFolderByUserIdAndFolderId(userId: Long, folderId: Long): Folder =
        folderRepository.findByUserIdAndId(userId, folderId)
            ?: throw FolderNotFoundException()

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


    private fun getUser(userId: Long): User =
        userRepository.findById(userId).orElse(
            throw UserNotFoundException()
        )

    private fun setFolderName(folderName: String, userId: Long): String {
        val existFolderNames = folderRepository.findAllNamesByUserIdAndFolderName(userId, folderName)
        if (existFolderNames.isEmpty()) return folderName

        val usedIndexes = existFolderNames.mapNotNull {
            val regex = Regex("^$folderName(\\d+)$")
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