package com.company.module.waste.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 폐기물 추적 메인 엔티티
 * 배출 → 수거 → 압축 → 재활용 → 생산 전체 흐름의 마스터
 */
@Entity
@Table(name = "MOD_WASTE_TRACKING")
public class WasteTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRACKING_ID")
    private Long trackingId;

    @Column(name = "TRACKING_CODE", nullable = false, unique = true, length = 30)
    private String trackingCode;

    @Column(name = "WASTE_TYPE", nullable = false, length = 20)
    private String wasteType;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "CENTER_NAME", length = 100)
    private String centerName;

    @Column(name = "WEIGHT_KG", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "IS_DELETED", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isDeleted == null) this.isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getTrackingId() { return trackingId; }
    public void setTrackingId(Long trackingId) { this.trackingId = trackingId; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCenterName() { return centerName; }
    public void setCenterName(String centerName) { this.centerName = centerName; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
