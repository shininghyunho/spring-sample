package sample.crud.common.response.error

import sample.crud.common.response.ErrorCode

class CustomException(
    val errorCode: ErrorCode,
    override val message: String? = errorCode.message,
): RuntimeException()