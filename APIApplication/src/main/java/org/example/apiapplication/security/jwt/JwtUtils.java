package org.example.apiapplication.security.jwt;

import io.jsonwebtoken.*;
import org.example.apiapplication.exceptions.auth.TokenRefreshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.access-token-secret}")
    private String accessTokenSecret;

    @Value("${app.jwt.refresh-token-secret}")
    private String refreshTokenSecret;

    @Value("${app.jwt.expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    public String generateAccessToken(String email, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return generateTokenFromEmail(email, claims, accessTokenSecret, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String email) {
        return generateTokenFromEmail(email, new HashMap<>(), refreshTokenSecret, refreshTokenExpirationMs);
    }

    public String generateTokenFromEmail(String email, Map<String, Object> claims, String secret, long expirationMs) {
        return Jwts.builder().setSubject(email).setIssuedAt(new Date())
                .addClaims(claims)
                .setExpiration(new Date((new Date()).getTime() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getEmailFromAccessToken(String token) {
        return getEmailFromToken(token, accessTokenSecret);
    }

    public String getEmailFromRefreshToken(String token) {
        try {
            return getEmailFromToken(token, refreshTokenSecret);
        } catch (Exception e) {
            throw new TokenRefreshException(token, "Refresh token is invalid! " +
                    "Please sign in again to get new!");
        }
    }

    public String getEmailFromToken(String token, String secret) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, accessTokenSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, refreshTokenSecret);
    }

    private boolean validateToken(String token, String secret) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
