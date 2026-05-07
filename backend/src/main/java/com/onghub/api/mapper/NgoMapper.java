package com.onghub.api.mapper;

import com.onghub.api.dto.request.NgoRegisterRequest;
import com.onghub.api.dto.request.NgoUpdateRequest;
import com.onghub.api.dto.response.NgoResponse;
import com.onghub.api.dto.response.NgoSummaryResponse;
import com.onghub.api.entity.Address;
import com.onghub.api.entity.Ngo;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NgoMapper {

    @Mapping(target = "managerUserId", source = "manager.id")
    @Mapping(target = "categoryId", source = "category.id")
    NgoResponse toResponse(Ngo ngo);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "state", source = "address.state")
    NgoSummaryResponse toSummary(Ngo ngo);

    Address toAddressEntity(NgoRegisterRequest.AddressRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddress(@MappingTarget Address address, NgoUpdateRequest.AddressRequest request);
}
