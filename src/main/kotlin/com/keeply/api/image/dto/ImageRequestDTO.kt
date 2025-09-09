package com.keeply.api.image.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

class ImageRequestDTO {
    data class SaveRequestDTO(
        @Schema(description = """
            OCR후 캐싱 여부
            신규 이미지 -> true
            미분류 이미지 -> false
        """)
        @NotBlank
        val isCached: Boolean,
        @Schema(description = """
            OCR후 캐싱된 이미지 Id
            신규 이미지 -> cachedImageId={cachedImageId}
            미분류 이미지 -> cachedImageId=null
        """)
        val cachedImageId: String? = null,
        @Schema(description = """
            미분류 폴더에서 OCR 후 저장하려는 이미지Id
            신규 이미지 -> imageId = null
            미분류 이미지 -> imageId = {imageId}
        """)
        val imageId: Long? = null,
        @Schema(description = """
            이미지와 함께 저장하려는 텍스트
        """)
        val imageInsight: String? = null,
        @Schema(description = """
            폴더 Id
        """)
        val folderId: Long
    )

    data class MoveImageRequestDTO(
        @Schema(description = "폴더")
        @NotBlank
        val folderId: Long
    )
}
