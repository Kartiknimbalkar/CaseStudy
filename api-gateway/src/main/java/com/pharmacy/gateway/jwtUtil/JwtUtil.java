package com.pharmacy.gateway.jwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("secureKeyForSpringBootAppLetItBrEnd")
    private String secretKey;

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey(){
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }

    public String getUsername(Claims claims){
        return claims.getSubject();
    }

    public String getRoles(Claims claims){
        return claims.get("role", String.class);
    }
}
