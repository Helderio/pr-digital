package com.ponteshop.repository;

import com.ponteshop.entity.ExchangeRate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    Optional<ExchangeRate> findFirstByIsCurrentTrue();

    List<ExchangeRate> findAllByOrderByValidFromDesc();

    @Modifying
    @Query("update ExchangeRate er set er.isCurrent=false where er.isCurrent=true")
    int clearCurrent();
}

