package com.keeply.api.login.controller

import com.keeply.api.login.dto.KakaoUserInfo
import com.keeply.api.login.dto.LoginResponseDTO
import com.keeply.api.login.service.LoginService
import com.keeply.global.api.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/login")
class LoginController (
    private val loginService: LoginService
) {
    @PostMapping
    @Operation(summary = "로그인 API",
        description =
            "Me객체를 JSON형태의 requestBody로 요청"
    )
    fun loginAndRegister(
        @RequestBody requestDTO: KakaoUserInfo
    ) : ApiResponse<LoginResponseDTO> {
        val apiResponse = loginService.loginAndRegister(requestDTO)
        return apiResponse
    }
}