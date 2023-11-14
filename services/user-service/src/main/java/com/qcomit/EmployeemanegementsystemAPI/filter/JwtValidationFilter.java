package com.qcomit.EmployeemanegementsystemAPI.filter;

import com.qcomit.EmployeemanegementsystemAPI.util.constants.SecurityConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtValidationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(SecurityConstant.JWT_HEADER_NAME);
        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring("Bearer ".length());

            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecurityConstant.SECRET_KEY));

                Claims claims = (Claims) Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwt)
                        .getPayload();

                String username = (String) claims.get("username");
                String role = (String) claims.get("authorities");

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(role)));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid Token");
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/api/v1/user/login");
    }
}
