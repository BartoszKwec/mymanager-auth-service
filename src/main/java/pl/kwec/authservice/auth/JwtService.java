package pl.kwec.authservice.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private static final String JWT_SECRET = "supersecretkey1234567890supersecretkey";
    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24h

    private final Algorithm algorithm;
    private final BCryptPasswordEncoder passwordEncoder;

    public JwtService() {
        this.algorithm = Algorithm.HMAC256(JWT_SECRET);
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String generateToken(final String subject, final Map<String, String> claims) {
        var jwtBuilder = JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS));

        if (claims != null) {
            claims.forEach(jwtBuilder::withClaim);
        }

        return jwtBuilder.sign(algorithm);
    }

    public DecodedJWT verifyToken(final String token) throws JWTVerificationException {
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    public String getSubject(final String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT.getSubject();
    }

    public String getClaim(final String token, final String claimName) throws JWTVerificationException {
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT.getClaim(claimName).asString();
    }

    public String validateTokenAndGetUsername(final String token) {
        try {
            return getSubject(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public String encodePassword(final String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matchesPassword(final String rawPassword, final String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
