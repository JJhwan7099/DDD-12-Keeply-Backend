package com.keeply.api.ocr.service

import com.keeply.api.ocr.dto.OcrRequestDTO
import com.keeply.api.ocr.dto.OcrResponseDTO
import com.keeply.api.ocr.validator.OcrValidator
import com.keeply.domain.image.repository.ImageRepository
import com.keeply.global.api.ApiResponse
import com.keeply.global.api.exception.image.ImageNotFoundException
import com.keeply.global.aws.s3.S3Service
import com.keeply.global.common.googlevision.GoogleVisionAPI
import com.keeply.global.redis.RedisService
import org.springframework.http.HttpStatus
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
    /**
     * Validates and analyzes a newly uploaded image, optionally performs OCR, caches the image and result, and returns the cache id with detected text.
     *
     * If `requestDTO.isSkip` is false, runs OCR on the provided file; otherwise skips OCR and returns an empty detected text.
     * The image bytes and detected text are stored in Redis under a generated cache identifier.
     *
     * @param requestDTO Request parameters for analysis. The `isSkip` flag controls whether OCR is performed.
     * @param file The uploaded image file to analyze; must be non-null and a valid image (validated before use).
     * @return An ApiResponse containing an OcrResponseDTO with the generated cache identifier and the detected text (possibly empty when skipped).
     */
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

        return ApiResponse.success(
            HttpStatus.OK,
            OcrResponseDTO(cachedImageId, detectedText)
        )
    }

    /**
     * Extracts text from an image previously saved for the given user and returns it in an ApiResponse.
     *
     * Validates the request, loads the image record by imageId and userId, retrieves the image file from S3,
     * runs OCR to extract detected text, and returns the result with HTTP 200.
     *
     * @param userId ID of the owner of the saved image.
     * @param requestDTO Request containing the imageId to analyze (must be non-null).
     * @param file Ignored for saved-image analysis; the stored image is retrieved from S3 instead.
     * @return ApiResponse with an OcrResponseDTO containing the extracted text.
     * @throws ImageNotFoundException if no image exists for the given imageId and userId.
     */
    fun analyzeSavedImage(userId: Long, requestDTO: OcrRequestDTO, file: MultipartFile?): ApiResponse<OcrResponseDTO> {
        ocrValidator.validateAnalyzeRequest(requestDTO)
        val imageId = requestDTO.imageId
        val image = imageRepository.findImageByIdAndUserId(imageId!!,userId)
            ?: throw ImageNotFoundException()

        val file = s3Service.getMultipartFileFromS3(image.s3Key!!)

        val detectedText = googleVisionAPI.extractTextFromImage(file)

        return ApiResponse.success(
            HttpStatus.OK,
            OcrResponseDTO(
                detectedText = detectedText
            )
        )
    }

}