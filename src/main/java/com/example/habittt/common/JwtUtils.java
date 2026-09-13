package com.example.habittt.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtUtils {
    private static final SecretKey KEY = Keys.hmacShaKeyFor("habittt-secret-key-for-jwt-token-generation".getBytes(StandardCharsets.UTF_8));

    public static Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未登录或 Token 格式错误");
        }
        String cleanToken = token.replace("Bearer ", "");
        try {
            String subject = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(cleanToken)
                    .getPayload()
                    .getSubject();
            return Long.parseLong(subject);
        } catch (Exception e) {
            throw new RuntimeException("Token 无效或已过期");
        }
    }

    public static Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未登录或 Token 格式错误");
        }

        String cleanToken = token.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(cleanToken)
                    .getPayload();

            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException("Token 无效或已过期");
        }
    }

}