package com.ecoswap.ecoswap.security; // 💡 ASUMIMOS ESTE PAQUETE

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.*; // 💡 IMPORTAR ESTO PARA LAS EXCEPCIONES
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // ... (campos jwtSecret y jwtExpirationInMs) ...
    @Value("${jwt.secret}") 
    private String jwtSecret;

    @Value("${jwt.expiration.ms}") 
    private int jwtExpirationInMs; // Elimina la asignación directa aquí, la toma de application.properties

    // ... (métodos key(), generarToken() y getUsernameFromToken() sin cambios) ...
    
    // Método para obtener la clave de firma (Key)
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
    
    // 🚩 1. Generar el Token JWT
    public String generarToken(String userMail) {
        // Establece la fecha de expiración
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Construye el token
        return Jwts.builder()
                .setSubject(userMail)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    // 🚩 2. Obtener el email del token (se usa en el filtro de seguridad)
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }


    // 🚩 3. Validar el Token JWT (Lógica completa)
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            // Token JWT malformado
        } catch (ExpiredJwtException ex) {
            // Token JWT expirado
        } catch (UnsupportedJwtException ex) {
            // Token JWT no soportado
        } catch (IllegalArgumentException ex) {
            // La cadena de claims está vacía
        } catch (SignatureException ex) {
            // Firma JWT inválida
        }
        return false;
    }
}