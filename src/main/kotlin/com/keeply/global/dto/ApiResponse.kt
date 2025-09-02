package com.keeply.global.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class ApiResponse<T>(
    val success: Boolean,
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    val reason: String? = null,
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    val response: T? = null
) {

}