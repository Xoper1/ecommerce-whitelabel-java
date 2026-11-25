package com.xoper.whitelabel.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Client-Domain";
    private final ClientRepository clientRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/api")
                || path.equals("/error")
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String domain = request.getHeader(TENANT_HEADER);

        if (domain == null || domain.isBlank()) {
            writeErrorResponse(response, HttpStatus.BAD_REQUEST, "Header X-Client-Domain é obrigatório.");
            return;
        }

        Client client = clientRepository.findByDomainIgnoreCase(domain.trim()).orElse(null);

        if (client == null) {
            writeErrorResponse(response, HttpStatus.NOT_FOUND, "Loja não encontrada para o domínio: " + domain);
            return;
        }

        try {
            TenantContext.setCurrentTenant(client);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {"status": %d, "error": "%s", "message": "%s"}
                """.formatted(status.value(), status.getReasonPhrase(), message));
    }
}