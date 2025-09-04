package com.keeply.api.login.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class LoginResponseDTO(
    @JsonIgnore
    val accessToken: String,
    val refreshToken: String
)
