package sample.crud.common.response.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import sample.crud.common.response.CustomBody
import sample.crud.common.response.CustomResponse
import sample.crud.common.response.ErrorBody
import sample.crud.common.response.ErrorCode

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<CustomBody> {
        return CustomResponse(
            body = ErrorBody(
                errorCode = e.errorCode,
                message = e.message
            )
        ).toResponseEntity()
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<CustomBody> {
        return CustomResponse(
            body = ErrorBody(
                errorCode = ErrorCode.INTERNAL_SERVER_ERROR,
                message = e.message
            )
        ).toResponseEntity()
    }
}