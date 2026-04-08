package com.company.module.waste;

import com.company.module.waste.entity.WasteStage;
import com.company.module.waste.entity.WasteTracking;
import com.company.module.waste.repository.WasteStageRepository;
import com.company.module.waste.repository.WasteTrackingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Entity + Repository 단위 테스트
 * - 자체 H2 인메모리 DB 자동 생성 (@DataJpaTest)
 */
@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = WasteModuleTestConfig.class)
class WasteEntityTest {

    @Autowired private WasteTrackingRepository trackingRepo;
    @Autowired private WasteStageRepository stageRepo;

    @Test @DisplayName("추적 저장 및 조회")
    void saveAndFind() {
        WasteTracking t = mkTracking("WT-T01", "paper", "discharge", "1500.50");
        assertNotNull(t.getTrackingId());
        assertNotNull(t.getCreatedAt());
        assertFalse(t.getIsDeleted());
    }

    @Test @DisplayName("추적코드 조회")
    void findByCode() {
        mkTracking("WT-T02", "vinyl", "collection", "800");
        Optional<WasteTracking> found = trackingRepo.findByTrackingCode("WT-T02");
        assertTrue(found.isPresent());
        assertEquals("vinyl", found.get().getWasteType());
    }

    @Test @DisplayName("상태별 필터링")
    void filterByStatus() {
        mkTracking("WT-S1", "paper", "discharge", "100");
        mkTracking("WT-S2", "vinyl", "discharge", "200");
        mkTracking("WT-S3", "metal", "completed", "300");
        var page = trackingRepo.findByStatusAndIsDeletedFalse("discharge",
                org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(2, page.getTotalElements());
    }

    @Test @DisplayName("논리 삭제")
    void logicalDelete() {
        WasteTracking t = mkTracking("WT-D1", "paper", "discharge", "500");
        t.setIsDeleted(true);
        trackingRepo.save(t);
        assertEquals(0, trackingRepo.countByIsDeletedFalse());
    }

    @Test @DisplayName("총 중량 합계")
    void sumWeight() {
        mkTracking("WT-W1", "paper", "discharge", "1000.00");
        mkTracking("WT-W2", "vinyl", "collection", "500.50");
        assertEquals(new BigDecimal("1500.50"), trackingRepo.sumTotalWeightKg());
    }

    @Test @DisplayName("단계 저장 및 조회")
    void saveStage() {
        WasteTracking t = mkTracking("WT-STG1", "paper", "discharge", "1000");
        WasteStage s = mkStage(t.getTrackingId(), "discharge", "1000", "1000");
        assertNotNull(s.getStageId());
        assertNotNull(s.getProcessedAt());
    }

    @Test @DisplayName("추적ID별 단계 목록")
    void stagesByTracking() {
        WasteTracking t = mkTracking("WT-STG2", "vinyl", "compression", "800");
        Long tid = t.getTrackingId();
        mkStage(tid, "discharge", "800", "800");
        mkStage(tid, "collection", "800", "790");
        mkStage(tid, "compression", "790", "650");
        List<WasteStage> stages = stageRepo.findByTrackingIdOrderByProcessedAtAsc(tid);
        assertEquals(3, stages.size());
    }

    @Test @DisplayName("전체 순환 흐름")
    void fullLifecycle() {
        WasteTracking t = mkTracking("WT-LIFE", "paper", "discharge", "2000");
        Long tid = t.getTrackingId();
        mkStage(tid, "discharge", "2000", "2000");
        t.setStatus("collection"); trackingRepo.save(t);
        mkStage(tid, "collection", "2000", "1980");
        t.setStatus("compression"); trackingRepo.save(t);
        mkStage(tid, "compression", "1980", "1600");
        t.setStatus("recycling"); trackingRepo.save(t);
        mkStage(tid, "recycling", "1600", "1400");
        t.setStatus("completed"); trackingRepo.save(t);
        mkStage(tid, "production", "1400", "1200");

        assertEquals("completed", trackingRepo.findById(tid).get().getStatus());
        assertEquals(5, stageRepo.findByTrackingIdOrderByProcessedAtAsc(tid).size());
    }

    // helpers
    private WasteTracking mkTracking(String code, String type, String status, String weight) {
        WasteTracking t = new WasteTracking();
        t.setTrackingCode(code); t.setWasteType(type); t.setStatus(status);
        t.setWeightKg(new BigDecimal(weight));
        return trackingRepo.save(t);
    }
    private WasteStage mkStage(Long tid, String type, String in, String out) {
        WasteStage s = new WasteStage();
        s.setTrackingId(tid); s.setStageType(type);
        s.setInputWeightKg(new BigDecimal(in)); s.setOutputWeightKg(new BigDecimal(out));
        s.setWorkerName("테스트"); return stageRepo.save(s);
    }
}
