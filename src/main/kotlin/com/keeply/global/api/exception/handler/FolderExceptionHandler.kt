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

    /**
     * Handles FolderNotFoundException and converts it into a standardized error response.
     *
     * @param e The thrown FolderNotFoundException; its `errorResultCode` is used to build the response.
     * @return ResponseEntity containing an ApiResponse with no body and the error result produced from the exception.
     */
    @ExceptionHandler(FolderNotFoundException::class)
    fun handleFolderNotFoundException(e: FolderNotFoundException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    /**
     * Handles InvalidFolderIdException thrown by controllers in the folder API package.
     *
     * Builds an error response using the exception's `errorResultCode` and returns it
     * as a ResponseEntity containing an ApiResponse with no data.
     *
     * @param e The caught InvalidFolderIdException whose `errorResultCode` is used to construct the response.
     * @return ResponseEntity containing an ApiResponse<Nothing> representing the error.
     */
    @ExceptionHandler(InvalidFolderIdException::class)
    fun handlerInvalidFolderIdException(e: InvalidFolderIdException)
            : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }
}