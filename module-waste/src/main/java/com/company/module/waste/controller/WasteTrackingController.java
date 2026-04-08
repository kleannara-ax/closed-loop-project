package com.company.module.waste.controller;

import com.company.module.waste.dto.WasteTrackingDto;
import com.company.module.waste.service.WasteTrackingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 폐기물 추적 REST Controller
 * API Prefix: /waste-api/**
 */
@RestController
@RequestMapping("/waste-api")
public class WasteTrackingController {

    private final WasteTrackingService svc;

    public WasteTrackingController(WasteTrackingService svc) {
        this.svc = svc;
    }

    @GetMapping("/trackings")
    public ResponseEntity<Page<WasteTrackingDto.ListResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String wasteType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(svc.getList(status, wasteType,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    @GetMapping("/trackings/{id}")
    public ResponseEntity<WasteTrackingDto.DetailResponse> detail(@PathVariable Long id) {
        return ResponseEntity.ok(svc.getDetail(id));
    }

    @PostMapping("/trackings")
    public ResponseEntity<WasteTrackingDto.ListResponse> create(@Valid @RequestBody WasteTrackingDto.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.create(req));
    }

    @PostMapping("/trackings/stage")
    public ResponseEntity<WasteTrackingDto.DetailResponse> stage(@Valid @RequestBody WasteTrackingDto.StageRequest req) {
        return ResponseEntity.ok(svc.advanceStage(req));
    }

    @DeleteMapping("/trackings/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        svc.delete(id);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "추적 ID " + id + " 삭제 완료");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<WasteTrackingDto.DashboardResponse> dashboard() {
        return ResponseEntity.ok(svc.dashboard());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> h = new LinkedHashMap<>();
        h.put("module", "waste");
        h.put("status", "UP");
        return ResponseEntity.ok(h);
    }
}
