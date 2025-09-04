package com.keeply.api.image.controller

import com.keeply.api.image.dto.ImageRequestDTO
import com.keeply.api.image.dto.ImageResponseDTO
import com.keeply.api.image.service.ImageService
import com.keeply.global.api.dto.ApiResponse
import com.keeply.global.api.dto.Message
import com.keeply.global.security.CustomUserDetails
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImageController (
    private val imageService: ImageService
) {
    @PostMapping
    @Operation(summary = "이미지 저장(폴더o)",
        description =
        "신규로 OCR을 거친 이미지는 cachedImageId가 필요," +
                "미분류이미지에서 OCR을 거친 이미지는 imageId가 필요"
    )
    fun saveImage(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestBody requestDTO: ImageRequestDTO.SaveRequestDTO
    ): ApiResponse<ImageResponseDTO.SaveResponseDTO> {
        val apiResponse = if (requestDTO.isCached) {
            imageService.saveCachedImage(userDetails.userId, requestDTO)
        } else {
            imageService.setFolderOfImage(userDetails.userId, requestDTO)
        }
        return apiResponse
    }
    @PostMapping("/save")
    @Operation(summary = "ocr을 거치지 않은 이미지 저장",
        description = """
            미분류 이미지 folderId = null
            폴더에 이미지 저장 folderId = {folderId}
        """)
    fun saveImageWithoutFolder(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestPart("file") file: MultipartFile,
        @RequestParam("folderId", required = false) folderId: Long?,
    ): ApiResponse<ImageResponseDTO.SaveResponseDTO> {
        val apiResponse = if(folderId==null) imageService.saveUncategorizedImage(userDetails.userId, file)
        else imageService.saveImage(userDetails.userId, file, folderId)
        return apiResponse
    }

    @PatchMapping("/{imageId}")
    @Operation(summary = "이미지를 다른 폴더로 이동")
    fun moveImageToAnotherFolder(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable imageId: Long,
        @RequestBody requestDTO: ImageRequestDTO.MoveImageRequestDTO
    ): ApiResponse<ImageResponseDTO.MoveImageResponseDTO> {
        val apiResponse = imageService.moveImage(userDetails.userId, imageId, requestDTO)
        return apiResponse
    }

    @GetMapping("/{imageId}")
    @Operation(summary = "단일 이미지 조회 API")
    fun getImage(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable imageId: Long,
    ): ApiResponse<ImageResponseDTO.ImageInfoDTO> {
        val apiResponse = imageService.getImageInfo(userDetails.userId, imageId)
        return apiResponse
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "이미지 삭제 API")
    fun deleteImage(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable imageId: Long
    ): ApiResponse<Message> {
        val apiResponse = imageService.deleteImage(userDetails.userId, imageId)
        return apiResponse
    }
}