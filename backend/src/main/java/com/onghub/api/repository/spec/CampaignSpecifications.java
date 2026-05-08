package com.onghub.api.repository.spec;

import com.onghub.api.entity.Campaign;
import com.onghub.api.entity.CampaignStatus;
import com.onghub.api.entity.NgoStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class CampaignSpecifications {

    private CampaignSpecifications() {}

    /// Visible on anonymous catalogue: ACTIVE campaign and ACTIVE NGO
    public static Specification<Campaign> publiclyListed() {
        return (root, query, cb) -> {
            Join<?, ?> ngo = root.join("ngo");
            query.distinct(true);
            return cb.and(
                cb.equal(root.get("status"), CampaignStatus.ACTIVE),
                cb.equal(ngo.get("status"), NgoStatus.ACTIVE)
            );
        };
    }

    public static Specification<Campaign> hasNgoId(Long ngoId) {
        return (root, query, cb) -> ngoId == null ? cb.conjunction()
            : cb.equal(root.join("ngo").get("id"), ngoId);
    }

    public static Specification<Campaign> hasCampaignStatus(CampaignStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction()
            : cb.equal(root.get("status"), status);
    }

    public static Specification<Campaign> categoryContains(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("category")), category.toLowerCase());
        };
    }

    public static Specification<Campaign> isUrgent(Boolean urgent) {
        return (root, query, cb) -> urgent == null ? cb.conjunction()
            : cb.equal(root.get("urgent"), urgent);
    }

    public static Specification<Campaign> searchText(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + term.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(cb.coalesce(root.get("description"), "")), like)
            );
        };
    }

    /// Filters campaigns linked to NGO address.city (case insensitive partial match)
    public static Specification<Campaign> localityCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isBlank()) {
                return cb.conjunction();
            }
            Join<?, ?> ngo = root.join("ngo");
            Join<?, ?> addr = ngo.join("address", JoinType.LEFT);
            String like = "%" + city.toLowerCase() + "%";
            return cb.and(
                cb.isNotNull(addr.get("city")),
                cb.like(cb.lower(addr.get("city")), like)
            );
        };
    }

    public static Specification<Campaign> managedByEmail(String managerEmail) {
        return (root, query, cb) -> {
            if (managerEmail == null || managerEmail.isBlank()) {
                return cb.conjunction();
            }
            query.distinct(true);
            Join<?, ?> ngo = root.join("ngo");
            return cb.equal(ngo.get("manager").get("email"), managerEmail.toLowerCase());
        };
    }
}
