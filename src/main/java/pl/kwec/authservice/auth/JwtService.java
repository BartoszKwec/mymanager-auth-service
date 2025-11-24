package pl.kwec.authservice.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final BCryptPasswordEncoder passwordEncoder;
    private final long jwtExpirationMs;

    public JwtService(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateToken(final String subject, final Map<String, String> claims) {
        var jwtBuilder = JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs));

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
}
