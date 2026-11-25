package com.xoper.whitelabel.auth;

import com.xoper.whitelabel.auth.AuthDtos.*;
import com.xoper.whitelabel.tenant.Client;
import com.xoper.whitelabel.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Client tenant = TenantContext.getCurrentTenant();

        User user = userRepository.findByClientIdAndEmailIgnoreCase(tenant.getId(), request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserSummary.fromEntity(user));
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Client tenant = TenantContext.getCurrentTenant();

        if (userRepository.existsByClientIdAndEmailIgnoreCase(tenant.getId(), request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado nesta loja.");
        }

        User newUser = User.builder()
                .client(tenant)
                .name(request.name().trim())
                .email(request.email().trim().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .build();

        User saved = userRepository.save(newUser);
        String token = jwtService.generateToken(saved);
        return new AuthResponse(token, UserSummary.fromEntity(saved));
    }
}