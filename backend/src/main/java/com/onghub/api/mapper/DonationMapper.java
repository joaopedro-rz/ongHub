package com.onghub.api.mapper;

import com.onghub.api.dto.response.DonationResponse;
import com.onghub.api.entity.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DonationMapper {

    @Mapping(target = "campaignId", source = "donation.campaign.id")
    @Mapping(target = "campaignTitle", source = "donation.campaign.title")
    @Mapping(target = "campaignItemId", source = "donation.campaignItem.id")
    @Mapping(target = "receiptNumber", source = "receiptNumber")
    DonationResponse toDonationResponse(Donation donation, String receiptNumber);
}

