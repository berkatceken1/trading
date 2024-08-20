package com.berk.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

// JwtTokenValidator class'ı, gelen her HTTP isteğinde JWT'nin doğrulanmasını sağlar.
public class JwtTokenValidator extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // JWT'yi HTTP başlığından alıyoruz.
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        // Eğer JWT mevcutsa, onu doğrulamaya çalışıyoruz.
        if (jwt != null) {
            // JWT'nin "Bearer " ön ekini kaldırıyoruz (ilk 7 karakter).
            jwt = jwt.substring(7);

            try {
                // JWT imzasını doğrulamak için gizli anahtarı oluşturuyoruz.
                SecretKey secretKey = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

                // JWT'yi çözüp (parse) içindeki bilgileri (claims) alıyoruz.
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey) // Anahtarı doğrulama için ayarlıyoruz.
                        .build()
                        .parseClaimsJws(jwt) // JWT'yi parse ediyoruz.
                        .getBody(); // JWT'nin gövdesindeki verileri alıyoruz.

                // JWT'den kullanıcı e-posta bilgisini alıyoruz.
                String email = String.valueOf(claims.get("email"));

                // JWT'den kullanıcı yetki bilgisini alıyoruz.
                String authorities = String.valueOf(claims.get("authorities"));

                // Yetkileri, Spring Security'nin kullanabileceği bir listeye çeviriyoruz.
                List<GrantedAuthority> authoritiesList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                // Kullanıcı doğrulamasını (authentication) oluşturuyoruz.
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authoritiesList
                );

                // Kullanıcıyı doğrulamak için Spring Security'nin güvenlik bağlamını (context) ayarlıyoruz.
                SecurityContextHolder.getContext().setAuthentication(auth);



            } catch (Exception e) {
                throw new RuntimeException("Invalid token");
            }
        }

        // Diğer filtrelerin çalışması için filtre zincirine devam ediyoruz.
        filterChain.doFilter(request, response);
    }
}
