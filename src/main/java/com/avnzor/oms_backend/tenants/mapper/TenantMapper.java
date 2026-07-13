package com.avnzor.oms_backend.tenants.mapper;

import com.avnzor.oms_backend.tenants.dto.TenantResponse;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toInstant")
    TenantResponse toResponse(Tenant tenant);

    List<TenantResponse> toResponseList(List<Tenant> tenants);

    @Named("toInstant")
    default Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toInstant(ZoneOffset.UTC);
    }
}
