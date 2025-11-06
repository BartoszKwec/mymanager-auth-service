package pl.kwec.authservice.auth;

public record RegisterRequest(String email, String password, String role) {
}
