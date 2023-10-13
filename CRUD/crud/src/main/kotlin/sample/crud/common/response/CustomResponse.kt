package sample.crud.common.response

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

class CustomResponse (
    private val headers: HttpHeaders? = null,
    private val body: CustomBody,
) {
    fun toResponseEntity() = ResponseEntity
        .status(body.status)
        .headers(headers)
        .body(body)
}