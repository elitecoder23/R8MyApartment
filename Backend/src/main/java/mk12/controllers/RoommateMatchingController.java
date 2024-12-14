package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mk12.dto.MatchResultDTO;
import mk12.dto.QuizSubmissionDTO;
import mk12.service.RoommateMatchingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roommate-matching")
@CrossOrigin(origins = "*")
@Tag(name = "Roommate Matching", description = "API endpoints for roommate matching functionality including quiz submission, match finding, and compatibility calculations")
public class RoommateMatchingController {
    private static final Logger logger = LoggerFactory.getLogger(RoommateMatchingController.class);
    private final RoommateMatchingService matchingService;

    public RoommateMatchingController(RoommateMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @Operation(
            summary = "Submit roommate matching quiz",
            description = "Submits a new quiz containing lifestyle preferences, habits, and personality traits for roommate matching"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Quiz submitted successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid quiz submission data",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while processing quiz submission",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PostMapping("/submit-quiz")
    public ResponseEntity<?> submitQuiz(
            @Parameter(description = "Quiz submission data including username and answers", required = true)
            @RequestBody QuizSubmissionDTO submission) {
        try {
            logger.info("Receiving quiz submission for user: {}", submission.getUsername());
            matchingService.submitQuiz(submission);
            logger.info("Successfully submitted quiz for user: {}", submission.getUsername());
            return new ResponseEntity<>(
                    Map.of("message", "Quiz submitted successfully"),
                    HttpStatus.CREATED
            );
        } catch (RuntimeException e) {
            logger.error("Error submitting quiz", e);
            return new ResponseEntity<>(
                    Map.of("error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            logger.error("Unexpected error submitting quiz", e);
            return new ResponseEntity<>(
                    Map.of("error", "An unexpected error occurred"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Operation(
            summary = "Find potential roommate matches",
            description = "Retrieves a list of potential roommate matches based on quiz compatibility scores and preferences"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved matches",
                    content = @Content(schema = @Schema(implementation = MatchResultDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid username or no quiz submission found",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while finding matches",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/find-matches")
    public ResponseEntity<?> findMatches(
            @Parameter(description = "Username of the person seeking matches", required = true)
            @RequestParam String username) {
        try {
            logger.info("Finding matches for user: {}", username);
            List<MatchResultDTO> matches = matchingService.findMatches(username);
            logger.info("Found {} potential matches for user: {}", matches.size(), username);
            return ResponseEntity.ok(matches);
        } catch (RuntimeException e) {
            logger.error("Error finding matches for user: {}", username, e);
            return new ResponseEntity<>(
                    Map.of("error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            logger.error("Unexpected error finding matches for user: {}", username, e);
            return new ResponseEntity<>(
                    Map.of("error", "An unexpected error occurred"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Operation(
            summary = "Get quiz submission",
            description = "Retrieves the existing quiz submission for a specific user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved quiz submission",
                    content = @Content(schema = @Schema(implementation = QuizSubmissionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quiz submission not found for the specified user"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while retrieving quiz submission",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/quiz-submission/{username}")
    public ResponseEntity<?> getQuizSubmissionByUsername(
            @Parameter(description = "Username of the quiz submission owner", required = true)
            @PathVariable String username) {
        try {
            logger.info("Retrieving quiz submission for user: {}", username);
            QuizSubmissionDTO submission = matchingService.getQuizSubmissionByUsername(username);
            if (submission != null) {
                logger.info("Successfully retrieved quiz submission for user: {}", username);
                return ResponseEntity.ok(submission);
            } else {
                logger.warn("Quiz submission not found for user: {}", username);
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                logger.warn("Quiz submission not found for user: {}", username);
                return ResponseEntity.notFound().build();
            }
            logger.error("Runtime error retrieving quiz submission for user: {}", username, e);
            return new ResponseEntity<>(
                    Map.of("error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            logger.error("Unexpected error retrieving quiz submission for user: {}", username, e);
            return new ResponseEntity<>(
                    Map.of("error", "Failed to retrieve quiz submission"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Operation(
            summary = "Delete quiz submission",
            description = "Removes an existing quiz submission for a specific user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quiz submission deleted successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quiz submission not found for deletion"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while deleting quiz submission",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @DeleteMapping("/quiz-submission/{username}")
    public ResponseEntity<?> deleteQuizSubmissionByUsername(
            @Parameter(description = "Username of the quiz submission to delete", required = true)
            @PathVariable String username) {
        try {
            logger.info("Deleting quiz submission for user: {}", username);
            boolean deleted = matchingService.deleteQuizSubmissionByUsername(username);
            if (deleted) {
                logger.info("Successfully deleted quiz submission for user: {}", username);
                return ResponseEntity.ok(Map.of("message", "Quiz submission deleted successfully"));
            } else {
                logger.warn("Quiz submission not found for deletion: {}", username);
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                logger.warn("Quiz submission not found for user: {}", username);
                return ResponseEntity.notFound().build();
            }
            logger.error("Runtime error deleting quiz submission for user: {}", username, e);
            return new ResponseEntity<>(
                    Map.of("error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            logger.error("Error deleting quiz submission for user: {}", username, e);
            return new ResponseEntity<>(
                    Map.of("error", "Failed to delete quiz submission"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Operation(
            summary = "Update quiz submission",
            description = "Updates an existing quiz submission with new answers and preferences"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quiz submission updated successfully",
                    content = @Content(schema = @Schema(implementation = QuizSubmissionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quiz submission not found for update"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while updating quiz submission",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PutMapping("/quiz-submission/{username}")
    public ResponseEntity<?> updateQuizSubmissionByUsername(
            @Parameter(description = "Username of the quiz submission to update", required = true)
            @PathVariable String username,
            @Parameter(description = "Updated quiz submission data", required = true)
            @RequestBody QuizSubmissionDTO updatedSubmission) {
        try {
            logger.info("Updating quiz submission for user: {}", username);
            updatedSubmission.setUsername(username);
            QuizSubmissionDTO updated = matchingService.updateQuizSubmissionByUsername(username, updatedSubmission);
            if (updated != null) {
                logger.info("Successfully updated quiz submission for user: {}", username);
                return ResponseEntity.ok(updated);
            } else {
                logger.warn("Quiz submission not found for update: {}", username);
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                logger.warn("Quiz submission not found for user: {}", username);
                return ResponseEntity.notFound().build();
            }
            logger.error("Runtime error updating quiz submission for user: {}", username, e);
            return new ResponseEntity<>(
                    Map.of("error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            logger.error("Error updating quiz submission for user: {}", username, e);
            return new ResponseEntity<>(
                    Map.of("error", "Failed to update quiz submission"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Operation(
            summary = "Calculate match compatibility",
            description = "Calculates the compatibility percentage between two specific users based on their quiz submissions"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully calculated match compatibility",
                    content = @Content(schema = @Schema(implementation = MatchResultDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid usernames or missing quiz submissions",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while calculating match",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/calculate-match")
    public ResponseEntity<?> calculateMatch(
            @Parameter(description = "Username of the first user", required = true)
            @RequestParam String username1,
            @Parameter(description = "Username of the second user", required = true)
            @RequestParam String username2) {
        try {
            logger.info("Calculating match between users: {} and {}", username1, username2);
            MatchResultDTO match = matchingService.calculateSingleMatch(username1, username2);
            logger.info("Successfully calculated match between users: {} and {}", username1, username2);
            return ResponseEntity.ok(match);
        } catch (RuntimeException e) {
            logger.error("Error calculating match between users: {} and {}", username1, username2, e);
            return new ResponseEntity<>(
                    Map.of("error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            logger.error("Unexpected error calculating match", e);
            return new ResponseEntity<>(
                    Map.of("error", "An unexpected error occurred"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}