package com.avnzor.oms_backend.sales.mapper;

import com.avnzor.oms_backend.sales.dto.UpdateSaleStatusResponse;
import com.avnzor.oms_backend.sales.dto.UpdateShelvingStatusResponse;
import com.avnzor.oms_backend.sales.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    @Mapping(target = "message", constant = "Sale status updated successfully")
    UpdateSaleStatusResponse toStatusUpdateResponse(Sale sale);

    default UpdateShelvingStatusResponse toShelvingStatusResponse(Integer id, String status) {
        return new UpdateShelvingStatusResponse(true, id, status);
    }
}
