package edu.icet.paymentservice.security;

import edu.icet.paymentservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    private static final long EXPIRATION_TIME_IN_MILLISEC = 1000L * 60L * 60L * 24L * 30L * 6L;
    private SecretKey key;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @PostConstruct
    private void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String generateToken(User user) {
        String username = user.getEmail();
        return generateToken(username);
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISEC))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            return extractClaims(token, Claims::getSubject);
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage(), e);
            return null;
        }
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claimsTFunction.apply(claims);
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid token", e);
        }
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
