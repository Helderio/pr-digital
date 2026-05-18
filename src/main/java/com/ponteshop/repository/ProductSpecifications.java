package com.ponteshop.repository;

import com.ponteshop.entity.Product;
import com.ponteshop.enums.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecifications {
    private ProductSpecifications() {
    }

    public static Specification<Product> status(ProductStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Product> category(String category) {
        return (root, query, cb) -> (category == null || category.isBlank())
            ? cb.conjunction()
            : cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase());
    }

    public static Specification<Product> storeSlug(String storeSlug) {
        return (root, query, cb) -> (storeSlug == null || storeSlug.isBlank())
            ? cb.conjunction()
            : cb.equal(cb.lower(root.join("store").get("slug")), storeSlug.trim().toLowerCase());
    }

    public static Specification<Product> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return cb.conjunction();
            String like = "%" + search.trim().toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("description")), like)
            );
        };
    }
}

