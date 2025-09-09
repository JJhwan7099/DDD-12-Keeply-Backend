package com.keeply.api.user.controller

import com.keeply.api.user.dto.UserInfoDTO
import com.keeply.api.user.service.UserService
import com.keeply.global.api.ApiResponse
import com.keeply.global.api.dto.Message
import com.keeply.global.security.CustomUserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
) {
    /**
     * Retrieves information for the currently authenticated user.
     *
     * @param userDetails The authenticated principal; this method uses `userDetails.userId` to fetch the profile.
     * @return ApiResponse containing the user's UserInfoDTO.
     */
    @GetMapping
    fun getUserInfo(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ApiResponse<UserInfoDTO> {
        val apiResponse = userService.getUserInfo(userDetails.userId)
        return apiResponse
    }

    /**
     * Deletes the authenticated user's account.
     *
     * Delegates account deletion to UserService using the authenticated user's ID and returns
     * the service's ApiResponse containing a status message.
     *
     * @param userDetails The authenticated user's details (injected from the security principal).
     * @return ApiResponse containing a Message describing the outcome of the deletion.
     */
    @DeleteMapping
    fun deleteUser(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ApiResponse<Message> {
        val apiResponse = userService.deleteUser(userDetails.userId)
        return apiResponse
    }

    /**
     * Logs out the authenticated user and returns an API response containing a status message.
     *
     * @param userDetails The authenticated user's details (provided by Spring Security); only the `userId` is used.
     * @return ApiResponse containing a Message that conveys the outcome of the logout operation.
     */
    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ApiResponse<Message> {
        val apiResponse = userService.logout(userDetails.userId)
        return apiResponse
    }
}