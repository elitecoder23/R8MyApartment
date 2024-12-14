package mk12.controllers;

import mk12.dto.AdminLoginDTO;
import mk12.dto.LoginResponse;
import mk12.dto.ReportDTO;
import mk12.model.Report;
import mk12.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private ResponseEntity<Map<String, Object>> createResponse(boolean success, Object data, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (data != null) response.put("data", data);
        if (message != null) response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signupAdmin(@RequestBody AdminLoginDTO signupDTO) {
        try {
            logger.info("Attempting to create admin account for: {}", signupDTO.getEmail());
            LoginResponse response = adminService.createAdmin(signupDTO);
            return createResponse(true, response, "Admin account created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating admin account: {}", e.getMessage());
            return createResponse(false, null, e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginAdmin(@RequestBody AdminLoginDTO loginDTO) {
        try {
            logger.info("Admin login attempt for: {}", loginDTO.getEmail());
            LoginResponse response = adminService.loginAdmin(loginDTO);
            return createResponse(true, response, "Login successful", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Admin login failed: {}", e.getMessage());
            return createResponse(false, null, e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            logger.info("Fetching all reports, page: {}, size: {}", page, size);
            Page<ReportDTO> reports = adminService.getAllReports(PageRequest.of(page, size));
            return createResponse(true, reports, null, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching reports: {}", e.getMessage());
            return createResponse(false, null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reports/pending")
    public ResponseEntity<Map<String, Object>> getPendingReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            logger.info("Fetching pending reports, page: {}, size: {}", page, size);
            Page<ReportDTO> reports = adminService.getReportsByStatus(Report.ReportStatus.PENDING, PageRequest.of(page, size));
            return createResponse(true, reports, null, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching pending reports: {}", e.getMessage());
            return createResponse(false, null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/reports/{id}/review")
    public ResponseEntity<Map<String, Object>> reviewReport(
            @PathVariable Long id,
            @RequestParam Report.ReportStatus status) {
        try {
            logger.info("Updating report status. ID: {}, New Status: {}", id, status);
            ReportDTO report = adminService.updateReportStatus(id, status);
            return createResponse(true, report, "Report status updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating report status: {}", e.getMessage());
            return createResponse(false, null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/apartments/{apartmentName}")
    public ResponseEntity<Map<String, Object>> deleteApartment(@PathVariable String apartmentName) {
        try {
            logger.info("Attempting to delete apartment: {}", apartmentName);
            adminService.deleteApartment(apartmentName);
            return createResponse(true, null, "Apartment deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting apartment: {}", e.getMessage());
            return createResponse(false, null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}