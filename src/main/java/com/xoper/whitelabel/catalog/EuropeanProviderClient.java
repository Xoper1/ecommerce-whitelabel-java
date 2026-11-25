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
public class EuropeanProviderClient implements ProductProviderClient {

    private static final Logger log = LoggerFactory.getLogger(EuropeanProviderClient.class);
    private final RestClient restClient;
    private final String providerUrl;

    public EuropeanProviderClient(@Value("${app.providers.european-url}") String providerUrl) {
        this.restClient = RestClient.create();
        this.providerUrl = providerUrl;
    }

    @Override
    public ProviderType getProviderType() {
        return ProviderType.EUROPEAN;
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
            log.error("Erro ao consultar fornecedor EUROPEAN: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private ProductDto normalize(Map<String, Object> raw) {
        String rawId = String.valueOf(raw.getOrDefault("id", ""));
        String name = String.valueOf(raw.getOrDefault("name", "Unnamed Product"));
        String description = String.valueOf(raw.getOrDefault("description", ""));
        BigDecimal price = parseBigDecimal(raw.getOrDefault("price", "0"));
        boolean hasDiscount = Boolean.parseBoolean(String.valueOf(raw.getOrDefault("hasDiscount", false)));
        BigDecimal discountValue = parseBigDecimal(raw.getOrDefault("discountValue", "0"));

        List<String> images = Collections.emptyList();
        Object gallery = raw.get("gallery");
        if (gallery instanceof List<?> list) {
            images = list.stream().map(String::valueOf).toList();
        }

        String material = "";
        String adjective = "";
        Object detailsObj = raw.get("details");
        if (detailsObj instanceof Map<?, ?> details) {
            material = String.valueOf(details.get("material"));
            adjective = String.valueOf(details.get("adjective"));
        }

        return new ProductDto(
                "EU-" + rawId,
                name,
                description,
                price,
                adjective.isBlank() ? "European" : adjective,
                images,
                material,
                "International",
                hasDiscount,
                discountValue,
                ProviderType.EUROPEAN.name()
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