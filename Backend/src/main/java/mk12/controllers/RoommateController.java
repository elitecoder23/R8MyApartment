package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mk12.service.RoommateService;
import mk12.model.Roommate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing roommates.
 * Provides endpoints for creating, reading, updating, deleting, and searching roommates.
 */
@RestController
@RequestMapping("/roommates")
@Tag(name = "Roommate Management",
        description = "API endpoints for managing roommate profiles, including creation, updates, searches, and deletions")
public class RoommateController {

    @Autowired
    private RoommateService roommateService;

    @Operation(
            summary = "Create new roommate profile",
            description = "Creates a new roommate profile with details such as preferences, habits, and contact information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Roommate profile created successfully",
                    content = @Content(schema = @Schema(implementation = Roommate.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid roommate data provided",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while creating roommate profile",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping
    public ResponseEntity<Roommate> createRoommate(
            @Parameter(description = "Roommate profile details to be created", required = true)
            @RequestBody Roommate roommate) {
        Roommate createdRoommate = roommateService.createRoommate(roommate);
        return ResponseEntity.ok(createdRoommate);
    }

    @Operation(
            summary = "Get all roommate profiles",
            description = "Retrieves a list of all registered roommate profiles in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of roommates",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while retrieving roommates",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Roommate>> getAllRoommates() {
        List<Roommate> roommates = roommateService.getAllRoommates();
        return ResponseEntity.ok(roommates);
    }

    @Operation(
            summary = "Get roommate by name",
            description = "Retrieves a specific roommate profile using their name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Roommate found successfully",
                    content = @Content(schema = @Schema(implementation = Roommate.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Roommate not found with the provided name",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while retrieving roommate",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/{name}")
    public ResponseEntity<Roommate> getRoommateByName(
            @Parameter(description = "Name of the roommate to retrieve", required = true)
            @PathVariable String name) {
        Roommate roommate = roommateService.getRoommateByName(name);
        if (roommate != null) {
            return ResponseEntity.ok(roommate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Update roommate profile",
            description = "Updates an existing roommate's profile information including preferences and details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Roommate profile updated successfully",
                    content = @Content(schema = @Schema(implementation = Roommate.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Roommate not found with the provided name",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid update data provided",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while updating roommate",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PutMapping("/{name}")
    public ResponseEntity<Roommate> updateRoommate(
            @Parameter(description = "Name of the roommate to update", required = true)
            @PathVariable String name,
            @Parameter(description = "Updated roommate profile details", required = true)
            @RequestBody Roommate roommateDetails) {
        Roommate updatedRoommate = roommateService.updateRoommate(name, roommateDetails);
        if (updatedRoommate != null) {
            return ResponseEntity.ok(updatedRoommate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Delete roommate profile",
            description = "Permanently removes a roommate profile from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Roommate deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Roommate not found with the provided name",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while deleting roommate",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteRoommate(
            @Parameter(description = "Name of the roommate to delete", required = true)
            @PathVariable String name) {
        boolean deleted = roommateService.deleteRoommate(name);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Search roommates",
            description = "Searches for roommate profiles by name with option for exact or partial matching"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved matching roommates",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search parameters",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while searching roommates",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/search")
    public ResponseEntity<List<Roommate>> searchRoommates(
            @Parameter(description = "Name to search for", required = true)
            @RequestParam String name,
            @Parameter(description = "Whether to perform exact name matching (true) or partial matching (false)", required = false)
            @RequestParam(required = false) Boolean exact) {
        List<Roommate> roommates;
        if (exact != null && exact) {
            roommates = roommateService.getRoommatesByExactName(name);
        } else {
            roommates = roommateService.searchRoommates(name);
        }
        return ResponseEntity.ok(roommates);
    }
}