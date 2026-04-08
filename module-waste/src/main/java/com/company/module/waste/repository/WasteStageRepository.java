package com.company.module.waste.repository;

import com.company.module.waste.entity.WasteStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WasteStageRepository extends JpaRepository<WasteStage, Long> {

    List<WasteStage> findByTrackingIdOrderByProcessedAtAsc(Long trackingId);

    long countByTrackingId(Long trackingId);
}
