package com.qcomit.EmployeemanegementsystemAPI.filter;


import com.qcomit.EmployeemanegementsystemAPI.util.constants.SecurityConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {

            SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecurityConstant.SECRET_KEY));
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", authentication.getName());
            authentication.getAuthorities().forEach
                    (role -> claims.put("authorities", role.getAuthority()));

            String jwt = Jwts.builder()
                    .claims(claims)
                    .signWith(secretKey)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 48))
                    .compact();
            request.setAttribute("token", jwt);
            response.setHeader(SecurityConstant.JWT_HEADER_NAME, jwt);

        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/api/v1/user/login");
    }


}
