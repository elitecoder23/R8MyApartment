package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mk12.model.Apartment;
import mk12.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing apartments.
 * Provides endpoints for creating, retrieving, updating, and deleting apartments.
 */
@RestController
@RequestMapping("/api/apartments")
@CrossOrigin(origins = "*")
@Tag(name = "Apartment Management", description = "APIs for managing apartment properties")
public class ApartmentController {

    @Autowired
    private ApartmentService apartmentService;

    @Operation(summary = "Create a new apartment",
            description = "Creates a new apartment entry with the specified details and assigns it to an owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Apartment created successfully",
                    content = @Content(schema = @Schema(implementation = Apartment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<Apartment> createApartment(
            @Parameter(description = "Apartment details", required = true)
            @RequestBody Apartment apartment,
            @Parameter(description = "Name of the apartment owner", required = true)
            @RequestParam String owner) {
        return ResponseEntity.ok(apartmentService.createApartment(apartment, owner));
    }

    @Operation(summary = "Get all apartments",
            description = "Retrieves a list of all apartments in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all apartments",
                    content = @Content(schema = @Schema(implementation = Apartment.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/showall")
    public ResponseEntity<List<Apartment>> getAllApartments() {
        return ResponseEntity.ok(apartmentService.getAllApartments());
    }

    @Operation(summary = "Delete an apartment",
            description = "Removes an apartment from the system by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Apartment successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Apartment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{name}/delete")
    public ResponseEntity<Void> deleteApartment(
            @Parameter(description = "Name of the apartment to delete", required = true)
            @PathVariable String name) {
        apartmentService.deleteApartment(name);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Find apartment by name",
            description = "Retrieves a specific apartment's details by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Apartment found",
                    content = @Content(schema = @Schema(implementation = Apartment.class))),
            @ApiResponse(responseCode = "404", description = "Apartment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/name/{apartmentName}")
    public ResponseEntity<Apartment> getApartmentByName(
            @Parameter(description = "Name of the apartment to search for", required = true)
            @PathVariable String apartmentName) {
        return ResponseEntity.ok(apartmentService.getApartmentByName(apartmentName));
    }

    @Operation(summary = "Find apartments by owner",
            description = "Retrieves all apartments belonging to a specific owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved owner's apartments",
                    content = @Content(schema = @Schema(implementation = Apartment.class))),
            @ApiResponse(responseCode = "404", description = "No apartments found for the owner"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/owner/name/{ownerName}")
    public ResponseEntity<List<Apartment>> getApartmentsByOwnerName(
            @Parameter(description = "Name of the owner whose apartments to retrieve", required = true)
            @PathVariable String ownerName) {
        return ResponseEntity.ok(apartmentService.getApartmentsByOwnerName(ownerName));
    }

    @Operation(summary = "Update apartment details",
            description = "Updates an existing apartment's information based on the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Apartment successfully updated",
                    content = @Content(schema = @Schema(implementation = Apartment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Apartment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update/{apartmentName}")
    public ResponseEntity<Apartment> updateApartment(
            @Parameter(description = "Name of the apartment to update", required = true)
            @PathVariable String apartmentName,
            @Parameter(description = "Updated apartment details", required = true)
            @RequestBody Apartment apartmentDetails) {
        Apartment updatedApartment = apartmentService.updateApartment(apartmentName, apartmentDetails);
        return ResponseEntity.ok(updatedApartment);
    }

}