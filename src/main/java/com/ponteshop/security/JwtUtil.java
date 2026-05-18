package com.ponteshop.security;

import com.ponteshop.config.JwtProperties;
import com.ponteshop.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = buildKey(jwtProperties.secret());
    }

    public String generateToken(UUID userId, String email, UserRole role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtProperties.expiration());

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(exp)
            .claim("userId", userId.toString())
            .claim("email", email)
            .claim("role", role.name())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
    }

    private static Key buildKey(String secret) {
        if (secret == null) throw new IllegalArgumentException("jwt.secret is required");
        try {
            byte[] decoded = Decoders.BASE64.decode(secret);
            if (decoded.length >= 32) return Keys.hmacShaKeyFor(decoded);
        } catch (Exception ignored) {
        }
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(raw.length >= 32 ? raw : pad(raw, 32));
    }

    private static byte[] pad(byte[] raw, int min) {
        byte[] out = new byte[min];
        for (int i = 0; i < out.length; i++) {
            out[i] = raw[i % raw.length];
        }
        return out;
    }
}

