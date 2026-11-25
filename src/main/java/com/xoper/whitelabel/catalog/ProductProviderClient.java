package com.xoper.whitelabel.catalog;

import com.xoper.whitelabel.tenant.ProviderType;
import java.util.List;

public interface ProductProviderClient {
    ProviderType getProviderType();
    List<ProductDto> fetchAll();
}