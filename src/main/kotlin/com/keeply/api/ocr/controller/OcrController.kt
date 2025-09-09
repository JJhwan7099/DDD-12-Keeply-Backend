package com.keeply.api.ocr.controller

import com.keeply.api.ocr.dto.OcrRequestDTO
import com.keeply.api.ocr.dto.OcrResponseDTO
import com.keeply.api.ocr.service.OcrService
import com.keeply.global.api.ApiResponse
import com.keeply.global.security.CustomUserDetails
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.constraints.Positive
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/ocr")
class OcrController (
    private val ocrService: OcrService
) {
    /**
     * Extracts text via OCR from an uploaded image and returns recommended tags.
     *
     * If `isNew` is true the controller processes the image as a new upload; if false it processes an existing (uncategorized) image and therefore `imageId` must be provided.
     *
     * @param isNew When true, analyze the provided file as a new image. When false, analyze an existing image identified by `imageId`.
     * @param imageId ID of the existing image to analyze; required when `isNew` is false.
     * @param isSkip Indicates whether tag recommendation steps should be skipped (true = skip).
     * @param file The uploaded image file to analyze; may be null for requests that do not include an upload.
     * @return ApiResponse containing an OcrResponseDTO with extracted text and recommended tags.
    @PostMapping("/analyze", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "OCR을 통한 텍스트 추출 및 태그 추천 API",
        description =
            "isNew: true = 신규 이미지 \n" +
                    "isNew: false = 미분류 이미지 (imageId 필수)"
    )
    fun analyze(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam("isNew") isNew: Boolean,
        @RequestParam("imageId", required = false) @Positive imageId: Long?,
        @RequestParam("isSkip") isSkip: Boolean,
        @RequestPart("file") file: MultipartFile?
    ): ApiResponse<OcrResponseDTO> {
        val requestDTO = OcrRequestDTO(isNew, imageId, isSkip)
        val apiResponse = if (requestDTO.isNew) {
            ocrService.analyzeNewImage(requestDTO, file)
        } else {
            ocrService.analyzeSavedImage(userDetails.userId, requestDTO, file)
        }
        return apiResponse
    }
}