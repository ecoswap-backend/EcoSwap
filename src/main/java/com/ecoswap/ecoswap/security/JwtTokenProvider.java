package com.ecoswap.ecoswap.security;

import io.jsonwebtoken.*; 
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generarToken(String email) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .setSubject(email) 
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
        
        return token;
    }

    public String obtenerEmailDeJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            System.err.println("JWT no válido: Formato incorrecto");
        } catch (ExpiredJwtException ex) {
            System.err.println("JWT expirado");
        } catch (UnsupportedJwtException ex) {
            System.err.println("JWT no soportado");
        } catch (IllegalArgumentException ex) {
            System.err.println("Cadena JWT vacía");
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            System.err.println("Firma JWT inválida");
        }
        return false;
    }
}