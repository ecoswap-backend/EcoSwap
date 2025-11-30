package com.ecoswap.ecoswap.security; // 💡 ASUMIMOS ESTE PAQUETE

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts; // <--- Importación de Jwts
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.*; // <-- Importación para excepciones

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}") 
    private String jwtSecret;

    @Value("${jwt.expiration.ms}") 
    private int jwtExpirationInMs;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Generar el Token JWT
    public String generarToken(String userMail) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userMail)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    // Obtener el email del token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder() // <-- Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Validar el Token JWT
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken); // <-- Jwts.parserBuilder()
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException | SignatureException ex) {
            // Aquí se pueden loguear los errores
        }
        return false;
    }
}