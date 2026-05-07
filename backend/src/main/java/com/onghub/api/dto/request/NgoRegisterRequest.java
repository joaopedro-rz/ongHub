package com.onghub.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NgoRegisterRequest(
    @NotBlank @Size(max = 200) String name,
    @Size(max = 20) String cnpj,
    String description,
    @Size(max = 30) String phone,
    @Size(max = 255) String website,
    @Email String email,
    Long categoryId,
    @Valid AddressRequest address
) {
    public record AddressRequest(
        @NotBlank String street,
        String number,
        String complement,
        String neighborhood,
        @NotBlank String city,
        @NotBlank String state,
        String country,
        @NotBlank String postalCode,
        Double latitude,
        Double longitude
    ) {}
}
