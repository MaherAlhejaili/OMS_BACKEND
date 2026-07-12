package com.avnzor.oms_backend.support;

import com.avnzor.oms_backend.auth.entity.WarehouseWorker;
import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;

public final class TestUserFactory {

    public static final String TEST_TENANT_SLUG = "test-tenant";
    public static final Long TEST_TENANT_ID = 1L;

    private TestUserFactory() {
    }

    public static WarehouseWorker logisticWorker() {
        WarehouseWorker worker = new WarehouseWorker();
        worker.setId(1);
        worker.setUsername("logistic.user");
        worker.setPassword("password");
        worker.setName("Logistic User");
        worker.setRole("worker");
        worker.setDepartment("Logistic");
        return worker;
    }

    public static WarehouseWorker warehouseWorker() {
        WarehouseWorker worker = new WarehouseWorker();
        worker.setId(2);
        worker.setUsername("warehouse.user");
        worker.setPassword("password");
        worker.setName("Warehouse User");
        worker.setRole("worker");
        worker.setDepartment("Warehouse");
        return worker;
    }

    public static WarehouseUserPrincipal logisticPrincipal() {
        return new WarehouseUserPrincipal(logisticWorker(), TEST_TENANT_ID, TEST_TENANT_SLUG);
    }

    public static WarehouseUserPrincipal warehousePrincipal() {
        return new WarehouseUserPrincipal(warehouseWorker(), TEST_TENANT_ID, TEST_TENANT_SLUG);
    }
}
