package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mk12.model.ApartmentApplication;
import mk12.service.ApartmentApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
@Tag(name = "Apartment Applications", description = "APIs for managing apartment applications")
public class ApartmentApplicationController {

    @Autowired
    private ApartmentApplicationService applicationService;

    private Map<String, Object> createResponse(boolean success, Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (data != null) response.put("data", data);
        if (message != null) response.put("message", message);
        return response;
    }

    @Operation(summary = "Submit application for an apartment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate application"),
            @ApiResponse(responseCode = "404", description = "Apartment or user not found")
    })
    @PostMapping("/submit/{apartmentName}/{username}")
    public ResponseEntity<?> submitApplication(
            @Parameter(description = "Name of the apartment")
            @PathVariable String apartmentName,
            @Parameter(description = "Username of the applicant")
            @PathVariable String username,
            @RequestBody ApartmentApplication application) {
        try {
            ApartmentApplication submitted =
                    applicationService.submitApplication(apartmentName, username, application);
            return ResponseEntity.ok(createResponse(true, submitted,
                    "Application submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Update application status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PutMapping("/{apartmentName}/{username}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @Parameter(description = "Name of the apartment")
            @PathVariable String apartmentName,
            @Parameter(description = "Username of the applicant")
            @PathVariable String username,
            @Parameter(description = "New status (ACCEPTED/REJECTED)")
            @RequestParam String status) {
        try {
            ApartmentApplication.ApplicationStatus newStatus =
                    ApartmentApplication.ApplicationStatus.valueOf(status.toUpperCase());
            ApartmentApplication updated = applicationService.updateApplicationStatus(
                    apartmentName, username, newStatus);
            return ResponseEntity.ok(createResponse(true, updated,
                    "Application status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Get user's applications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserApplications(
            @Parameter(description = "Username of the applicant")
            @PathVariable String username) {
        try {
            return ResponseEntity.ok(createResponse(true,
                    applicationService.getUserApplications(username), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Get owner's received applications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    @GetMapping("/owner/{ownerEmail}")
    public ResponseEntity<?> getOwnerApplications(
            @Parameter(description = "Email of the apartment owner")
            @PathVariable String ownerEmail) {
        try {
            return ResponseEntity.ok(createResponse(true,
                    applicationService.getOwnerApplications(ownerEmail), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }

    @Operation(summary = "Get applications for a specific apartment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Apartment not found")
    })
    @GetMapping("/apartment/{apartmentName}")
    public ResponseEntity<?> getApartmentApplications(
            @Parameter(description = "Name of the apartment")
            @PathVariable String apartmentName) {
        try {
            return ResponseEntity.ok(createResponse(true,
                    applicationService.getApplicationsForApartment(apartmentName), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createResponse(false, null, e.getMessage()));
        }
    }
}