package com.tictactoe.config;

import com.tictactoe.security.JwtFilter;
import com.tictactoe.security.SupabaseJwtVerifier;
import com.tictactoe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.DispatcherType;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SupabaseJwtVerifier jwtVerifier;
    private final UserService userService;

    @Autowired
    public SecurityConfig(SupabaseJwtVerifier jwtVerifier, UserService userService) {
        this.jwtVerifier = jwtVerifier;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Cookies are used, but we disable CSRF for ease of client-side integration and API requests
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                .requestMatchers(
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/signup"),
                    new AntPathRequestMatcher("/forgot-password"),
                    new AntPathRequestMatcher("/reset-password"),
                    new AntPathRequestMatcher("/static/**"),
                    new AntPathRequestMatcher("/ws/**"),
                    new AntPathRequestMatcher("/api/config/supabase"),
                    new AntPathRequestMatcher("/error")
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtFilter(jwtVerifier, userService), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    // Redirect unauthenticated request to /login
                    response.sendRedirect("/login");
                })
            );

        return http.build();
    }
}
