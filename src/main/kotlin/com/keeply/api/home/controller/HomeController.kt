package com.keeply.api.home.controller

import com.keeply.api.home.dto.HomeResponseDTO
import com.keeply.api.home.service.HomeService
import com.keeply.global.api.dto.ApiResponse
import com.keeply.global.security.CustomUserDetails
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/home")
class HomeController(
    private val homeService: HomeService,
) {
    @GetMapping
    @Operation(summary = "홈화면 요청 API")
    fun getHome(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<HomeResponseDTO>{
        val apiResponse = homeService.getHome(userDetails.userId)
        return apiResponse
    }
}