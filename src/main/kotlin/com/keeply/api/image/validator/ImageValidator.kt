package com.keeply.api.image.validator

import com.keeply.api.image.dto.ImageRequestDTO.MoveImageRequestDTO
import com.keeply.api.image.dto.ImageRequestDTO.SaveRequestDTO
import com.keeply.global.exception.folder.InvalidFolderIdException
import com.keeply.global.exception.image.ImageSizeTooLargeException
import com.keeply.global.exception.image.InvalidImageIdException
import com.keeply.global.exception.user.InvalidUserIdException
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class ImageValidator {
    fun validateImage(file: MultipartFile) {
        if(file.size > 1_048_576) {
            throw ImageSizeTooLargeException()
        }
    }

    fun validateSaveRequest(request: SaveRequestDTO) {
        if (request.folderId <= 0) {
            throw InvalidFolderIdException()
        }

        if (request.isCached) {
            if (request.cachedImageId.isNullOrBlank()) {
                throw IllegalArgumentException("isCached가 true일 경우 cachedImageId는 필수입니다.")
            }
        } else {
            if (request.imageId == null) {
                throw IllegalArgumentException("isCached가 false일 경우 imageId는 필수입니다.")
            }
        }
    }

    fun validateMoveRequest(request: MoveImageRequestDTO) {
        if (request.folderId <= 0) {
            throw InvalidFolderIdException()
        }
    }
    fun validateDeleteRequest(userId: Long, imageId: Long) {
        if(imageId <= 0) {
            throw InvalidImageIdException()
        }
        if(userId <= 0) {
            throw InvalidUserIdException()
        }
    }
}