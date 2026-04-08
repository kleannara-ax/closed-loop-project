package com.company.module.waste.service;

import com.company.core.exception.BusinessException;
import com.company.module.waste.dto.WasteTrackingDto;
import com.company.module.waste.entity.WasteStage;
import com.company.module.waste.entity.WasteTracking;
import com.company.module.waste.repository.WasteStageRepository;
import com.company.module.waste.repository.WasteTrackingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WasteTrackingService {

    private static final Logger log = LoggerFactory.getLogger(WasteTrackingService.class);
    private static final DateTimeFormatter CODE_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final WasteTrackingRepository trackingRepo;
    private final WasteStageRepository stageRepo;

    public WasteTrackingService(WasteTrackingRepository trackingRepo, WasteStageRepository stageRepo) {
        this.trackingRepo = trackingRepo;
        this.stageRepo = stageRepo;
    }

    /** 목록 조회 */
    @Transactional(readOnly = true)
    public Page<WasteTrackingDto.ListResponse> getList(String status, String wasteType, Pageable pageable) {
        Page<WasteTracking> page;
        if (status != null && !status.isBlank()) {
            page = trackingRepo.findByStatusAndIsDeletedFalse(status, pageable);
        } else if (wasteType != null && !wasteType.isBlank()) {
            page = trackingRepo.findByWasteTypeAndIsDeletedFalse(wasteType, pageable);
        } else {
            page = trackingRepo.findByIsDeletedFalse(pageable);
        }
        return page.map(this::toListResp);
    }

    /** 상세 조회 */
    @Transactional(readOnly = true)
    public WasteTrackingDto.DetailResponse getDetail(Long id) {
        WasteTracking t = findOrThrow(id);
        List<WasteStage> stages = stageRepo.findByTrackingIdOrderByProcessedAtAsc(id);

        WasteTrackingDto.DetailResponse d = new WasteTrackingDto.DetailResponse();
        d.setTrackingId(t.getTrackingId());
        d.setTrackingCode(t.getTrackingCode());
        d.setWasteType(t.getWasteType());
        d.setStatus(t.getStatus());
        d.setCenterName(t.getCenterName());
        d.setWeightKg(t.getWeightKg());
        d.setCreatedAt(t.getCreatedAt());
        d.setUpdatedAt(t.getUpdatedAt());
        d.setCompletedAt(t.getCompletedAt());
        d.setStages(stages.stream().map(this::toStageResp).collect(Collectors.toList()));
        return d;
    }

    /** 신규 등록 */
    @Transactional
    public WasteTrackingDto.ListResponse create(WasteTrackingDto.CreateRequest req) {
        log.info("폐기물 추적 생성: type={}, weight={}", req.getWasteType(), req.getWeightKg());

        WasteTracking t = new WasteTracking();
        t.setTrackingCode("WT-" + LocalDateTime.now().format(CODE_FMT) + "-" + String.format("%04d", (int)(Math.random()*10000)));
        t.setWasteType(req.getWasteType());
        t.setStatus("discharge");
        t.setCenterName(req.getCenterName());
        t.setWeightKg(req.getWeightKg());
        WasteTracking saved = trackingRepo.save(t);

        // 배출 단계 자동 기록
        WasteStage s = new WasteStage();
        s.setTrackingId(saved.getTrackingId());
        s.setStageType("discharge");
        s.setCenterName(req.getCenterName());
        s.setInputWeightKg(req.getWeightKg());
        s.setOutputWeightKg(req.getWeightKg());
        s.setMemo(req.getMemo());
        stageRepo.save(s);

        return toListResp(saved);
    }

    /** 단계 진행 */
    @Transactional
    public WasteTrackingDto.DetailResponse advanceStage(WasteTrackingDto.StageRequest req) {
        WasteTracking t = findOrThrow(req.getTrackingId());
        if ("completed".equals(t.getStatus())) throw new BusinessException("이미 완료된 추적입니다");

        log.info("단계 진행: id={}, stage={}", req.getTrackingId(), req.getStageType());

        WasteStage s = new WasteStage();
        s.setTrackingId(req.getTrackingId());
        s.setStageType(req.getStageType());
        s.setCenterName(req.getCenterName());
        s.setInputWeightKg(req.getInputWeightKg());
        s.setOutputWeightKg(req.getOutputWeightKg());
        s.setWorkerName(req.getWorkerName());
        s.setMemo(req.getMemo());
        stageRepo.save(s);

        t.setStatus(req.getStageType());
        if ("production".equals(req.getStageType()) || "completed".equals(req.getStageType())) {
            t.setStatus("completed");
            t.setCompletedAt(LocalDateTime.now());
        }
        trackingRepo.save(t);

        return getDetail(req.getTrackingId());
    }

    /** 삭제 (논리) */
    @Transactional
    public void delete(Long id) {
        WasteTracking t = findOrThrow(id);
        log.info("폐기물 추적 삭제: id={}", id);
        t.setIsDeleted(true);
        trackingRepo.save(t);
    }

    /** 대시보드 */
    @Transactional(readOnly = true)
    public WasteTrackingDto.DashboardResponse dashboard() {
        WasteTrackingDto.DashboardResponse d = new WasteTrackingDto.DashboardResponse();
        d.setTotalTracking(trackingRepo.countByIsDeletedFalse());
        d.setCompletedTracking(trackingRepo.countByStatusAndIsDeletedFalse("completed"));
        d.setActiveTracking(d.getTotalTracking() - d.getCompletedTracking());
        d.setTotalWeightKg(trackingRepo.sumTotalWeightKg());
        return d;
    }

    // ---- helpers ----
    private WasteTracking findOrThrow(Long id) {
        WasteTracking t = trackingRepo.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "NOT_FOUND", "추적 ID " + id + " 없음"));
        if (t.getIsDeleted()) throw new BusinessException(HttpStatus.NOT_FOUND, "DELETED", "삭제된 추적");
        return t;
    }

    private WasteTrackingDto.ListResponse toListResp(WasteTracking t) {
        return new WasteTrackingDto.ListResponse(
                t.getTrackingId(), t.getTrackingCode(), t.getWasteType(),
                t.getStatus(), t.getCenterName(), t.getWeightKg(), t.getCreatedAt());
    }

    private WasteTrackingDto.StageResponse toStageResp(WasteStage s) {
        WasteTrackingDto.StageResponse r = new WasteTrackingDto.StageResponse();
        r.setStageId(s.getStageId());
        r.setStageType(s.getStageType());
        r.setCenterName(s.getCenterName());
        r.setInputWeightKg(s.getInputWeightKg());
        r.setOutputWeightKg(s.getOutputWeightKg());
        r.setWorkerName(s.getWorkerName());
        r.setMemo(s.getMemo());
        r.setProcessedAt(s.getProcessedAt());
        return r;
    }
}
