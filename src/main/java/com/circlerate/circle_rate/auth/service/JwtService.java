package com.circlerate.circle_rate.auth.service;

import com.circlerate.circle_rate.auth.model.AccessToken;
import com.circlerate.circle_rate.auth.model.RefreshToken;
import com.circlerate.circle_rate.auth.model.Role;
import com.circlerate.circle_rate.auth.repository.RefreshTokenRepository;
import com.circlerate.circle_rate.common.constants.GlobalConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    private SecretKey signingKey;
    private final RefreshTokenRepository refreshTokenRepository;

    
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30 minutes
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 days

    @PostConstruct
    public void initKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractTokenId(String refreshToken) {
        return extractAllClaims(refreshToken).get("tokenId", String.class);
    }
    public String extractRole(String token) {
        return (String) extractAllClaims(token).get(GlobalConstants.ROLE_CLAIM);
    }

    public boolean isTokenValid(String token) {
        Claims claims = extractAllClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.after(new Date());
    }

    public AccessToken generateAccessToken(String email, Role role) {
        Date issuedAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION);
        String token = Jwts.builder()
                .subject(email)
                .claim(GlobalConstants.ROLE_CLAIM, role.name())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
        return new AccessToken(email, role, token, issuedAt, expiration);
    }

    public RefreshToken generateRefreshToken(String email) {
        Date issuedAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION);
        RefreshToken refreshToken = new RefreshToken(email, issuedAt, expiration);
        refreshToken = refreshTokenRepository.save(refreshToken); //saved token to fetch _id
        String token = Jwts.builder()
                .subject(email)
                .issuedAt(issuedAt)
                .claim("tokenId", refreshToken.getId())
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
        refreshToken.setToken(token);
        refreshToken = refreshTokenRepository.save(refreshToken); //saves token with token
        return refreshToken;
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
