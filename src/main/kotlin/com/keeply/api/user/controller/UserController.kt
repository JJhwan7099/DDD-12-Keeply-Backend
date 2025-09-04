package com.keeply.api.user.controller

import com.keeply.api.user.dto.UserInfoDTO
import com.keeply.api.user.service.UserService
import com.keeply.global.api.dto.ApiResponse
import com.keeply.global.api.dto.Message
import com.keeply.global.security.CustomUserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
) {
    @GetMapping
    fun getUserInfo(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ApiResponse<UserInfoDTO> {
        val apiResponse = userService.getUserInfo(userDetails.userId)
        return apiResponse
    }

    @DeleteMapping
    fun deleteUser(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ApiResponse<Message> {
        val apiResponse = userService.deleteUser(userDetails.userId)
        return apiResponse
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ApiResponse<Message> {
        val apiResponse = userService.logout(userDetails.userId)
        return apiResponse
    }
}