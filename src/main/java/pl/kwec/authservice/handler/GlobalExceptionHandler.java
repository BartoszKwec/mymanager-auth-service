package pl.kwec.authservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    private static final String ACCESS_DENIED = "Access denied";
    private static final String UNEXPECTED_SERVER_ERROR = "Unexpected server error";
    private static final String UNEXPECTED_EXCEPTION_OCCURRED = "Unexpected exception occurred";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(final IllegalArgumentException ex,
            final WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(final BadCredentialsException ex, final WebRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, INVALID_EMAIL_OR_PASSWORD, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(final AccessDeniedException ex, final WebRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(final Exception ex, final WebRequest request) {
        logger.error(UNEXPECTED_EXCEPTION_OCCURRED, ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_SERVER_ERROR, request);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }
}
