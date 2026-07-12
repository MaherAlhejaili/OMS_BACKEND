package com.avnzor.oms_backend.tenants.bootstrap;

import com.avnzor.oms_backend.auth.entity.WarehouseWorker;
import com.avnzor.oms_backend.auth.repository.WarehouseWorkerRepository;
import com.avnzor.oms_backend.tenants.context.TenantContextHolder;
import com.avnzor.oms_backend.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevTenantUserSeeder implements ApplicationRunner {

    private final TenantRepository tenantRepository;
    private final WarehouseWorkerRepository warehouseWorkerRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        tenantRepository.findBySlug("tenant-1").ifPresent(tenant -> {
            try {
                TenantContextHolder.set(tenant.getId(), tenant.getSlug());

                if (warehouseWorkerRepository.findByUsername("logistic.user").isPresent()) {
                    return;
                }

                WarehouseWorker logisticUser = new WarehouseWorker();
                logisticUser.setUsername("logistic.user");
                logisticUser.setPassword("password");
                logisticUser.setName("Logistic User");
                logisticUser.setRole("worker");
                logisticUser.setDepartment("Logistic");
                warehouseWorkerRepository.save(logisticUser);

                WarehouseWorker warehouseUser = new WarehouseWorker();
                warehouseUser.setUsername("warehouse.user");
                warehouseUser.setPassword("password");
                warehouseUser.setName("Warehouse User");
                warehouseUser.setRole("worker");
                warehouseUser.setDepartment("Warehouse");
                warehouseWorkerRepository.save(warehouseUser);

                log.info("Seeded dev tenant users in tenant-1 database");
            } finally {
                TenantContextHolder.clear();
            }
        });
    }
}
