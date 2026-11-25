package com.xoper.whitelabel.tenant;

public final class TenantContext {

    private static final ThreadLocal<Client> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setCurrentTenant(Client client) {
        CURRENT_TENANT.set(client);
    }

    public static Client getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}