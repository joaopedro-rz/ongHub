package com.onghub.api.service;

import com.onghub.api.dto.request.*;
import com.onghub.api.dto.volunteer.VolunteerApiDtos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VolunteerService {

    Page<VolunteerApiDtos.OpportunitySummary> listPublic(Pageable pageable, Long ngoId);

    Page<VolunteerApiDtos.OpportunitySummary> listPublic(Pageable pageable, Long ngoId, Long skillId, String search);

    VolunteerApiDtos.OpportunityDetailPublic getPublic(Long opportunityId);

    VolunteerApiDtos.OpportunityManagement create(VolunteerOpportunityCreateRequest request, String principalEmail);

    Page<VolunteerApiDtos.OpportunityManagement> listManaged(Pageable pageable, Long ngoId, String principalEmail);

    VolunteerApiDtos.OpportunityManagement patch(Long id, VolunteerOpportunityPatchRequest request, String principalEmail);

    void delete(Long id, String principalEmail);

    VolunteerApiDtos.ApplicationResponse apply(Long opportunityId, VolunteerApplyRequest request, String volunteerEmail);

    Page<VolunteerApiDtos.ApplicationResponse> listApplications(Pageable pageable, Long opportunityId, String principalEmail);

    VolunteerApiDtos.ApplicationResponse decide(Long applicationId, VolunteerApplicationDecisionRequest request, String principalEmail);

    Page<VolunteerApiDtos.ApplicationResponse> listMine(Pageable pageable, String volunteerEmail);

    VolunteerApiDtos.ScheduleResponse addSchedule(Long opportunityId, VolunteerScheduleCreateRequest request, String principalEmail);
}
