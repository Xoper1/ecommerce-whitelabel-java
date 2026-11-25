package com.xoper.whitelabel.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {}

    public record LoginRequest(
            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "Formato de e-mail inválido")
            String email,

            @NotBlank(message = "Senha é obrigatória")
            String password
    ) {}

    public record RegisterRequest(
            @NotBlank(message = "Nome é obrigatório")
            @Size(min = 2, max = 120)
            String name,

            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "Formato de e-mail inválido")
            String email,

            @NotBlank(message = "Senha é obrigatória")
            @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
            String password
    ) {}

    public record AuthResponse(
            String accessToken,
            UserSummary user
    ) {}

    public record UserSummary(
            Long id,
            String name,
            String email,
            Long clientId
    ) {
        public static UserSummary fromEntity(User user) {
            return new UserSummary(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getClient().getId()
            );
        }
    }
}