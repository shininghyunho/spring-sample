package sample.crud.common.response

import java.time.LocalDateTime

class ErrorBody (
    errorCode: ErrorCode,
    message: String? = null
) : CustomBody(status = errorCode.status.value(), message = message) {
    val errorCode = errorCode.code
}