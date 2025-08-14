package com.example.mediaapi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class StreamTokenService {

    @Value("${app.stream.secret}")
    private String streamSecret;

    @Value("${app.stream.expiration-ms}")
    private long streamExpirationMs;

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(streamSecret.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateStreamToken(Long mediaId, String requesterIp) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + streamExpirationMs);
        return Jwts.builder()
                .setClaims(Map.of("mediaId", mediaId, "ip", requesterIp, "type", "stream"))
                .setSubject("stream:" + mediaId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public io.jsonwebtoken.Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }

    public long getExpirationMs() { return streamExpirationMs; }
}
