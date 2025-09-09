package com.keeply.global.api.exception.code

import org.springframework.http.HttpStatus

interface SuccessResultCode {
    /**
 * Indicates whether the result code represents a successful outcome.
 *
 * Default implementation returns `true`; implementors may override to change success semantics.
 *
 * @return `true` if this result code denotes success, otherwise `false`
 */
fun isSuccess() = true
    val status: HttpStatus
}