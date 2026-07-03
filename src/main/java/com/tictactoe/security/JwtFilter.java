package com.tictactoe.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.tictactoe.entity.User;
import com.tictactoe.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtFilter extends OncePerRequestFilter {

    private final SupabaseJwtVerifier jwtVerifier;
    private final UserService userService;

    public JwtFilter(SupabaseJwtVerifier jwtVerifier, UserService userService) {
        this.jwtVerifier = jwtVerifier;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromCookie(request);

        if (token != null) {
            try {
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                
                String userId = decodedJWT.getSubject(); // uuid from supabase
                String email = decodedJWT.getClaim("email").asString();
                
                var userMetadata = decodedJWT.getClaim("user_metadata");
                String username = null;
                if (userMetadata != null && !userMetadata.isNull()) {
                    username = userMetadata.asMap().get("username") != null ? 
                               userMetadata.asMap().get("username").toString() : null;
                }
                if (username == null || username.trim().isEmpty()) {
                    username = email.split("@")[0];
                }

                // Sync the user with the database
                User user = userService.syncUser(userId, email, username);

                // Create authentication token using verified details
                AuthenticatedUser principal = new AuthenticatedUser(user.getId(), user.getUsername(), user.getEmail());
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Token verification failed (expired, tampered, etc.)
                System.err.println("JWT Verification failed: " + e.getMessage());
                e.printStackTrace();
                SecurityContextHolder.clearContext();
                
                // Optional: Clear cookie to prevent infinite retries
                Cookie cookie = new Cookie("access_token", null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
