package project.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class JWTProvider {
    @Value("${jwt-secret}")
    private String jwtSecret;
    @Value("${jwt-expired}")
    private Long jwtExpired;

    @Value("${jwt-reset-secret}")
    private String jwtResetSecret;

    public String generateToken(String email) {
        try {
            Date today = new Date();
            Date expiredJWT = new Date(today.getTime() + jwtExpired);
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .subject(email)
                    .issuedAt(today)
                    .expiration(expiredJWT)
                    .signWith(key)
                    .compact();
        } catch (Exception e){
            log.error("generateToken", e);
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (UnsupportedJwtException e) {
            log.warn("Hệ thống không hỗ trợ JWT: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Chuỗi JWT đã hết hạn: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Chuỗi JWT sai định dạng: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Sai chữ ký JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Chuỗi JWT rỗng hoặc null: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("Lỗi xác thực JWT chung: {}", e.getMessage());
        }
        return false;
    }

    public String getEmailFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (Exception e){
            log.error("getEmailFromToken error: ", e);
            return null;
        }
    }

    public LocalDateTime getExpirationDateFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Date expiredDate = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getExpiration();
            return expiredDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            log.error("getExpirationDateFromToken error: ", e);
            return null;
        }
    }

    public String generateResetPasswordToken(String email) {
        try {
            Date today = new Date();
            long resetExpiry = 5 * 60 * 1000;
            Date expiredJWT = new Date(today.getTime() + resetExpiry);
            SecretKey key = Keys.hmacShaKeyFor(jwtResetSecret.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .subject(email)
                    .issuedAt(today)
                    .expiration(expiredJWT)
                    .signWith(key)
                    .compact();
        } catch (Exception e){
            log.error("generateResetPasswordToken", e);
            throw new RuntimeException(e);
        }
    }

    public boolean validateResetToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtResetSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token reset password không hợp lệ hoặc hết hạn: {}", e.getMessage());
            return false;
        }
    }

    public String getEmailFromResetToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtResetSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            log.error("getEmailFromResetToken error: ", e);
            return null;
        }
    }
}