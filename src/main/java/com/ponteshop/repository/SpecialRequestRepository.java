package com.ponteshop.repository;

import com.ponteshop.entity.SpecialRequest;
import com.ponteshop.enums.SpecialRequestStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpecialRequestRepository extends JpaRepository<SpecialRequest, UUID>, JpaSpecificationExecutor<SpecialRequest> {

    @EntityGraph(attributePaths = {"store"})
    List<SpecialRequest> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"store"})
    Optional<SpecialRequest> findByIdAndUser_Id(UUID id, UUID userId);

    long countByStatusIn(List<SpecialRequestStatus> statuses);
}
