package com.onghub.api.service;

import com.onghub.api.dto.request.CampaignCreateRequest;
import com.onghub.api.dto.response.CampaignDetailResponse;
import com.onghub.api.entity.Campaign;
import com.onghub.api.entity.Ngo;
import com.onghub.api.entity.User;
import com.onghub.api.mapper.CampaignDtoAssembler;
import com.onghub.api.repository.CampaignRepository;
import com.onghub.api.repository.NgoRepository;
import com.onghub.api.service.impl.CampaignServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private NgoRepository ngoRepository;

    private CampaignService campaignService;

    @BeforeEach
    void setup() {
        campaignService = new CampaignServiceImpl(campaignRepository, ngoRepository, new CampaignDtoAssembler());
    }

    @Test
    void createPersistsCampaignForNgoManager() {
        User manager = new User();
        manager.setEmail("mgr@test.com");

        Ngo ngo = new Ngo();
        ngo.setId(3L);
        ngo.setManager(manager);

        when(ngoRepository.findById(3L)).thenReturn(Optional.of(ngo));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CampaignCreateRequest request = new CampaignCreateRequest(
            3L,
            "Campanha X",
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null
        );

        CampaignDetailResponse response = campaignService.create(request, "mgr@test.com");

        assertEquals("Campanha X", response.title());
        verify(campaignRepository).save(any(Campaign.class));
    }
}
