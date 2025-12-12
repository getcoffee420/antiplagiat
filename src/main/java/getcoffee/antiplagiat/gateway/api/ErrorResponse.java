import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ErrorResponse(
        @NotNull Instant timestamp,
        int status,
        @NotNull String error,
        @NotNull String message,
        @NotNull String path
) {}
