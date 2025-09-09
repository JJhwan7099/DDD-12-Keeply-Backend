package com.keeply.api.image.validator

import com.keeply.api.image.dto.ImageRequestDTO.MoveImageRequestDTO
import com.keeply.api.image.dto.ImageRequestDTO.SaveRequestDTO
import com.keeply.global.api.exception.folder.InvalidFolderIdException
import com.keeply.global.api.exception.image.ImageIllegalArgumentException
import com.keeply.global.api.exception.image.ImageSizeTooLargeException
import com.keeply.global.api.exception.image.InvalidImageIdException
import com.keeply.global.api.exception.user.InvalidUserIdException
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class ImageValidator {
    /**
     * Validates that the uploaded image does not exceed the maximum allowed size.
     *
     * Checks the provided MultipartFile's size and throws an exception if it is larger than 1 MB (1,048,576 bytes).
     *
     * @param file The uploaded image to validate.
     * @throws ImageSizeTooLargeException if the file size is greater than 1,048,576 bytes.
     */
    fun validateImage(file: MultipartFile) {
        if(file.size > 1_048_576) {
            throw ImageSizeTooLargeException()
        }
    }

    /**
     * Validate a save-image request.
     *
     * Ensures `folderId` is positive. If `isCached` is true, `cachedImageId` must be non-null and non-blank;
     * otherwise `imageId` must be non-null.
     *
     * @param request Save request to validate.
     * @throws InvalidFolderIdException if `folderId` is <= 0.
     * @throws ImageIllegalArgumentException if a required image identifier is missing or blank.
     */
    fun validateSaveRequest(request: SaveRequestDTO) {
        if (request.folderId <= 0) {
            throw InvalidFolderIdException()
        }

        if (request.isCached) {
            if (request.cachedImageId.isNullOrBlank()) {
                throw ImageIllegalArgumentException()
            }
        } else {
            if (request.imageId == null) {
                throw ImageIllegalArgumentException()
            }
        }
    }

    /**
     * Validates a move-image request.
     *
     * Ensures the target folder ID in the request is a positive value.
     *
     * @param request The move request; its `folderId` must be > 0.
     * @throws InvalidFolderIdException if `request.folderId` is less than or equal to 0.
     */
    fun validateMoveRequest(request: MoveImageRequestDTO) {
        if (request.folderId <= 0) {
            throw InvalidFolderIdException()
        }
    }
    /**
     * Validates identifiers for a delete-image request.
     *
     * Ensures both `userId` and `imageId` are positive (> 0).
     *
     * @param userId The ID of the user performing the deletion; must be > 0.
     * @param imageId The ID of the image to delete; must be > 0.
     * @throws InvalidImageIdException if `imageId` is not > 0.
     * @throws InvalidUserIdException if `userId` is not > 0.
     */
    fun validateDeleteRequest(userId: Long, imageId: Long) {
        if(imageId <= 0) {
            throw InvalidImageIdException()
        }
        if(userId <= 0) {
            throw InvalidUserIdException()
        }
    }
}