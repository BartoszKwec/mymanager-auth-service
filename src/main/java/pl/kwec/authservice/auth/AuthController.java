package pl.kwec.authservice.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kwec.authservice.user.User;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String USER_REGISTERED = "User registered: %s";
    private static final String LOGIN_SUCCESSFUL_TOKEN = "Login successful. Token: %s";

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody final RegisterRequest request) {
        final User user = authService.registerUser(request.email(), request.password(), request.role());
        return ResponseEntity.ok(String.format(USER_REGISTERED, user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody final LoginRequest request) {
        final String token = authService.loginUser(request.email(), request.password());
        return ResponseEntity.ok(String.format(LOGIN_SUCCESSFUL_TOKEN, token));
    }
}