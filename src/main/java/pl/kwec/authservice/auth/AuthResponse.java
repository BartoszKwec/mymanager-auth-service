package pl.kwec.authservice.auth;

public record AuthResponse(
        String token,
        String message
) {
}
