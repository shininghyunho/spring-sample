package sample.crud.common.response.error

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import sample.crud.common.response.CustomBody
import sample.crud.common.response.ErrorBody
import sample.crud.common.response.ErrorCode

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): CustomBody {
        return ErrorBody (
            errorCode = e.errorCode,
            message = e.message
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): CustomBody {
        return ErrorBody (
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR,
            message = e.message
        )
    }
}