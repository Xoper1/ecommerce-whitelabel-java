package com.xoper.whitelabel.catalog;

import com.xoper.whitelabel.tenant.Client;
import com.xoper.whitelabel.tenant.ProviderConfig;
import com.xoper.whitelabel.tenant.ProviderType;
import com.xoper.whitelabel.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final Map<ProviderType, ProductProviderClient> providerMap;

    public CatalogService(List<ProductProviderClient> providers) {
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(ProductProviderClient::getProviderType, Function.identity()));
    }

    public List<ProductDto> getProductsForCurrentTenant(String search, String category) {
        Client tenant = TenantContext.getCurrentTenant();

        List<ProviderType> activeTypes = tenant.getProviderConfigs().stream()
                .filter(ProviderConfig::isActive)
                .map(ProviderConfig::getProviderName)
                .toList();

        // Executa chamadas HTTP aos fornecedores em paralelo usando Virtual Threads
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<List<ProductDto>>> futures = activeTypes.stream()
                    .map(providerMap::get)
                    .filter(java.util.Objects::nonNull)
                    .map(client -> CompletableFuture.supplyAsync(client::fetchAll, executor))
                    .toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .filter(p -> matchesSearch(p, search))
                    .filter(p -> matchesCategory(p, category))
                    .toList();
        }
    }

    public ProductDto getProductById(String id) {
        return getProductsForCurrentTenant(null, null).stream()
                .filter(p -> p.id().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + id));
    }

    private boolean matchesSearch(ProductDto product, String search) {
        if (search == null || search.isBlank()) return true;
        String term = search.toLowerCase();
        return product.name().toLowerCase().contains(term)
                || product.description().toLowerCase().contains(term);
    }

    private boolean matchesCategory(ProductDto product, String category) {
        if (category == null || category.isBlank()) return true;
        return product.category().equalsIgnoreCase(category.trim());
    }
}