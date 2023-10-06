package sample.crud.common.response

import org.springframework.http.HttpStatus

/* 200 OK : 정상 */
/* 201 CREATED : 정상, 데이터가 생성됨(POST) */
/* 202 Accepted : 정상, 클라이언트의 요청은 정상적이나 서버가 아직 요청을 완료하지 못했다. */
/* 204 NO CONTENT : 정상, Response Body 가 아예 없는것, 현재는 아무것도 해당 안됨 */

/* 400 BAD_REQUEST : 실패, 클라이언트에서 넘어온 파라미터가 이상함 */
/* 401 UNAUTHORIZED : 인증(UNAUTHENTICATED)되지 않은 사용자 */
/* 403 FORBIDDEN :  권한이 없는 사용자(UNAUTHORIZED) */
/* 404 NOT_FOUND : 실패, 데이터가 있어야 하나 없음 */

/* 500 INTERNAL_SERVER_ERROR : 내부 서버 에러 */
/* 501 Not Implemented : 실패, 없는 리소스 요청 */
enum class ErrorCode (
    val status: HttpStatus,
    val code: String,
    val message: String,
) {
    /* 400 BAD_REQUEST */
    DUPLICATED_VALUE(status = HttpStatus.BAD_REQUEST, code = "B001", message = "중복된 값이 존재합니다."),
    NOT_FOUND(status = HttpStatus.BAD_REQUEST, code = "B002", message = "존재하지 않는 값입니다."),

    /* 500 SERVER_ERROR */
    INTERNAL_SERVER_ERROR(status = HttpStatus.INTERNAL_SERVER_ERROR, code = "S001", message = "서버 내부 에러"),
}