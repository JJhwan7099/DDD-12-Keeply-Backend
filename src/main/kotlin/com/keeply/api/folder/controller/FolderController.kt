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
    /**
     * Create a new folder for the authenticated user.
     *
     * Creates a folder using data from the validated request DTO and associates it with the current user.
     *
     * @param userDetails Authenticated user principal (used to obtain the userId).
     * @param requestDTO Payload containing folder creation data; validated before use.
     * @return ApiResponse containing the created Folder DTO.
     */
    @PostMapping
    @Operation(summary = "폴더 생성 API")
    fun createFolder(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody requestDTO: FolderRequestDTO.CreateRequestDTO
    ): ApiResponse<FolderResponseDTO.Folder> {
        val apiResponse = folderService.createFolder(userDetails.userId, requestDTO)
        return apiResponse
    }

    /**
     * Retrieves the authenticated user's folders, optionally filtered by a search keyword and ordered.
     *
     * Constructs a GetFoldersRequestDTO from the provided query parameters and delegates to FolderService
     * to fetch the folder list for the authenticated user.
     *
     * @param keyword Optional search keyword to filter folders; if null, no keyword filtering is applied.
     * @param sortBy Field name to sort by (default: "updatedAt").
     * @param orderBy Sort direction, either "asc" or "desc" (default: "desc").
     * @return ApiResponse containing the user's FolderList DTO.
     */
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

    /**
     * Retrieve images for a specific folder or the uncategorized images list.
     *
     * If `folderId` equals `"uncategorized"`, returns uncategorized images for the authenticated user.
     * Otherwise `folderId` must be a numeric string representing a positive folder ID; the numeric ID
     * is parsed and used to return the folder's images.
     *
     * @param folderId Either the literal `"uncategorized"` or a numeric folder ID as a string.
     * @return An ApiResponse wrapping FolderResponseDTO.FolderImages for the requested folder or uncategorized set.
     * @throws InvalidFolderIdException If `folderId` is neither `"uncategorized"` nor a valid numeric ID.
     */
    @GetMapping("/{folderId}")
    @Operation(summary = "folderId로 폴더의 이미지 리스트 검색, 미분류 이미지 리스트 검색 API",
        description = "폴더 검색시 folderId, 미분류 이미지 검색시, folderId = \"uncategorized\"")
    fun getFolderImages(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable @Positive folderId: String
    ): ApiResponse<FolderResponseDTO.FolderImages> {
        val apiResponse = if (folderId == "uncategorized") {
            folderService.getUncategorizedImages(userDetails.userId)
        } else {
            val parsedFolderId = folderId.toLongOrNull()
                ?: throw InvalidFolderIdException()
            folderService.getFolderImages(userDetails.userId, parsedFolderId)
        }
        return apiResponse
    }


    /**
     * Updates an existing folder for the authenticated user.
     *
     * @param folderId ID of the folder to update; must be positive.
     * @param requestDTO Fields to update on the folder (e.g., name, description).
     * @return ApiResponse containing the updated Folder response DTO.
     */
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

    /**
     * Deletes the specified folder for the authenticated user.
     *
     * Deletes the folder identified by `folderId` (and any service-managed associations) belonging to the authenticated user.
     *
     * @param folderId Positive ID of the folder to delete.
     * @return ApiResponse containing a Message describing the result of the deletion.
     */
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