package com.xoper.whitelabel.catalog;

import java.math.BigDecimal;
import java.util.List;

public record ProductDto(
        String id,
        String name,
        String description,
        BigDecimal price,
        String category,
        List<String> images,
        String material,
        String department,
        boolean hasDiscount,
        BigDecimal discountValue,
        String provider
) {}