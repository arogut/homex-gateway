package com.arogut.homex.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final String secret;

    private final String expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") String expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String generateToken(String deviceId, Map<String, Object> claims) {
        long expirationTimeLong = Long.parseLong(expiration); //in second

        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(deviceId)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(new SecretKeySpec(Base64.getEncoder().encode(secret.getBytes()),
                        SignatureAlgorithm.HS512.getJcaName()))
                .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(new SecretKeySpec(Base64.getEncoder().encode(secret.getBytes()), SignatureAlgorithm.HS512.getJcaName()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token)
                .getExpiration();
    }

    public boolean isTokenExpired(String token) {
        final Date exp = getExpirationDateFromToken(token);
        return exp.before(new Date());
    }

    public String getExpiration() {
        return expiration;
    }
}
