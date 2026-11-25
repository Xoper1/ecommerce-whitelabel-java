package com.xoper.whitelabel.tenant;

import java.util.List;

public record ClientResponse(
        Long id,
        String name,
        String domain,
        String logoUrl,
        String primaryColor,
        List<String> activeProviders
) {
    public static ClientResponse fromEntity(Client client) {
        List<String> providers = client.getProviderConfigs().stream()
                .filter(ProviderConfig::isActive)
                .map(cfg -> cfg.getProviderName().name())
                .toList();

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getDomain(),
                client.getLogoUrl(),
                client.getPrimaryColor(),
                providers
        );
    }
}