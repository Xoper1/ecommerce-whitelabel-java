package com.xoper.whitelabel.auth;

import com.xoper.whitelabel.auth.AuthDtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, registo e perfil isolados por loja (tenant)")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar utilizador na loja atual",
            parameters = @Parameter(name = "X-Client-Domain", in = ParameterIn.HEADER, required = true, example = "cliente1.local")
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Registar novo utilizador na loja atual",
            parameters = @Parameter(name = "X-Client-Domain", in = ParameterIn.HEADER, required = true, example = "cliente1.local")
    )
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Obter dados do utilizador autenticado",
            parameters = @Parameter(name = "X-Client-Domain", in = ParameterIn.HEADER, required = true, example = "cliente1.local")
    )
    public ResponseEntity<UserSummary> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserSummary.fromEntity(user));
    }
}