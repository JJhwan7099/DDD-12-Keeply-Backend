package com.keeply.api.user.controller

import com.keeply.api.user.dto.UserSettingRequestDTO
import com.keeply.api.user.dto.UserSettingResponseDTO
import com.keeply.api.user.service.UserSettingService
import com.keeply.global.api.ApiResponse
import com.keeply.global.security.CustomUserDetails
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/notification")
class UserSettingController(
    private val userSettingService: UserSettingService
) {
    /**
     * Retrieve the authenticated user's notification settings.
     *
     * @param userDetails The authenticated user's details (provides userId used to look up settings).
     * @return An ApiResponse containing the user's UserSettingResponseDTO with current notification settings.
     */
    @GetMapping
    @Operation(summary = "User 알림설정 정보 조회 API")
    fun getUserSetting(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ApiResponse<UserSettingResponseDTO> {
        val apiResponse = userSettingService.getUserSetting(userDetails.userId)
        return apiResponse
    }

    /**
     * Create or update the authenticated user's notification settings.
     *
     * Applies the provided notification settings for the current user and returns the resulting
     * settings wrapped in an ApiResponse.
     *
     * @param userDetails The current authenticated user (from @AuthenticationPrincipal).
     * @param requestDTO Notification settings to create or update.
     * @return ApiResponse containing the updated UserSettingResponseDTO.
     */
    @PostMapping
    @Operation(summary = "User 알림설정 API")
    fun setUserSetting(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestBody requestDTO: UserSettingRequestDTO
    ): ApiResponse<UserSettingResponseDTO> {
        val apiReponse = userSettingService.setUserSetting(userDetails.userId, requestDTO)
        return apiReponse
    }
}