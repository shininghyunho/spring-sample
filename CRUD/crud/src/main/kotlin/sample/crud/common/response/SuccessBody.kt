package sample.crud.common.response

import org.springframework.http.HttpStatus

class SuccessBody(
    val data: Any? = null,
    status: HttpStatus,
    message: String?
) : CustomBody(status = status.value(), message = message)