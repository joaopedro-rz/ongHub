package com.onghub.api.repository;

import com.onghub.api.entity.Ngo;
import com.onghub.api.entity.NgoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface NgoRepository extends JpaRepository<Ngo, Long>, JpaSpecificationExecutor<Ngo> {
    Page<Ngo> findByStatus(NgoStatus status, Pageable pageable);
    Page<Ngo> findByManager_Email(String email, Pageable pageable);
    Optional<Ngo> findByIdAndStatus(Long id, NgoStatus status);
}
