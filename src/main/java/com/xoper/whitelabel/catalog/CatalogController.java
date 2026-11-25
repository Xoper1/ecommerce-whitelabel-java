package com.xoper.whitelabel.catalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Catalog / Products", description = "Catálogo agregado e normalizado por loja (tenant)")
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping
    @Operation(
            summary = "Listar produtos da loja atual",
            description = "Agrega produtos apenas dos fornecedores ativos para a loja indicada em X-Client-Domain",
            parameters = @Parameter(name = "X-Client-Domain", in = ParameterIn.HEADER, required = true, example = "cliente3.local")
    )
    public ResponseEntity<List<ProductDto>> listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(catalogService.getProductsForCurrentTenant(search, category));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obter detalhes de um produto por ID",
            parameters = @Parameter(name = "X-Client-Domain", in = ParameterIn.HEADER, required = true, example = "cliente3.local")
    )
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(catalogService.getProductById(id));
    }
}