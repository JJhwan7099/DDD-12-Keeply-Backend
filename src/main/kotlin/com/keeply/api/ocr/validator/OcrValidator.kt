package com.keeply.api.ocr.validator

import com.keeply.api.ocr.dto.OcrRequestDTO
import com.keeply.global.api.exception.image.ImageNotFoundException
import com.keeply.global.api.exception.image.ImageSizeTooLargeException
import com.keeply.global.api.exception.ocr.OcrIllegalArgumentException
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class OcrValidator {
    /**
     * Validates an uploaded image file for presence and size.
     *
     * Throws if the provided file is null or exceeds 1 MB.
     *
     * @param file The uploaded image file to validate; may be null.
     * @throws ImageNotFoundException if `file` is null.
     * @throws ImageSizeTooLargeException if `file.size` is greater than 1_048_576 bytes (1 MB).
     */
    fun validateImageFile(file: MultipartFile?) {
        file ?: throw ImageNotFoundException()
        if(file.size > 1_048_576) {
            throw ImageSizeTooLargeException()
        }
    }
    /**
     * Validates an OCR analyze request's image identity rules.
     *
     * Ensures that for a new request (request.isNew == true) no imageId is provided,
     * and for an existing request (request.isNew == false) an imageId is present.
     *
     * @param request The request to validate.
     * @throws OcrIllegalArgumentException If a new request contains an imageId, or an existing request lacks an imageId.
     */
    fun validateAnalyzeRequest(request: OcrRequestDTO) {
        if (request.isNew) {
            if (request.imageId != null) {
                throw OcrIllegalArgumentException()
            }
        } else {
            if (request.imageId == null) {
                throw OcrIllegalArgumentException()
            }
        }
    }
}