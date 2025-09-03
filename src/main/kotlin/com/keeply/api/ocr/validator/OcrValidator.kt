package com.keeply.api.ocr.validator

import com.keeply.api.ocr.dto.OcrRequestDTO
import com.keeply.global.exception.image.ImageNotFoundException
import com.keeply.global.exception.image.ImageSizeTooLargeException
import com.keeply.global.exception.ocr.OcrIllegalArgumentException
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class OcrValidator {
    fun validateImageFile(file: MultipartFile?) {
        file ?: throw ImageNotFoundException()
        if(file.size > 1_048_576) {
            throw ImageSizeTooLargeException()
        }
    }
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