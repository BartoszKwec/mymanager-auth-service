package pl.kwec.authservice.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.kwec.authservice.user.Role;
import pl.kwec.authservice.user.User;
import pl.kwec.authservice.user.UserRepository;

import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {

    private static final String EMAIL_ALREADY_REGISTERED = "Email already registered: %s";
    private static final String USER_NOT_FOUND = "User not found: ";

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public User registerUser(final String email, final String password, final String roleStr) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new IllegalArgumentException(String.format(EMAIL_ALREADY_REGISTERED, email));
                });

        final Role role = parseRoleOrDefault(roleStr);

        final User user = User.builder()
                .email(email)
                .password(jwtService.encodePassword(password))
                .role(role)
                .build();

        return userRepository.save(user);
    }

    public String loginUser(final String email, final String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND + email));

        final Map<String, String> claims = Map.of(
                "userId", String.valueOf(user.getId()),
                "role", user.getRole().name()
        );

        return jwtService.generateToken(email, claims);
    }

    private Role parseRoleOrDefault(final String roleStr) {
        if (roleStr == null) return Role.USER;
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }
}
