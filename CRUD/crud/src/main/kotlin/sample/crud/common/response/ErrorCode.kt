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
    // USER
    NOT_EXISTED_USER(status = HttpStatus.BAD_REQUEST, code = "U001", message = "존재하지 않는 유저입니다."),
    DUPLICATED_EMAIL(status = HttpStatus.BAD_REQUEST, code = "U002", message = "이미 존재하는 이메일입니다."),
    DUPLICATED_NICKNAME(status = HttpStatus.BAD_REQUEST, code = "U003", message = "이미 존재하는 닉네임입니다."),

    // ITEM
    NOT_EXISTED_ITEM(status = HttpStatus.BAD_REQUEST, code = "I001", message = "존재하지 않는 아이템입니다."),
    DUPLICATED_ITEM_NAME(status = HttpStatus.BAD_REQUEST, code = "I002", message = "이미 존재하는 아이템 이름입니다."),
    NOT_ENOUGH_ITEM_QUANTITY(status = HttpStatus.BAD_REQUEST, code = "I003", message = "아이템의 재고가 부족합니다."),

    // ORDER
    NOT_EXISTED_ORDER(status = HttpStatus.BAD_REQUEST, code = "O001", message = "존재하지 않는 주문입니다."),
    NOT_EXISTED_ORDER_ITEM(status = HttpStatus.BAD_REQUEST, code = "O002", message = "존재하지 않는 주문 아이템입니다."),

    /* 500 SERVER_ERROR */
    INTERNAL_SERVER_ERROR(status = HttpStatus.INTERNAL_SERVER_ERROR, code = "S001", message = "서버 내부 에러"),
}