package com.company.module.waste.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 폐기물 처리 단계 기록 엔티티
 * 각 단계(배출, 수거, 압축, 재활용, 생산)마다 1건씩 기록
 */
@Entity
@Table(name = "MOD_WASTE_STAGE")
public class WasteStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STAGE_ID")
    private Long stageId;

    @Column(name = "TRACKING_ID", nullable = false)
    private Long trackingId;

    @Column(name = "STAGE_TYPE", nullable = false, length = 20)
    private String stageType;

    @Column(name = "CENTER_NAME", length = 100)
    private String centerName;

    @Column(name = "INPUT_WEIGHT_KG", precision = 10, scale = 2)
    private BigDecimal inputWeightKg;

    @Column(name = "OUTPUT_WEIGHT_KG", precision = 10, scale = 2)
    private BigDecimal outputWeightKg;

    @Column(name = "WORKER_NAME", length = 50)
    private String workerName;

    @Column(name = "MEMO", length = 500)
    private String memo;

    @Column(name = "PROCESSED_AT", nullable = false)
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        if (this.processedAt == null) this.processedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public Long getTrackingId() { return trackingId; }
    public void setTrackingId(Long trackingId) { this.trackingId = trackingId; }
    public String getStageType() { return stageType; }
    public void setStageType(String stageType) { this.stageType = stageType; }
    public String getCenterName() { return centerName; }
    public void setCenterName(String centerName) { this.centerName = centerName; }
    public BigDecimal getInputWeightKg() { return inputWeightKg; }
    public void setInputWeightKg(BigDecimal inputWeightKg) { this.inputWeightKg = inputWeightKg; }
    public BigDecimal getOutputWeightKg() { return outputWeightKg; }
    public void setOutputWeightKg(BigDecimal outputWeightKg) { this.outputWeightKg = outputWeightKg; }
    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
