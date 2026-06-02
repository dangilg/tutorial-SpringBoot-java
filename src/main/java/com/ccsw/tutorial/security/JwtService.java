package com.ccsw.tutorial.security;

import com.ccsw.tutorial.exceptions.NotValidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * @author dgilguti
 *
 * Servicio encargado de generar y verificar tokens JWT
 */
@Service
public class JwtService {
    private static final String SECRET_KEY = "9fK2LxQ7!Z#tR4P@H8sYwB3JkE5mC%N&";

    //token valido 1d
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24;

    //Token valido 1h
    //private static final long EXPIRATION_MS = 1000 * 60 * 60;

    //token Valido 5"
    //private static final long EXPIRATION_MS = 5000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

    }

    /**
     * Genera un token válido según el username
     * @param username String a partir del cual se genera le token
     * @return token
     */
    public String generateToken(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS)).signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Se extrae el Username del token para verificar que el token corresponde al usuario
     * @param token Token JWT
     * @return Username
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Verifica si el token es válido o ha expirado o ha sido modificado
     * @param token Token JWT
     * @return True si es válido, False si no
     */
    public boolean isTokenValid(String token) {

        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new NotValidTokenException();
        }
    }

}
