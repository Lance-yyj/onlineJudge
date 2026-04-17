package com.edu.cqie.oj.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和验证JWT Token
 */
public class JwtUtil {
    
    // 密钥（实际项目中应该从配置文件读取）
    private static final String SECRET_KEY = "onlineJudgeACMSecretKey2026ForJWTTokenGenerationAndValidation";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    
    // Token有效期：24小时
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    
    /**
     * 生成JWT Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public static String generateToken(Integer userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 验证Token并获取Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    public static Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Token无效或已过期");
        }
    }
    
    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public static Integer getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", Integer.class);
    }
    
    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("username", String.class);
    }
    
    /**
     * 检查Token是否过期
     *
     * @param token JWT Token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
