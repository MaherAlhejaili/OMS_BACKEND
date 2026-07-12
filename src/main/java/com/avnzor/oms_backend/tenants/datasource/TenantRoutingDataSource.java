package com.avnzor.oms_backend.tenants.datasource;

import com.avnzor.oms_backend.tenants.context.TenantContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContextHolder.getTenantSlug();
    }
}
