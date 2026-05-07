package com.onghub.api.dto.response;

import com.onghub.api.entity.NgoStatus;

public record NgoResponse(
    Long id,
    String name,
    String cnpj,
    String description,
    String phone,
    String website,
    String email,
    NgoStatus status,
    Long managerUserId,
    Long categoryId,
    AddressResponse address
) {
    public record AddressResponse(
        Long id,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String country,
        String postalCode,
        Double latitude,
        Double longitude
    ) {}
}
