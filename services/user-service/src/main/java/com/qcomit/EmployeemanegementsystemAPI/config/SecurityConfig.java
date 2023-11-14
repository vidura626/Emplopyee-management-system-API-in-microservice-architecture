package com.qcomit.EmployeemanegementsystemAPI.config;

import com.qcomit.EmployeemanegementsystemAPI.filter.JwtGeneratorFilter;
import com.qcomit.EmployeemanegementsystemAPI.filter.JwtValidationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        CsrfTokenRequestAttributeHandler attributeHandler = new CsrfTokenRequestAttributeHandler();
        attributeHandler.setCsrfRequestAttributeName("_csrf");

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(configurer ->
                        configurer.configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();
                                configuration.setAllowedOriginPatterns(List.of("*"));
                                configuration.setMaxAge(3600L);
                                configuration.setExposedHeaders(List.of("Authorization"));
                                configuration.setAllowedMethods(List.of("*"));
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(List.of("*"));
                                return configuration;
                            }
                        }))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtValidationFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtGeneratorFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(req -> {
                    req
                            .requestMatchers("/api/v1/user/login").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/v1/user")
                            .permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/user")
                            .hasAnyRole("USER", "ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/user")
                            .hasAnyRole("USER", "ADMIN");

                });

        http.httpBasic(withDefaults());
        http.formLogin(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
