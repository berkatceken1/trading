package com.berk.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JwtProvider {
    private static SecretKey secretKey = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public static String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities(); // Kullanıcının yetkilerini alıyoruz.
        String roles = populateAuthorities(authorities);

        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000)) // 1 gün
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .signWith(secretKey)
                .compact();
        return jwt;
    }

    public static String getEmailFromToken(String token) {
        token = token.substring(7); // "Bearer " ön ekini kaldırıyoruz.

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        String email = String.valueOf(claims.get("email"));
        return email;
    }

    // Yetkileri virgülle ayırarak birleştirir.
    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auth = new HashSet<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            auth.add(grantedAuthority.getAuthority());
        }
        return String.join(",", auth);
    }

}
