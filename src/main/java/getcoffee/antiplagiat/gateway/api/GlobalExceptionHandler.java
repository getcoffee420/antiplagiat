package getcoffee.antiplagiat.gateway.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final HttpStatus STATUS_TOO_LARGE = HttpStatus.valueOf(413);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> tooLarge(MaxUploadSizeExceededException ex, HttpServletRequest req) {
        return build(req, STATUS_TOO_LARGE, "File too large");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> downstreamUnavailable(ResourceAccessException ex, HttpServletRequest req) {
        String msg = String.valueOf(ex.getMessage());
        String who = "Downstream service";

        if (msg.contains("http://localhost:8081")) who = "Storage";
        if (msg.contains("http://localhost:8082")) who = "Analysis";

        return build(req, HttpStatus.SERVICE_UNAVAILABLE, who + " unavailable: " + msg);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponse> downstreamError(RestClientResponseException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.BAD_GATEWAY;
        String msg = ex.getResponseBodyAsString();
        return build(req, status, msg);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> badRequest(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, String.valueOf(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> other(Exception ex, HttpServletRequest req) {
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, String.valueOf(ex.getMessage()));
    }

    private ResponseEntity<ErrorResponse> build(HttpServletRequest req, HttpStatus status, String message) {
        String safeMessage = (message.isBlank() ? status.getReasonPhrase() : message);

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                safeMessage,
                req.getRequestURI()
        );

        return ResponseEntity.status(status).body(body);
    }
}
