package com.ponteshop.repository;

import com.ponteshop.entity.Store;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    List<Store> findByIsActiveTrueOrderByDisplayOrderAsc();

    Optional<Store> findBySlug(String slug);
}

