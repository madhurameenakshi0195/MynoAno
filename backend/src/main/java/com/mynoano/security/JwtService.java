package com.mynoano.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long ttl;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long ttl) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.ttl = ttl;
    }

    public String create(Long userId) {
        Date now = new Date();
        return Jwts.builder().subject(String.valueOf(userId)).issuedAt(now)
                .expiration(new Date(now.getTime() + ttl)).signWith(key).compact();
    }

    public Long parse(String token) {
        return Long.valueOf(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());
    }
}
