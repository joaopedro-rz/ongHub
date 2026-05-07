package com.onghub.api.repository.spec;

import com.onghub.api.entity.Ngo;
import com.onghub.api.entity.NgoStatus;
import org.springframework.data.jpa.domain.Specification;

public final class NgoSpecifications {

    private NgoSpecifications() {}

    public static Specification<Ngo> hasStatus(NgoStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Ngo> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? cb.conjunction() : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Ngo> search(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + term.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("description")), like)
            );
        };
    }
}
