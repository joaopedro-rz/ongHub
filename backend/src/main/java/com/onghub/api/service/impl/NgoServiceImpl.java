package com.onghub.api.service.impl;

import com.onghub.api.dto.request.NgoRegisterRequest;
import com.onghub.api.dto.request.NgoUpdateRequest;
import com.onghub.api.dto.response.NgoResponse;
import com.onghub.api.dto.response.NgoSummaryResponse;
import com.onghub.api.entity.*;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.mapper.NgoMapper;
import com.onghub.api.repository.NgoCategoryRepository;
import com.onghub.api.repository.NgoRepository;
import com.onghub.api.repository.UserRepository;
import com.onghub.api.repository.spec.NgoSpecifications;
import com.onghub.api.service.NgoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NgoServiceImpl implements NgoService {

    private final NgoRepository ngoRepository;
    private final UserRepository userRepository;
    private final NgoCategoryRepository ngoCategoryRepository;
    private final NgoMapper ngoMapper;

    public NgoServiceImpl(
        NgoRepository ngoRepository,
        UserRepository userRepository,
        NgoCategoryRepository ngoCategoryRepository,
        NgoMapper ngoMapper
    ) {
        this.ngoRepository = ngoRepository;
        this.userRepository = userRepository;
        this.ngoCategoryRepository = ngoCategoryRepository;
        this.ngoMapper = ngoMapper;
    }

    @Override
    public NgoResponse registerNgo(String managerEmail, NgoRegisterRequest request) {
        User manager = userRepository.findByEmail(managerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Ngo ngo = new Ngo();
        ngo.setName(request.name());
        ngo.setCnpj(request.cnpj());
        ngo.setDescription(request.description());
        ngo.setPhone(request.phone());
        ngo.setWebsite(request.website());
        ngo.setEmail(request.email());
        ngo.setLogoUrl(request.logoUrl());
        ngo.setSocialLinks(request.socialLinks());
        ngo.setCertifications(request.certifications());
        ngo.setStatus(NgoStatus.PENDING);
        ngo.setManager(manager);

        if (request.categoryId() != null) {
            NgoCategory category = ngoCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("NgoCategory not found"));
            ngo.setCategory(category);
        }

        if (request.address() != null) {
            Address address = ngoMapper.toAddressEntity(request.address());
            if (address.getCountry() == null || address.getCountry().isBlank()) {
                address.setCountry("BR");
            }
            ngo.setAddress(address);
        }

        Ngo saved = ngoRepository.save(ngo);
        return ngoMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NgoSummaryResponse> list(Pageable pageable, String managerEmail, NgoStatus status, Long categoryId, String search) {
        Specification<Ngo> spec = (root, query, cb) -> cb.conjunction();
        if (managerEmail != null && !managerEmail.isBlank()) {
            String email = managerEmail.toLowerCase();
            spec = spec.and((root, query, cb) -> cb.equal(root.get("manager").get("email"), email));
        }
        spec = spec.and(NgoSpecifications.hasStatus(status))
            .and(NgoSpecifications.hasCategoryId(categoryId))
            .and(NgoSpecifications.search(search));
        return ngoRepository.findAll(spec, pageable).map(ngoMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NgoSummaryResponse> listPublic(Pageable pageable, Long categoryId, String search) {
        Specification<Ngo> spec = NgoSpecifications.hasStatus(NgoStatus.ACTIVE)
            .and(NgoSpecifications.hasCategoryId(categoryId))
            .and(NgoSpecifications.search(search));
        return ngoRepository.findAll(spec, pageable).map(ngoMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public NgoResponse getById(Long id) {
        Ngo ngo = ngoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));
        return ngoMapper.toResponse(ngo);
    }

    @Override
    public NgoResponse update(Long id, String managerEmail, NgoUpdateRequest request) {
        Ngo ngo = ngoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));

        // basic manager ownership check
        if (managerEmail != null && ngo.getManager() != null && !managerEmail.equalsIgnoreCase(ngo.getManager().getEmail())) {
            throw new ResourceNotFoundException("Ngo not found");
        }

        if (request.name() != null) ngo.setName(request.name());
        if (request.cnpj() != null) ngo.setCnpj(request.cnpj());
        if (request.description() != null) ngo.setDescription(request.description());
        if (request.phone() != null) ngo.setPhone(request.phone());
        if (request.website() != null) ngo.setWebsite(request.website());
        if (request.email() != null) ngo.setEmail(request.email());
        if (request.logoUrl() != null) ngo.setLogoUrl(request.logoUrl());
        if (request.socialLinks() != null) ngo.setSocialLinks(request.socialLinks());
        if (request.certifications() != null) ngo.setCertifications(request.certifications());

        if (request.categoryId() != null) {
            NgoCategory category = ngoCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("NgoCategory not found"));
            ngo.setCategory(category);
        }

        if (request.address() != null) {
            if (ngo.getAddress() == null) {
                Address address = new Address();
                ngo.setAddress(address);
            }
            ngoMapper.updateAddress(ngo.getAddress(), request.address());
            if (ngo.getAddress().getCountry() == null || ngo.getAddress().getCountry().isBlank()) {
                ngo.getAddress().setCountry("BR");
            }
        }

        return ngoMapper.toResponse(ngoRepository.save(ngo));
    }

    @Override
    @Transactional(readOnly = true)
    public NgoResponse getPublicById(Long id) {
        Ngo ngo = ngoRepository.findByIdAndStatus(id, NgoStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));
        return ngoMapper.toResponse(ngo);
    }

    @Override
    public NgoResponse updateStatus(Long id, NgoStatus status) {
        Ngo ngo = ngoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));
        ngo.setStatus(status);
        return ngoMapper.toResponse(ngoRepository.save(ngo));
    }
}
