package getcoffee.antiplagiat.gateway.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> tooLarge(HttpServletRequest req) {
        return build(req, HttpStatus.PAYLOAD_TOO_LARGE, "File too large");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> downstreamUnavailable(ResourceAccessException ex, HttpServletRequest req) {
        String msg = ex.getMessage();
        String who = "Downstream service";

        if (msg != null) {
            if (msg.contains("http://localhost:8081")) who = "Storage";
            if (msg.contains("http://localhost:8082")) who = "Analysis";
        }

        return build(req, HttpStatus.SERVICE_UNAVAILABLE, who + " unavailable: " + msg);
    }


    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponse> downstreamError(RestClientResponseException ex, HttpServletRequest req) {

        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.BAD_GATEWAY;

        String msg = ex.getResponseBodyAsString();
        if (msg == null || msg.isBlank()) {
            msg = ex.getMessage();
        }
        return build(req, status, msg);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> other(Exception ex, HttpServletRequest req) {
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpServletRequest req, HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> badRequest(Exception ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

}
