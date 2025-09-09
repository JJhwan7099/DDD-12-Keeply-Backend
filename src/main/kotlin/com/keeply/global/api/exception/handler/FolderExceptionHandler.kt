package com.keeply.global.api.exception.handler

import com.keeply.global.api.ApiResponse
import com.keeply.global.api.exception.folder.FolderNotFoundException
import com.keeply.global.api.exception.folder.InvalidFolderIdException
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice(basePackages = ["com.keeply.api.folder"])
class FolderExceptionHandler: BaseExceptionHandler() {

    @ExceptionHandler(FolderNotFoundException::class)
    fun handleFolderNotFoundException(e: FolderNotFoundException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    @ExceptionHandler(InvalidFolderIdException::class)
    fun handlerInvalidFolderIdException(e: InvalidFolderIdException)
            : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }
}