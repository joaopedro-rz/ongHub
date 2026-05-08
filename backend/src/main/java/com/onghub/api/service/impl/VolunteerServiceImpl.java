package com.onghub.api.service.impl;

import com.onghub.api.dto.request.*;
import com.onghub.api.dto.volunteer.VolunteerApiDtos;
import com.onghub.api.entity.*;
import com.onghub.api.exception.BadRequestException;
import com.onghub.api.exception.DuplicateEntryException;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.repository.*;
import com.onghub.api.security.SecurityUtils;
import com.onghub.api.service.VolunteerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.List;

@Service
@Transactional
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerOpportunityRepository opportunityRepository;
    private final VolunteerApplicationRepository applicationRepository;
    private final VolunteerScheduleRepository scheduleRepository;
    private final NgoRepository ngoRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public VolunteerServiceImpl(
        VolunteerOpportunityRepository opportunityRepository,
        VolunteerApplicationRepository applicationRepository,
        VolunteerScheduleRepository scheduleRepository,
        NgoRepository ngoRepository,
        UserRepository userRepository,
        SkillRepository skillRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.scheduleRepository = scheduleRepository;
        this.ngoRepository = ngoRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VolunteerApiDtos.OpportunitySummary> listPublic(Pageable pageable, Long ngoId) {
        return listPublic(pageable, ngoId, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VolunteerApiDtos.OpportunitySummary> listPublic(Pageable pageable, Long ngoId, Long skillId, String search) {
        String skillName = null;
        if (skillId != null) {
            Skill skill = skillRepository.findById(skillId).orElse(null);
            if (skill == null) {
                return Page.empty(pageable);
            }
            skillName = skill.getName();
        }

        return opportunityRepository
            .findPublicListing(NgoStatus.ACTIVE, ngoId, skillName, search, pageable)
            .map(this::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public VolunteerApiDtos.OpportunityDetailPublic getPublic(Long opportunityId) {
        VolunteerOpportunity o = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (o.getNgo().getStatus() != NgoStatus.ACTIVE) {
            throw new ResourceNotFoundException("Opportunity not found");
        }
        List<VolunteerApiDtos.ScheduleResponse> schedules = scheduleRepository.findByOpportunity_IdOrderBySlotStartAsc(o.getId())
            .stream()
            .map(this::toSchedule)
            .toList();
        return new VolunteerApiDtos.OpportunityDetailPublic(
            toSummary(o),
            o.getDescription(),
            o.getSkillsRequired(),
            o.getHoursPerWeek(),
            schedules
        );
    }

    @Override
    public VolunteerApiDtos.OpportunityManagement create(VolunteerOpportunityCreateRequest request, String principalEmail) {
        Ngo ngo = ngoRepository.findById(request.ngoId())
            .orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));
        assertManageNgo(ngo, principalEmail);

        VolunteerOpportunity o = new VolunteerOpportunity();
        o.setNgo(ngo);
        populateFields(o, request.title(), request.description(), request.skillsRequired(),
            request.slotsAvailable(), request.hoursPerWeek(), request.startDate(), request.endDate());
        return toManagement(opportunityRepository.save(o));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VolunteerApiDtos.OpportunityManagement> listManaged(Pageable pageable, Long ngoId, String principalEmail) {
        if (SecurityUtils.isAdmin()) {
            if (ngoId != null) {
                return opportunityRepository.findByNgo_Id(ngoId, pageable).map(this::toManagement);
            }
            return opportunityRepository.findAll(pageable).map(this::toManagement);
        }
        if (ngoId == null) {
            throw new BadRequestException("ngoId is required");
        }
        Ngo ngo = ngoRepository.findById(ngoId).orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));
        assertManageNgo(ngo, principalEmail);
        return opportunityRepository.findByNgo_Id(ngoId, pageable).map(this::toManagement);
    }

    @Override
    public VolunteerApiDtos.OpportunityManagement patch(Long id, VolunteerOpportunityPatchRequest request, String principalEmail) {
        VolunteerOpportunity o = loadManaged(id, principalEmail);
        if (request.title() != null) {
            o.setTitle(request.title().trim());
        }
        if (request.description() != null) {
            o.setDescription(request.description());
        }
        if (request.skillsRequired() != null) {
            o.setSkillsRequired(request.skillsRequired());
        }
        if (request.slotsAvailable() != null) {
            o.setSlotsAvailable(request.slotsAvailable());
        }
        if (request.hoursPerWeek() != null) {
            o.setHoursPerWeek(request.hoursPerWeek());
        }
        if (request.startDate() != null) {
            o.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            o.setEndDate(request.endDate());
        }
        return toManagement(opportunityRepository.save(o));
    }

    @Override
    public void delete(Long id, String principalEmail) {
        VolunteerOpportunity o = loadManaged(id, principalEmail);
        opportunityRepository.delete(o);
    }

    @Override
    public VolunteerApiDtos.ApplicationResponse apply(Long opportunityId, VolunteerApplyRequest request, String volunteerEmail) {
        VolunteerOpportunity o = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (o.getNgo().getStatus() != NgoStatus.ACTIVE) {
            throw new BadRequestException("Opportunity not available");
        }
        User volunteer = loadUser(volunteerEmail);
        if (applicationRepository.findByOpportunity_IdAndVolunteer_Id(opportunityId, volunteer.getId()).isPresent()) {
            throw new DuplicateEntryException("Already applied");
        }
        VolunteerApplication app = new VolunteerApplication();
        app.setOpportunity(o);
        app.setVolunteer(volunteer);
        app.setSkillsNote(request.skillsNote());
        app.setStatus(VolunteerApplicationStatus.PENDING);
        return toApplication(applicationRepository.save(app));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VolunteerApiDtos.ApplicationResponse> listApplications(Pageable pageable, Long opportunityId, String principalEmail) {
        VolunteerOpportunity o = loadManaged(opportunityId, principalEmail);
        return applicationRepository.findByOpportunity_Id(o.getId(), pageable).map(this::toApplication);
    }

    @Override
    public VolunteerApiDtos.ApplicationResponse decide(Long applicationId, VolunteerApplicationDecisionRequest request, String principalEmail) {
        VolunteerApplication app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        assertManageOpportunity(app.getOpportunity(), principalEmail);
        if (app.getStatus() != VolunteerApplicationStatus.PENDING) {
            throw new BadRequestException("Application already processed");
        }
        if (request.status() != VolunteerApplicationStatus.APPROVED && request.status() != VolunteerApplicationStatus.REJECTED) {
            throw new BadRequestException("Invalid status");
        }

        if (request.status() == VolunteerApplicationStatus.APPROVED) {
            if (!hasRequiredSkills(app.getOpportunity().getSkillsRequired(), app.getSkillsNote())) {
                throw new BadRequestException("Volunteer lacks required skills");
            }
        }

        app.setStatus(request.status());
        return toApplication(applicationRepository.save(app));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VolunteerApiDtos.ApplicationResponse> listMine(Pageable pageable, String volunteerEmail) {
        User volunteer = loadUser(volunteerEmail);
        return applicationRepository.findByVolunteer_Id(volunteer.getId(), pageable).map(this::toApplication);
    }

    @Override
    public VolunteerApiDtos.ScheduleResponse addSchedule(Long opportunityId, VolunteerScheduleCreateRequest request, String principalEmail) {
        VolunteerOpportunity o = loadManaged(opportunityId, principalEmail);
        VolunteerSchedule s = new VolunteerSchedule();
        s.setOpportunity(o);
        s.setSlotStart(request.slotStart());
        s.setSlotEnd(request.slotEnd());
        s.setTitle(request.title());
        if (request.volunteerUserId() != null) {
            User vol = userRepository.findById(request.volunteerUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer user not found"));
            s.setVolunteer(vol);
        }
        return toSchedule(scheduleRepository.save(s));
    }

    private VolunteerOpportunity loadManaged(Long id, String principalEmail) {
        VolunteerOpportunity o = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        assertManageOpportunity(o, principalEmail);
        return o;
    }

    private void assertManageOpportunity(VolunteerOpportunity o, String principalEmail) {
        assertManageNgo(o.getNgo(), principalEmail);
    }

    private void assertManageNgo(Ngo ngo, String principalEmail) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        if (ngo.getManager() == null || !ngo.getManager().getEmail().equalsIgnoreCase(principalEmail)) {
            throw new ResourceNotFoundException("Ngo not found");
        }
    }

    private boolean hasRequiredSkills(String requiredSkills, String volunteerSkillsNote) {
        Set<String> required = tokenizeSkills(requiredSkills);
        if (required.isEmpty()) {
            return true;
        }
        Set<String> provided = tokenizeSkills(volunteerSkillsNote);
        return provided.containsAll(required);
    }

    private Set<String> tokenizeSkills(String raw) {
        if (raw == null) {
            return Set.of();
        }
        String cleaned = raw.trim();
        if (cleaned.isEmpty()) {
            return Set.of();
        }

        return Arrays.stream(cleaned.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(s -> s.toLowerCase(Locale.ROOT))
            .collect(Collectors.toSet());
    }

    private User loadUser(String email) {
        return userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private static void populateFields(
        VolunteerOpportunity o,
        String title,
        String description,
        String skillsRequired,
        int slotsAvailable,
        Integer hoursPerWeek,
        java.time.LocalDate startDate,
        java.time.LocalDate endDate
    ) {
        o.setTitle(title.trim());
        o.setDescription(description);
        o.setSkillsRequired(skillsRequired);
        o.setSlotsAvailable(slotsAvailable);
        o.setHoursPerWeek(hoursPerWeek);
        o.setStartDate(startDate);
        o.setEndDate(endDate);
    }

    private VolunteerApiDtos.OpportunitySummary toSummary(VolunteerOpportunity o) {
        return new VolunteerApiDtos.OpportunitySummary(
            o.getId(),
            o.getNgo().getId(),
            o.getNgo().getName(),
            o.getTitle(),
            o.getSlotsAvailable(),
            o.getStartDate(),
            o.getEndDate()
        );
    }

    private VolunteerApiDtos.OpportunityManagement toManagement(VolunteerOpportunity o) {
        return new VolunteerApiDtos.OpportunityManagement(
            o.getId(),
            o.getNgo().getId(),
            o.getTitle(),
            o.getDescription(),
            o.getSkillsRequired(),
            o.getSlotsAvailable(),
            o.getHoursPerWeek(),
            o.getStartDate(),
            o.getEndDate()
        );
    }

    private VolunteerApiDtos.ApplicationResponse toApplication(VolunteerApplication app) {
        return new VolunteerApiDtos.ApplicationResponse(
            app.getId(),
            app.getOpportunity().getId(),
            app.getVolunteer().getId(),
            app.getVolunteer().getEmail(),
            app.getStatus().name(),
            app.getSkillsNote()
        );
    }

    private VolunteerApiDtos.ScheduleResponse toSchedule(VolunteerSchedule s) {
        Long volId = s.getVolunteer() != null ? s.getVolunteer().getId() : null;
        return new VolunteerApiDtos.ScheduleResponse(
            s.getId(),
            s.getSlotStart(),
            s.getSlotEnd(),
            s.getTitle(),
            volId
        );
    }
}
