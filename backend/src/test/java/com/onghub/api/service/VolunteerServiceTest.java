package com.onghub.api.service;

import com.onghub.api.dto.request.VolunteerApplicationDecisionRequest;
import com.onghub.api.dto.volunteer.VolunteerApiDtos;
import com.onghub.api.entity.*;
import com.onghub.api.exception.BadRequestException;
import com.onghub.api.repository.*;
import com.onghub.api.service.impl.VolunteerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolunteerServiceTest {

    @Mock
    private VolunteerOpportunityRepository opportunityRepository;

    @Mock
    private VolunteerApplicationRepository applicationRepository;

    @Mock
    private VolunteerScheduleRepository scheduleRepository;

    @Mock
    private NgoRepository ngoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    private VolunteerService volunteerService;

    @BeforeEach
    void setup() {
        volunteerService = new VolunteerServiceImpl(
            opportunityRepository,
            applicationRepository,
            scheduleRepository,
            ngoRepository,
            userRepository,
            skillRepository
        );
    }

    @Test
    void approveThrowsWhenVolunteerLacksRequiredSkills() {
        User manager = new User();
        manager.setEmail("manager@test.com");

        Ngo ngo = new Ngo();
        ngo.setManager(manager);

        VolunteerOpportunity opportunity = new VolunteerOpportunity();
        opportunity.setId(1L);
        opportunity.setNgo(ngo);
        opportunity.setSkillsRequired("java,sql");

        User volunteerUser = new User();
        volunteerUser.setId(2L);
        volunteerUser.setEmail("vol@test.com");

        VolunteerApplication app = new VolunteerApplication();
        app.setId(10L);
        app.setOpportunity(opportunity);
        app.setVolunteer(volunteerUser);
        app.setStatus(VolunteerApplicationStatus.PENDING);
        app.setSkillsNote("java"); // missing sql

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));

        VolunteerApplicationDecisionRequest req = new VolunteerApplicationDecisionRequest(
            VolunteerApplicationStatus.APPROVED
        );

        assertThrows(BadRequestException.class, () -> volunteerService.decide(10L, req, "manager@test.com"));
    }

    @Test
    void approveSucceedsWhenVolunteerMatchesRequiredSkills() {
        User manager = new User();
        manager.setEmail("manager@test.com");

        Ngo ngo = new Ngo();
        ngo.setManager(manager);

        VolunteerOpportunity opportunity = new VolunteerOpportunity();
        opportunity.setId(1L);
        opportunity.setNgo(ngo);
        opportunity.setSkillsRequired("java,sql");

        User volunteerUser = new User();
        volunteerUser.setId(2L);
        volunteerUser.setEmail("vol@test.com");

        VolunteerApplication app = new VolunteerApplication();
        app.setId(10L);
        app.setOpportunity(opportunity);
        app.setVolunteer(volunteerUser);
        app.setStatus(VolunteerApplicationStatus.PENDING);
        app.setSkillsNote("java, sql");

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(VolunteerApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        VolunteerApplicationDecisionRequest req = new VolunteerApplicationDecisionRequest(
            VolunteerApplicationStatus.APPROVED
        );

        VolunteerApiDtos.ApplicationResponse resp = volunteerService.decide(10L, req, "manager@test.com");
        assertEquals("APPROVED", resp.status());
        assertEquals("java, sql", resp.skillsNote());
    }
}

