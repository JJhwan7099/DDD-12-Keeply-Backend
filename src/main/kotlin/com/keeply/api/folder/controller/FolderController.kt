package com.keeply.api.folder.controller

import com.keeply.api.folder.dto.FolderRequestDTO
import com.keeply.api.folder.dto.FolderResponseDTO
import com.keeply.api.folder.service.FolderService
import com.keeply.global.api.ApiResponse
import com.keeply.global.api.dto.Message
import com.keeply.global.api.exception.folder.InvalidFolderIdException
import com.keeply.global.security.CustomUserDetails
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/folders")
@Validated
class FolderController (
    private val folderService: FolderService
) {
    @PostMapping
    @Operation(summary = "폴더 생성 API")
    fun createFolder(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody requestDTO: FolderRequestDTO.CreateRequestDTO
    ): ApiResponse<FolderResponseDTO.Folder> {
        val apiResponse = folderService.createFolder(userDetails.userId, requestDTO)
        return apiResponse
    }

    @GetMapping
    @Operation(summary = "유저별 폴더 목록 조회 검색 API")
    fun getFolders(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false, defaultValue = "updatedAt") sortBy: String,
        @RequestParam(required = false, defaultValue = "desc") orderBy: String,
    ): ApiResponse<FolderResponseDTO.FolderList> {
        val requestDTO = FolderRequestDTO.GetFoldersRequestDTO(
            keyword = keyword,
            sortBy = sortBy,
            orderBy = orderBy
        )
        val apiResponse = folderService.getFolders(userDetails.userId, requestDTO)
        return apiResponse
    }

    @GetMapping("/{folderId}")
    @Operation(summary = "folderId로 폴더의 이미지 리스트 검색 API")
    fun getFolderImages(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable(required = true) @Positive folderId: Long
    ): ApiResponse<FolderResponseDTO.FolderImages> {
        val apiResponse = folderService.getFolderImages(userDetails.userId, folderId)
        return apiResponse
    }

    @GetMapping("/uncategorized")
    @Operation(summary = "미분류 이미지 리스트 검색 API")
    fun getUncategorizedImages(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ApiResponse<FolderResponseDTO.FolderImages> {
        val apiResponse = folderService.getUncategorizedImages(userDetails.userId)
        return apiResponse
    }


    @PutMapping("/{folderId}")
    @Operation(summary = "폴더 수정 API")
    fun updateFolder(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable @Positive folderId: Long,
        @RequestBody requestDTO : FolderRequestDTO.UpdateRequestDTO
    ): ApiResponse<FolderResponseDTO.Folder> {
        val apiResponse = folderService.updateFolder(userDetails.userId, folderId, requestDTO)
        return apiResponse
    }

    @DeleteMapping("/{folderId}")
    @Operation(summary = "폴더 삭제 API")
    fun deleteFolder(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable @Positive folderId: Long
    ): ApiResponse<Message> {
        val apiResponse = folderService.deleteFolder(userDetails.userId, folderId)
        return apiResponse
    }
}