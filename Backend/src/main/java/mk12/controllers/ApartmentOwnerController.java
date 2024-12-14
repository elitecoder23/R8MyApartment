package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mk12.dto.ApartmentOwnerDTO;
import mk12.dto.LoginDTO;
import mk12.dto.LoginResponse;
import mk12.service.ApartmentOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing apartment owners.
 * Provides endpoints for registering and logging in apartment owners.
 */
@RestController
@RequestMapping("/api/owners")
@CrossOrigin(origins = "*")
@Tag(name = "Apartment Owner Management",
        description = "APIs for apartment owner registration and authentication")
public class ApartmentOwnerController {

    @Autowired
    private ApartmentOwnerService ownerService;

    @Operation(summary = "Register new apartment owner",
            description = "Creates a new apartment owner account with the provided details and returns authentication information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Owner successfully registered",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data or username already exists",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registerOwner(
            @Parameter(description = "Owner registration details",
                    required = true,
                    schema = @Schema(implementation = ApartmentOwnerDTO.class))
            @RequestBody ApartmentOwnerDTO ownerDTO) {
        return ResponseEntity.ok(ownerService.registerOwner(ownerDTO));
    }

    @Operation(summary = "Login apartment owner",
            description = "Authenticates an existing apartment owner and returns authentication token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginOwner(
            @Parameter(description = "Owner login credentials",
                    required = true,
                    schema = @Schema(implementation = LoginDTO.class))
            @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(ownerService.loginOwner(loginDTO));
    }
}