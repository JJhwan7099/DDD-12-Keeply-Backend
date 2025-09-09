package com.keeply.global.api.exception.code

import org.springframework.http.HttpStatus

interface ErrorResultCode {
    /**
 * Indicates whether this result code represents a successful outcome.
 *
 * Default implementation returns `false`. Implementations that represent a success should override
 * this method to return `true`.
 *
 * @return `true` if the result code denotes success, otherwise `false`.
 */
fun isSuccess() = false
    val status: HttpStatus
    val message: String
}