package sample.crud.common.response

import java.time.LocalDateTime

class ErrorBody (
    errorCode: ErrorCode,
) : CustomBody(status = errorCode.status.value(), message = errorCode.message)