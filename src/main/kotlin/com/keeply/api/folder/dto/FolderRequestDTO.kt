package com.keeply.api.folder.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class FolderRequestDTO{
    data class CreateRequestDTO (
        @Schema(description = "폴더명")
        @NotBlank
        val folderName: String,
        @Schema(description = "폴더 색상 코드")
        @Pattern(
            regexp = "^[0-9a-fA-F]{8}$"
        )
        val folderColor: String
    )
    data class UpdateRequestDTO (
        @Schema(description = "폴더명")
        @NotBlank
        @Size(max = 20, min = 1)
        val folderName: String,
        @Schema(description = "폴더 색상 코드")
        @Pattern(
            regexp = "^[0-9a-fA-F]{8}$"
        )
        val folderColor: String
    )
    data class GetFoldersRequestDTO(
        @Schema(description = "폴더 검색 키워드")
        val keyword: String?,
        @Schema(description = "폴더 정렬 기준(updatedAt/imageCount")
        val sortBy: String,
        @Schema(description = "내림차순(desc)/ 오름차순(asc)")
        val orderBy: String
    )
}