package com.avnzor.oms_backend.tenants.context;

public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void set(TenantContext context) {
        CONTEXT.set(context);
    }

    public static void set(Long tenantId, String tenantSlug) {
        CONTEXT.set(new TenantContext(tenantId, tenantSlug));
    }

    public static TenantContext get() {
        return CONTEXT.get();
    }

    public static String getTenantSlug() {
        TenantContext context = CONTEXT.get();
        return context != null ? context.tenantSlug() : null;
    }

    public static Long getTenantId() {
        TenantContext context = CONTEXT.get();
        return context != null ? context.tenantId() : null;
    }

    public static boolean hasTenant() {
        return CONTEXT.get() != null;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
