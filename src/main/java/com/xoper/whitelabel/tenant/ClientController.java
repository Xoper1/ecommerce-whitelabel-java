package com.xoper.whitelabel.tenant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
@Tag(name = "Tenant / Client", description = "Configurações visuais e de fornecedores da loja ativa")
public class ClientController {

    @GetMapping("/config")
    @Operation(
            summary = "Obter configuração da loja atual",
            description = "Retorna nome, logo, cor primária e fornecedores ativos com base no header X-Client-Domain",
            parameters = {
                    @Parameter(name = "X-Client-Domain", in = ParameterIn.HEADER, required = true, example = "cliente1.local")
            }
    )
    public ResponseEntity<ClientResponse> getClientConfig() {
        Client currentTenant = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(ClientResponse.fromEntity(currentTenant));
    }
}