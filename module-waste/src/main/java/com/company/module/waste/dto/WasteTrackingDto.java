package com.company.module.waste.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 폐기물 추적 DTO
 */
public class WasteTrackingDto {

    // ===== Request =====
    public static class CreateRequest {
        @NotBlank(message = "폐기물 유형은 필수입니다")
        private String wasteType;

        @NotNull(message = "중량은 필수입니다")
        private BigDecimal weightKg;

        private String centerName;
        private String memo;

        public String getWasteType() { return wasteType; }
        public void setWasteType(String wasteType) { this.wasteType = wasteType; }
        public BigDecimal getWeightKg() { return weightKg; }
        public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
        public String getCenterName() { return centerName; }
        public void setCenterName(String centerName) { this.centerName = centerName; }
        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }
    }

    public static class StageRequest {
        @NotNull(message = "추적 ID는 필수입니다")
        private Long trackingId;

        @NotBlank(message = "단계 유형은 필수입니다")
        private String stageType;

        private String centerName;
        private BigDecimal inputWeightKg;
        private BigDecimal outputWeightKg;
        private String workerName;
        private String memo;

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
    }

    // ===== Response =====
    public static class ListResponse {
        private Long trackingId;
        private String trackingCode;
        private String wasteType;
        private String status;
        private String centerName;
        private BigDecimal weightKg;
        private LocalDateTime createdAt;

        public ListResponse() {}
        public ListResponse(Long trackingId, String trackingCode, String wasteType,
                            String status, String centerName, BigDecimal weightKg, LocalDateTime createdAt) {
            this.trackingId = trackingId;
            this.trackingCode = trackingCode;
            this.wasteType = wasteType;
            this.status = status;
            this.centerName = centerName;
            this.weightKg = weightKg;
            this.createdAt = createdAt;
        }

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
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class DetailResponse extends ListResponse {
        private LocalDateTime updatedAt;
        private LocalDateTime completedAt;
        private List<StageResponse> stages;

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        public List<StageResponse> getStages() { return stages; }
        public void setStages(List<StageResponse> stages) { this.stages = stages; }
    }

    public static class StageResponse {
        private Long stageId;
        private String stageType;
        private String centerName;
        private BigDecimal inputWeightKg;
        private BigDecimal outputWeightKg;
        private String workerName;
        private String memo;
        private LocalDateTime processedAt;

        public Long getStageId() { return stageId; }
        public void setStageId(Long stageId) { this.stageId = stageId; }
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

    public static class DashboardResponse {
        private long totalTracking;
        private long completedTracking;
        private long activeTracking;
        private BigDecimal totalWeightKg;

        public long getTotalTracking() { return totalTracking; }
        public void setTotalTracking(long totalTracking) { this.totalTracking = totalTracking; }
        public long getCompletedTracking() { return completedTracking; }
        public void setCompletedTracking(long completedTracking) { this.completedTracking = completedTracking; }
        public long getActiveTracking() { return activeTracking; }
        public void setActiveTracking(long activeTracking) { this.activeTracking = activeTracking; }
        public BigDecimal getTotalWeightKg() { return totalWeightKg; }
        public void setTotalWeightKg(BigDecimal totalWeightKg) { this.totalWeightKg = totalWeightKg; }
    }
}
