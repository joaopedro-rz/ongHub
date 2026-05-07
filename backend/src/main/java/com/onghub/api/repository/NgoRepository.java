package com.onghub.api.repository;

import com.onghub.api.entity.Ngo;
import com.onghub.api.entity.NgoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NgoRepository extends JpaRepository<Ngo, Long> {
    Page<Ngo> findByStatus(NgoStatus status, Pageable pageable);
    Page<Ngo> findByManagerEmail(String managerEmail, Pageable pageable);
    Optional<Ngo> findByIdAndStatus(Long id, NgoStatus status);
}
