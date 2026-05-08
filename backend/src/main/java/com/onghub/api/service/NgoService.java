package com.onghub.api.service;

import com.onghub.api.dto.request.NgoRegisterRequest;
import com.onghub.api.dto.request.NgoUpdateRequest;
import com.onghub.api.dto.response.NgoResponse;
import com.onghub.api.dto.response.NgoSummaryResponse;
import com.onghub.api.entity.NgoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NgoService {
    NgoResponse registerNgo(String managerEmail, NgoRegisterRequest request);

    Page<NgoSummaryResponse> list(Pageable pageable, String managerEmail, NgoStatus status, Long categoryId, String search);

    Page<NgoSummaryResponse> listPublic(Pageable pageable, Long categoryId, String search);

    NgoResponse getById(Long id);

    NgoResponse update(Long id, String managerEmail, NgoUpdateRequest request);

    NgoResponse getPublicById(Long id);

    NgoResponse updateStatus(Long id, NgoStatus status);
}
