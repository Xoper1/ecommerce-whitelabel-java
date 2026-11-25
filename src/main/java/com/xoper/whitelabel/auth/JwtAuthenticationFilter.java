package com.xoper.whitelabel.auth;

import com.xoper.whitelabel.tenant.Client;
import com.xoper.whitelabel.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.parseToken(token);
            String email = claims.getSubject();
            Long tokenClientId = claims.get("clientId", Long.class);

            Client currentTenant = TenantContext.getCurrentTenant();

            // Garante isolamento estrito: o token só vale para o tenant da requisição atual
            if (currentTenant != null && currentTenant.getId().equals(tokenClientId)) {
                userRepository.findByClientIdAndEmailIgnoreCase(tokenClientId, email)
                        .ifPresent(user -> {
                            var auth = new UsernamePasswordAuthenticationToken(
                                    user, null, Collections.emptyList()
                            );
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
            }
        } catch (JwtException | IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}