package pl.kwec.authservice.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.kwec.authservice.user.Role;
import pl.kwec.authservice.user.User;
import pl.kwec.authservice.user.UserRepository;

@Service
@AllArgsConstructor
public class AuthService {

    private static final String EMAIL_ALREADY_REGISTERED = "Email already registered: %s";

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

        return jwtService.generateToken(email, null);
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
