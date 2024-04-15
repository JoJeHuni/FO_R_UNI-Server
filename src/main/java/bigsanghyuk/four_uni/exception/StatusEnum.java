package bigsanghyuk.four_uni.exception;

import lombok.Getter;

@Getter
public enum StatusEnum {

    USER_NOT_FOUND(404,"USER_NOT_FOUND"),
    EMAIL_DUPLICATE(403, "EMAIL_DUPLICATE"),
    PASSWORD_MISMATCH(400, "PASSWORD_MISMATCH"),
    PASSWORD_INVALID(400, "PASSWORD_INVALID"),
    POST_NOT_FOUND(404,"POST_NOT_FOUND"),
    DEADLINE_NOT_FOUND(404,"DEADLINE_NOT_FOUND"),
    COMMENT_NOT_FOUND(404,"COMMENT_NOT_FOUND"),
    ALREADY_LIKE(403, "ALREADY_LIKE"),
    REASON_NOT_FOUND(404, "REASON_NOT_FOUND"),
    ALREADY_SCRAPPED(403, "ALREADY_SCRAPPED"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    TOKEN_NOT_FOUND(404, "TOKEN_NOT_FOUND"),
    SCRAP_NOT_FOUND(404,"SCRAP_NOT_FOUND"),
    VERIFICATION_MISMATCH(400, "VERIFICATION_MISMATCH"),
    VERIFICATION_EXPIRED(400, "VERIFICATION_EXPIRED"),
    ACCESS_TOKEN_EXPIRED(401, "ACCESS_TOKEN_EXPIRED"),
    WRONG_TYPE_TOKEN(400, "WRONG_TYPE_TOKEN"),
    MALFORMED_TOKEN(400, "MALFORMED_TOKEN"),
    REFRESH_TOKEN_MISMATCH(400, "REFRESH_TOKEN_MISMATCH"),
    SEND_MAIL_FAILED(500, "SEND_MAIL_FAILED"),
    RESTRICTED(403, "RESTRICTED");

    private final int statusCode;
    private final String code;

    StatusEnum(int statusCode, String code) {
        this.statusCode = statusCode;
        this.code = code;
    }
}