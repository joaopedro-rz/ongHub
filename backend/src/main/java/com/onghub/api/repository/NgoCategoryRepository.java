package com.onghub.api.repository;

import com.onghub.api.entity.NgoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NgoCategoryRepository extends JpaRepository<NgoCategory, Long> {
    Optional<NgoCategory> findByNameIgnoreCase(String name);
}
