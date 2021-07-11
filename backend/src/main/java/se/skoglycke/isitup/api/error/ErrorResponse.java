package se.skoglycke.isitup.api.error;

import se.skoglycke.isitup.util.ValueObject;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class ErrorResponse extends ValueObject {

    private final int httpCode;
    private final String httpStatus;
    private final int errorCode;
    private final String message;

    public ErrorResponse(int httpCode, String httpStatus, int errorCode, String message) {
        this.httpCode = httpCode;
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ErrorResponse badRequest(int errorCode, String errorMessage) {
        return new ErrorResponse(
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                errorCode,
                errorMessage
        );
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
