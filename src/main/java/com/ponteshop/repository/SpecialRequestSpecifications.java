package com.ponteshop.repository;

import com.ponteshop.entity.SpecialRequest;
import com.ponteshop.enums.SpecialRequestStatus;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class SpecialRequestSpecifications {

    private SpecialRequestSpecifications() {
    }

    public static Specification<SpecialRequest> adminFilters(SpecialRequestStatus status, Integer storeId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (storeId != null) {
                predicates.add(cb.equal(root.get("store").get("id"), storeId));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
