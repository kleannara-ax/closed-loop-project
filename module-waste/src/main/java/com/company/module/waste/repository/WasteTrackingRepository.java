package com.company.module.waste.repository;

import com.company.module.waste.entity.WasteTracking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WasteTrackingRepository extends JpaRepository<WasteTracking, Long> {

    Optional<WasteTracking> findByTrackingCode(String trackingCode);

    Page<WasteTracking> findByIsDeletedFalse(Pageable pageable);

    Page<WasteTracking> findByStatusAndIsDeletedFalse(String status, Pageable pageable);

    Page<WasteTracking> findByWasteTypeAndIsDeletedFalse(String wasteType, Pageable pageable);

    long countByIsDeletedFalse();

    long countByStatusAndIsDeletedFalse(String status);

    @Query("SELECT COALESCE(SUM(t.weightKg), 0) FROM WasteTracking t WHERE t.isDeleted = false")
    BigDecimal sumTotalWeightKg();
}
