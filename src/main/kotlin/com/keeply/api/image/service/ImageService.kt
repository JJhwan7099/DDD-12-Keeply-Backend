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
import com.keeply.global.aws.s3.S3Service
import com.keeply.global.api.dto.ApiResponse
import com.keeply.global.api.dto.Message
import com.keeply.global.exception.folder.FolderNotFoundException
import com.keeply.global.exception.image.ImageNotFoundException
import com.keeply.global.exception.user.UserNotFoundException
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

    private fun getUser(userId: Long): User = userRepository.findUserById(userId)
        ?: throw UserNotFoundException()


    private fun getImage(imageId: Long, userId: Long): Image = imageRepository.findImageByIdAndUserId(imageId,userId)
        ?: throw ImageNotFoundException()


    private fun getFolder(userId: Long, folderId: Long): Folder = folderRepository.findByUserIdAndId(userId, folderId)
        ?: throw FolderNotFoundException()
}