package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mk12.model.Report;
import mk12.model.Apartment;
import mk12.service.ListingReportService;
import mk12.dto.ReportDTO;
import mk12.repository.IApartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/listing-reports")
@CrossOrigin(origins = "*")
@Tag(name = "Listing Reports", description = "APIs for managing apartment listing reports")
public class ListingReportController {
    private static final Logger logger = LoggerFactory.getLogger(ListingReportController.class);

    private final ListingReportService reportService;
    private final IApartmentRepository apartmentRepository;

    public ListingReportController(ListingReportService reportService,
                                   IApartmentRepository apartmentRepository) {
        this.reportService = reportService;
        this.apartmentRepository = apartmentRepository;
    }

    @Operation(summary = "Submit a report for an apartment listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report submitted successfully"),
            @ApiResponse(responseCode = "404", description = "Apartment not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/submit/{apartmentName}")
    public ResponseEntity<?> submitReport(
            @Parameter(description = "Name of the apartment being reported", required = true)
            @PathVariable String apartmentName,
            @Parameter(description = "Username of the person reporting", required = true)
            @RequestParam String reporterUsername,
            @Parameter(description = "Email of the person reporting", required = true)
            @RequestParam String reporterEmail,
            @Parameter(description = "Reason for reporting", required = true)
            @RequestBody String reason) {
        try {
            logger.info("Receiving report submission for apartment: {} from user: {}",
                    apartmentName, reporterUsername);

            Apartment apartment = apartmentRepository.findByName(apartmentName)
                    .orElseThrow(() -> new RuntimeException("Apartment not found: " + apartmentName));

            ReportDTO report = new ReportDTO();
            report.setReporterUsername(reporterUsername);
            report.setReporterEmail(reporterEmail);
            report.setReason(reason);
            report.setApartmentName(apartmentName);

            ReportDTO savedReport = reportService.submitReport(apartment, report);

            logger.info("Successfully submitted report for apartment: {}", apartmentName);
            return ResponseEntity.ok(createResponse(true, savedReport,
                    "Report submitted successfully"));
        } catch (Exception e) {
            logger.error("Error submitting report: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Get report by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report found successfully"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @GetMapping("/{reportId}")
    public ResponseEntity<?> getReport(
            @Parameter(description = "ID of the report", required = true)
            @PathVariable Long reportId) {
        try {
            ReportDTO report = reportService.getReportById(reportId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Get all reports for an apartment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Apartment not found")
    })
    @GetMapping("/apartment/{apartmentName}")
    public ResponseEntity<?> getReportsByApartment(
            @Parameter(description = "Name of the apartment", required = true)
            @PathVariable String apartmentName) {
        try {
            return ResponseEntity.ok(reportService.getReportsByApartmentName(apartmentName));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Get reports by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getReportsByStatus(
            @Parameter(description = "Status of the reports to retrieve", required = true)
            @PathVariable Report.ReportStatus status) {
        try {
            return ResponseEntity.ok(reportService.getReportsByStatus(status));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Update report status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @PutMapping("/{reportId}/status")
    public ResponseEntity<?> updateReportStatus(
            @Parameter(description = "ID of the report", required = true)
            @PathVariable Long reportId,
            @Parameter(description = "New status for the report", required = true)
            @RequestParam Report.ReportStatus status) {
        try {
            ReportDTO report = reportService.updateReportStatus(reportId, status);
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