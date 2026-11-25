package com.xoper.whitelabel.catalog;

import com.xoper.whitelabel.tenant.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class BrazilianProviderClient implements ProductProviderClient {

    private static final Logger log = LoggerFactory.getLogger(BrazilianProviderClient.class);
    private final RestClient restClient;
    private final String providerUrl;

    public BrazilianProviderClient(@Value("${app.providers.brazilian-url}") String providerUrl) {
        this.restClient = RestClient.create();
        this.providerUrl = providerUrl;
    }

    @Override
    public ProviderType getProviderType() {
        return ProviderType.BRAZILIAN;
    }

    @Override
    public List<ProductDto> fetchAll() {
        try {
            @SuppressWarnings("unchecked")
            List rawProducts = restClient.get()
                    .uri(providerUrl)
                    .retrieve()
                    .body(List.class);

            if (rawProducts == null) return Collections.emptyList();

            return rawProducts.stream()
                    .map(item -> normalize((Map<String, Object>) item))
                    .toList();
        } catch (Exception e) {
            log.error("Erro ao consultar fornecedor BRAZILIAN: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private ProductDto normalize(Map<String, Object> raw) {
        String rawId = String.valueOf(raw.getOrDefault("id", ""));
        String name = String.valueOf(raw.getOrDefault("nome", raw.getOrDefault("name", "Produto sem nome")));
        String description = String.valueOf(raw.getOrDefault("descricao", raw.getOrDefault("description", "")));
        BigDecimal price = parseBigDecimal(raw.getOrDefault("preco", raw.getOrDefault("price", "0")));
        String category = String.valueOf(raw.getOrDefault("categoria", raw.getOrDefault("category", "Geral")));
        String material = String.valueOf(raw.getOrDefault("material", ""));
        String department = String.valueOf(raw.getOrDefault("departamento", raw.getOrDefault("department", "")));

        Object imgObj = raw.getOrDefault("imagem", raw.get("image"));
        List<String> images = imgObj != null ? List.of(String.valueOf(imgObj)) : Collections.emptyList();

        return new ProductDto(
                "BR-" + rawId,
                name,
                description,
                price,
                category,
                images,
                material,
                department,
                false,
                BigDecimal.ZERO,
                ProviderType.BRAZILIAN.name()
        );
    }

    private BigDecimal parseBigDecimal(Object val) {
        try {
            return new BigDecimal(String.valueOf(val));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}