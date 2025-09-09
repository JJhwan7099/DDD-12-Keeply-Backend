package com.keeply.api.image.controller

import com.keeply.api.image.dto.ImageRequestDTO
import com.keeply.api.image.dto.ImageResponseDTO
import com.keeply.api.image.service.ImageService
import com.keeply.global.api.ApiResponse
import com.keeply.global.api.dto.Message
import com.keeply.global.security.CustomUserDetails
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
@Validated
class ImageController (
    private val imageService: ImageService
) {
    /**
     * Saves an image and assigns it to a folder.
     *
     * If requestDTO.isCached is true the request represents a newly OCR-processed cached image and requires a `cachedImageId`.
     * If false the request represents an image OCR'd from the uncategorized set and requires an `imageId`.
     * The authenticated user's id (from the security principal) is used as the owner for the saved image.
     *
     * @param requestDTO Save request data. Must be valid; when `isCached` is true include `cachedImageId`, otherwise include `imageId`.
     * @return ApiResponse wrapping the saved image information (SaveResponseDTO).
     */
    @PostMapping
    @Operation(summary = "이미지 저장(폴더o)",
        description =
        "신규로 OCR을 거친 이미지는 cachedImageId가 필요," +
                "미분류이미지에서 OCR을 거친 이미지는 imageId가 필요"
    )
    fun saveImage(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody requestDTO: ImageRequestDTO.SaveRequestDTO
    ): ApiResponse<ImageResponseDTO.SaveResponseDTO> {
        val apiResponse = if (requestDTO.isCached) {
            imageService.saveCachedImage(userDetails.userId, requestDTO)
        } else {
            imageService.setFolderOfImage(userDetails.userId, requestDTO)
        }
        return apiResponse
    }
    /**
     * Saves an uploaded image that has not gone through OCR.
     *
     * If `folderId` is null the image is stored as uncategorized; otherwise the image is saved into the specified folder.
     *
     * @param file The image file to save (multipart). Must not be null.
     * @param folderId Optional target folder ID; when present it must be positive.
     * @return An ApiResponse wrapping the saved image information (SaveResponseDTO).
     */
    @PostMapping("/save")
    @Operation(summary = "ocr을 거치지 않은 이미지 저장",
        description = """
            미분류 이미지 folderId = null
            폴더에 이미지 저장 folderId = {folderId}
        """)
    fun saveImageWithoutFolder(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestPart("file") @NotNull file: MultipartFile,
        @RequestParam("folderId", required = false) @Positive folderId: Long?,
    ): ApiResponse<ImageResponseDTO.SaveResponseDTO> {
        val apiResponse = if(folderId==null) imageService.saveUncategorizedImage(userDetails.userId, file)
        else imageService.saveImage(userDetails.userId, file, folderId)
        return apiResponse
    }

    /**
     * Moves an image to another folder for the authenticated user.
     *
     * Moves the image identified by [imageId] to the destination specified in [requestDTO].
     *
     * @param imageId Positive id of the image to move.
     * @param requestDTO Contains the target folder information for the move operation.
     * @return ApiResponse wrapping ImageResponseDTO.MoveImageResponseDTO with the result of the move.
     */
    @PatchMapping("/{imageId}")
    @Operation(summary = "이미지를 다른 폴더로 이동")
    fun moveImageToAnotherFolder(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable @Positive imageId: Long,
        @RequestBody requestDTO: ImageRequestDTO.MoveImageRequestDTO
    ): ApiResponse<ImageResponseDTO.MoveImageResponseDTO> {
        val apiResponse = imageService.moveImage(userDetails.userId, imageId, requestDTO)
        return apiResponse
    }

    /**
     * Retrieves information for a single image.
     *
     * @param imageId The positive identifier of the image to fetch.
     * @return ApiResponse wrapping ImageResponseDTO.ImageInfoDTO containing the image details.
     */
    @GetMapping("/{imageId}")
    @Operation(summary = "단일 이미지 조회 API")
    fun getImage(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable @Positive imageId: Long,
    ): ApiResponse<ImageResponseDTO.ImageInfoDTO> {
        val apiResponse = imageService.getImageInfo(userDetails.userId, imageId)
        return apiResponse
    }

    /**
     * Deletes an image owned by the authenticated user.
     *
     * Calls the image service to remove the image identified by [imageId] and returns an ApiResponse
     * containing a Message with the operation result.
     *
     * @param imageId ID of the image to delete; must be positive.
     * @return ApiResponse<Message> containing a success or failure message for the deletion.
     */
    @DeleteMapping("/{imageId}")
    @Operation(summary = "이미지 삭제 API")
    fun deleteImage(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable @Positive imageId: Long
    ): ApiResponse<Message> {
        val apiResponse = imageService.deleteImage(userDetails.userId, imageId)
        return apiResponse
    }
}