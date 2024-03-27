package com.pbp.authservice.utils;

import com.pbp.authservice.dto.UserDto;
import com.pbp.authservice.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private String jwtExpiration;

    private SecretKey key;

    @PostConstruct
    private void initKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(UserDto user, Set<Role> roles, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("roles", roles);

        long expMillis = "ACCESS".equalsIgnoreCase(tokenType)
                ? Long.parseLong(jwtExpiration) * 1000
                : Long.parseLong(jwtExpiration) * 1000 * 5;

        final Date now = new Date();
        final Date exp = new Date(now.getTime() + expMillis);

        return Jwts.builder()
                .claims(claims)
                .subject(claims.get("id").toString())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

//    public boolean validateJwtToken(String authToken) {
//        try {
//            Jwts.parser().verifyWith(key).build().parse(authToken);
//            return true;
//        } catch (MalformedJwtException e) {
//            log.error("Invalid JWT token: {}", e.getMessage());
//        } catch (ExpiredJwtException e) {
//            log.error("JWT token is expired: {}", e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            log.error("JWT token is unsupported: {}", e.getMessage());
//        } catch (IllegalArgumentException e) {
//            log.error("JWT claims string is empty: {}", e.getMessage());
//        }
//
//        return false;
//    }

}
