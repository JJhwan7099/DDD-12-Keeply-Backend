package com.keeply.api.ocr.service

import com.keeply.api.ocr.dto.OcrRequestDTO
import com.keeply.api.ocr.dto.OcrResponseDTO
import com.keeply.api.ocr.validator.OcrValidator
import com.keeply.domain.image.repository.ImageRepository
import com.keeply.global.aws.s3.S3Service
import com.keeply.global.common.googlevision.GoogleVisionAPI
import com.keeply.global.dto.ApiResponse
import com.keeply.global.exception.image.ImageNotFoundException
import com.keeply.global.redis.RedisService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class OcrService(
    private val redisService: RedisService,
    private val imageRepository: ImageRepository,
    private val ocrValidator: OcrValidator,
    private val s3Service: S3Service,
    private val googleVisionAPI: GoogleVisionAPI
) {
    fun analyzeNewImage(requestDTO: OcrRequestDTO, file: MultipartFile?): ApiResponse<OcrResponseDTO> {

        ocrValidator.validateImageFile(file)
        ocrValidator.validateAnalyzeRequest(requestDTO)
        val cachedImageId = UUID.randomUUID().toString()
        val imageBytes = file!!.bytes

        var detectedText: String = ""


        if(!requestDTO.isSkip) {
            detectedText = googleVisionAPI.extractTextFromImage(file)
        }

        redisService.cacheImage(cachedImageId, imageBytes, detectedText)

        return ApiResponse(
            success = true,
            response = OcrResponseDTO(cachedImageId, detectedText)
        )
    }

    fun analyzeSavedImage(userId: Long, requestDTO: OcrRequestDTO, file: MultipartFile?): ApiResponse<OcrResponseDTO> {
        ocrValidator.validateAnalyzeRequest(requestDTO)
        val imageId = requestDTO.imageId
        val image = imageRepository.findImageByIdAndUserId(imageId!!,userId)
            ?: throw ImageNotFoundException()

        val file = s3Service.getMultipartFileFromS3(image.s3Key!!)

        val detectedText = googleVisionAPI.extractTextFromImage(file)

        return ApiResponse<OcrResponseDTO>(
            success = true,
            response = OcrResponseDTO(
                detectedText = detectedText
            )
        )
    }

}