package com.codepilot.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    // 使用更安全的密钥生成方式
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L; // 7天
    private static final long REFRESH_TIME = 24 * 60 * 60 * 1000L; // 24小时

    /**
     * 创建JWT token
     * 
     * @param claims token中要包含的数据
     * @return JWT token字符串
     */
    public static String createToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 解析JWT token
     * 
     * @param token JWT token字符串
     * @return Claims对象，包含token中的数据
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("Token已过期: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("解析Token失败: {}", e.getMessage());
            throw e;
        }
    }

    public static String generateToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);

            // 检查是否过期
            Date expiration = claims.getExpiration();
            Date now = new Date();

            // 如果token已经过期，返回false
            if (expiration.before(now)) {
                return false;
            }

            // 如果token距离过期时间不足24小时，可以考虑刷新token
            long timeToExpire = expiration.getTime() - now.getTime();
            if (timeToExpire < REFRESH_TIME) {
                log.info("Token即将过期，建议刷新");
            }

            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 刷新token
     */
    public static String refreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            claims.setIssuedAt(new Date());
            claims.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME));

            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(SECRET_KEY)
                    .compact();
        } catch (Exception e) {
            log.error("刷新Token失败: {}", e.getMessage());
            throw e;
        }
    }
}