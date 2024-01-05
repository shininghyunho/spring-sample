package sample.crud.common.response

class ErrorBody (
    errorCode: ErrorCode,
    message: String? = null
) : CustomBody(status = errorCode.status.value(), message = message) {
    val errorCode = errorCode.code
}