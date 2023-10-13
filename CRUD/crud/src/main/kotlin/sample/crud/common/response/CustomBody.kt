package sample.crud.common.response

import java.time.LocalDateTime

abstract class CustomBody (
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val message: String?,
)