package quickshow.dbms.project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "QuickShowSuperSecretKeyForJWTAuthentication2026";

    private static final long EXPIRATION_TIME =
            1000 * 60 * 60; // 1 hour

    private final SecretKey key;

    public JwtService() {

        this.key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(
            Integer userId,
            String email
    ) {

        Date now = new Date();

        Date expiration =
                new Date(
                        now.getTime() + EXPIRATION_TIME
                );

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    public Integer extractUserId(String token) {

        return extractAllClaims(token)
                .get("userId", Integer.class);
    }

    public boolean isTokenValid(String token) {

        try {

            extractAllClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateAdminToken(
            Integer adminId,
            String email,
            String role
    ) {

        Date now = new Date();

        Date expiration =
                new Date(
                        now.getTime() + EXPIRATION_TIME
                );

        return Jwts.builder()
                .subject(email)
                .claim("adminId", adminId)
                .claim("role", role)
                .claim("accountType", "ADMIN")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}