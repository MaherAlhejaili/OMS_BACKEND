package com.avnzor.oms_backend.tenants.bootstrap;

import com.avnzor.oms_backend.tenants.encryption.CredentialEncryptor;
import com.avnzor.oms_backend.tenants.repository.PlatformUserRepository;
import com.avnzor.oms_backend.tenants.repository.TenantRepository;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

public record TenantBootstrapContext(
        TenantRepository tenantRepository,
        PlatformUserRepository platformUserRepository,
        DataSource platformDataSource,
        CredentialEncryptor credentialEncryptor,
        PasswordEncoder passwordEncoder,
        Environment environment
) {
}
