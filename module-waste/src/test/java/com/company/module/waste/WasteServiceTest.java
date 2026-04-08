package com.company.module.waste;

import com.company.module.waste.dto.WasteTrackingDto;
import com.company.module.waste.entity.WasteTracking;
import com.company.module.waste.repository.WasteStageRepository;
import com.company.module.waste.repository.WasteTrackingRepository;
import com.company.module.waste.service.WasteTrackingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Service 레이어 통합 테스트
 * - 자체 H2 인메모리 DB 생성 및 연결 (application-test.yml)
 * - Core 모듈에 의존하지 않는 독립 테스트 환경 (WasteModuleTestConfig)
 */
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = WasteModuleTestConfig.class)
@Transactional
class WasteServiceTest {

    @Autowired private WasteTrackingService service;
    @Autowired private WasteTrackingRepository trackingRepo;
    @Autowired private WasteStageRepository stageRepo;

    @Test @DisplayName("신규 추적 생성")
    void createTracking() {
        WasteTrackingDto.CreateRequest req = new WasteTrackingDto.CreateRequest();
        req.setWasteType("paper");
        req.setWeightKg(new BigDecimal("1500.00"));
        req.setCenterName("테스트센터");
        req.setMemo("테스트 메모");

        WasteTrackingDto.ListResponse resp = service.create(req);

        assertNotNull(resp.getTrackingId());
        assertTrue(resp.getTrackingCode().startsWith("WT-"));
        assertEquals("paper", resp.getWasteType());
        assertEquals("discharge", resp.getStatus());
        assertEquals(new BigDecimal("1500.00"), resp.getWeightKg());

        // 배출 단계가 자동 기록되었는지 확인
        assertEquals(1, stageRepo.countByTrackingId(resp.getTrackingId()));
    }

    @Test @DisplayName("목록 조회 - 전체")
    void listAll() {
        createSample("paper", "1000");
        createSample("vinyl", "2000");
        createSample("metal", "500");

        Page<WasteTrackingDto.ListResponse> page = service.getList(null, null, PageRequest.of(0, 10));
        assertEquals(3, page.getTotalElements());
    }

    @Test @DisplayName("목록 조회 - 상태 필터")
    void listByStatus() {
        WasteTrackingDto.ListResponse t1 = createSample("paper", "1000");
        createSample("vinyl", "2000");

        // t1을 collection 단계로 진행
        WasteTrackingDto.StageRequest stageReq = new WasteTrackingDto.StageRequest();
        stageReq.setTrackingId(t1.getTrackingId());
        stageReq.setStageType("collection");
        stageReq.setInputWeightKg(new BigDecimal("1000"));
        stageReq.setOutputWeightKg(new BigDecimal("990"));
        service.advanceStage(stageReq);

        Page<WasteTrackingDto.ListResponse> page = service.getList("collection", null, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals("collection", page.getContent().get(0).getStatus());
    }

    @Test @DisplayName("상세 조회 - 단계 포함")
    void detailWithStages() {
        WasteTrackingDto.ListResponse created = createSample("paper", "2000");

        WasteTrackingDto.DetailResponse detail = service.getDetail(created.getTrackingId());
        assertNotNull(detail);
        assertEquals(created.getTrackingId(), detail.getTrackingId());
        assertEquals(1, detail.getStages().size()); // 배출 단계 1건
        assertEquals("discharge", detail.getStages().get(0).getStageType());
    }

    @Test @DisplayName("단계 진행 - 완료까지")
    void advanceToCompletion() {
        WasteTrackingDto.ListResponse created = createSample("paper", "2000");
        Long id = created.getTrackingId();

        // collection 단계
        advanceStage(id, "collection", "2000", "1980");
        // compression 단계
        advanceStage(id, "compression", "1980", "1600");
        // recycling 단계
        advanceStage(id, "recycling", "1600", "1400");
        // production (완료) 단계
        WasteTrackingDto.DetailResponse detail = advanceStage(id, "production", "1400", "1200");

        assertEquals("completed", detail.getStatus());
        assertNotNull(detail.getCompletedAt());
        assertEquals(5, detail.getStages().size()); // discharge + 4 단계
    }

    @Test @DisplayName("논리 삭제")
    void deleteTracking() {
        WasteTrackingDto.ListResponse created = createSample("paper", "500");
        service.delete(created.getTrackingId());

        // 삭제 후 목록에서 제외
        Page<WasteTrackingDto.ListResponse> page = service.getList(null, null, PageRequest.of(0, 10));
        assertEquals(0, page.getTotalElements());
    }

    @Test @DisplayName("대시보드 통계")
    void dashboard() {
        createSample("paper", "1000.00");
        createSample("vinyl", "500.50");
        WasteTrackingDto.ListResponse t3 = createSample("metal", "200.00");

        // t3을 완료 처리
        advanceStage(t3.getTrackingId(), "collection", "200", "190");
        advanceStage(t3.getTrackingId(), "compression", "190", "150");
        advanceStage(t3.getTrackingId(), "recycling", "150", "130");
        advanceStage(t3.getTrackingId(), "production", "130", "110");

        WasteTrackingDto.DashboardResponse dash = service.dashboard();
        assertEquals(3, dash.getTotalTracking());
        assertEquals(1, dash.getCompletedTracking());
        assertEquals(2, dash.getActiveTracking());
        assertEquals(new BigDecimal("1700.50"), dash.getTotalWeightKg());
    }

    // === helpers ===
    private WasteTrackingDto.ListResponse createSample(String type, String weight) {
        WasteTrackingDto.CreateRequest req = new WasteTrackingDto.CreateRequest();
        req.setWasteType(type);
        req.setWeightKg(new BigDecimal(weight));
        req.setCenterName("테스트센터");
        return service.create(req);
    }

    private WasteTrackingDto.DetailResponse advanceStage(Long trackingId, String stageType, String in, String out) {
        WasteTrackingDto.StageRequest req = new WasteTrackingDto.StageRequest();
        req.setTrackingId(trackingId);
        req.setStageType(stageType);
        req.setInputWeightKg(new BigDecimal(in));
        req.setOutputWeightKg(new BigDecimal(out));
        req.setWorkerName("테스트작업자");
        return service.advanceStage(req);
    }
}
