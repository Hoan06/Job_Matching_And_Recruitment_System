package project.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
            log.info("Hệ thống không hỗ trợ jwt");
            throw new RuntimeException("Hệ thống không hỗ trợ jwt", e);
        }catch (ExpiredJwtException e){
            log.info("Chuỗi jwt hết hạn");
            throw new RuntimeException("Chuỗi jwt hết hạn", e);
        }catch (MalformedJwtException e){
            log.info("Chuỗi jwt sai định dạng");
            throw new RuntimeException("Chuỗi jwt sai định dạng", e);
        }catch (SignatureException e){
            log.info("Sai chữ kí JWT");
            throw new RuntimeException("Sai chữ kí JWT", e);
        }catch (IllegalArgumentException e){
            log.info("Chuỗi JWT rỗng");
            throw new RuntimeException("Chuỗi JWT rỗng", e);
        }catch (JwtException e){
            log.info("Lỗi xác thực JWT");
            throw new RuntimeException("Lỗi xác thực JWT", e);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        }catch (Exception e){
            log.error("getEmailFromToken", e);
            throw new RuntimeException(e);
        }
    }

    public LocalDateTime getExpirationDateFromToken(String token) {
        try{
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Date expiredDate = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return expiredDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        }catch (ExpiredJwtException e){
            return e.getClaims()
                    .getExpiration()
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (Exception e){
            log.error("getExpirationDateFromToken", e);
            throw new RuntimeException("Không thể trích xuất thời gian hết hạn từ token " + e);
        }
    }
}
