package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mk12.dto.ReviewReportDTO;
import mk12.model.ReviewReport;
import mk12.service.ReviewReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/review-reports")
@CrossOrigin(origins = "*")
@Tag(name = "Review Reports", description = "APIs for managing review reports")
public class ReviewReportController {

    private final ReviewReportService reportService;

    public ReviewReportController(ReviewReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Submit a report for a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report submitted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/submit")
    public ResponseEntity<?> submitReport(
            @Parameter(description = "ID of the review being reported", required = true)
            @RequestParam int reviewId,
            @Parameter(description = "Username of the person reporting", required = true)
            @RequestParam String reporterUsername,
            @Parameter(description = "Reason for reporting", required = true)
            @RequestBody String reason) {
        try {
            ReviewReportDTO report = reportService.submitReport(reviewId, reporterUsername, reason);
            return ResponseEntity.ok(createResponse(true, report, "Report submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @GetMapping("/owner/{ownerEmail}")
    public ResponseEntity<?> getOwnerReports(
            @PathVariable String ownerEmail) {
        try {
            List<ReviewReportDTO> reports = reportService.getReportsByOwner(ownerEmail);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getReportsByStatus(
            @PathVariable ReviewReport.ReportStatus status) {
        try {
            List<ReviewReportDTO> reports = reportService.getReportsByStatus(status);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @PutMapping("/{reportId}/status")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable Long reportId,
            @RequestParam ReviewReport.ReportStatus status) {
        try {
            ReviewReportDTO report = reportService.updateReportStatus(reportId, status);
            return ResponseEntity.ok(report);
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