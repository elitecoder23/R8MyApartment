package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mk12.dto.ReportDTO;
import mk12.model.Report;
import mk12.service.OwnerReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/owners/reports")
@CrossOrigin(origins = "*")
@Tag(name = "Owner Report Management", description = "APIs for owners to manage reports on their listings")
public class OwnerReportController {
    private static final Logger logger = LoggerFactory.getLogger(OwnerReportController.class);

    private final OwnerReportService ownerReportService;

    public OwnerReportController(OwnerReportService ownerReportService) {
        this.ownerReportService = ownerReportService;
    }

    @Operation(summary = "Get all reports for owner's listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    @GetMapping("/{ownerEmail}/all")
    public ResponseEntity<?> getAllOwnerReports(
            @Parameter(description = "Email of the apartment owner", required = true)
            @PathVariable String ownerEmail) {
        try {
            List<ReportDTO> reports = ownerReportService.getReportsByOwner(ownerEmail);
            return ResponseEntity.ok(createResponse(true, reports, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Get pending reports for owner's listings")
    @GetMapping("/{ownerEmail}/pending")
    public ResponseEntity<?> getPendingOwnerReports(
            @PathVariable String ownerEmail) {
        try {
            List<ReportDTO> reports = ownerReportService.getPendingReportsByOwner(ownerEmail);
            return ResponseEntity.ok(createResponse(true, reports, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Add owner response to a report")
    @PutMapping("/{reportId}/respond")
    public ResponseEntity<?> respondToReport(
            @PathVariable Long reportId,
            @RequestParam String ownerEmail,
            @RequestBody String response) {
        try {
            ReportDTO report = ownerReportService.addOwnerResponse(reportId, ownerEmail, response);
            return ResponseEntity.ok(createResponse(true, report, "Response added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Get reports by status for owner")
    @GetMapping("/{ownerEmail}/status/{status}")
    public ResponseEntity<?> getOwnerReportsByStatus(
            @PathVariable String ownerEmail,
            @PathVariable Report.ReportStatus status) {
        try {
            List<ReportDTO> reports = ownerReportService.getReportsByOwnerAndStatus(ownerEmail, status);
            return ResponseEntity.ok(createResponse(true, reports, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Get reports for specific apartment")
    @GetMapping("/apartment/{apartmentName}")
    public ResponseEntity<?> getReportsForApartment(
            @PathVariable String apartmentName,
            @RequestParam String ownerEmail) {
        try {
            List<ReportDTO> reports = ownerReportService.getReportsByApartmentAndOwner(
                    apartmentName, ownerEmail);
            return ResponseEntity.ok(createResponse(true, reports, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    private Map<String, Object> createResponse(boolean success, Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (data != null) response.put("data", data);
        if (message != null) response.put("message", message);
        return response;
    }
}