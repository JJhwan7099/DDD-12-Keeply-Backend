package com.keeply.api.login.controller

import com.keeply.api.login.dto.KakaoUserInfo
import com.keeply.api.login.dto.LoginResponseDTO
import com.keeply.api.login.service.LoginService
import com.keeply.global.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/login")
class LoginController (
    private val loginService: LoginService
) {
    /**
     * Handles login (and registration) requests submitted as a Kakao user JSON payload.
     *
     * Validates the incoming KakaoUserInfo request body and delegates authentication/registration to the LoginService,
     * returning the service's ApiResponse containing a LoginResponseDTO.
     *
     * @param requestDTO The validated Kakao user information parsed from the request body.
     * @return ApiResponse wrapping the LoginResponseDTO produced by the login service.
     */
    @PostMapping
    @Operation(summary = "로그인 API",
        description =
            "Me객체를 JSON형태의 requestBody로 요청"
    )
    fun loginAndRegister(
        @Valid @RequestBody requestDTO: KakaoUserInfo
    ) : ApiResponse<LoginResponseDTO> {
        val apiResponse = loginService.loginAndRegister(requestDTO)
        return apiResponse
    }
}