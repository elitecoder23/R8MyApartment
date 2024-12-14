package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import mk12.dto.MainLoginDTO;
import mk12.model.User;
import mk12.repository.IUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for authentication.
 * Provides an endpoint for user sign-in.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication",
        description = "API for user authentication operations")
@SecurityRequirements  // Indicates that these endpoints don't require authentication
public class AuthController {

    @Autowired
    private IUserRepository usernameRepo;

    @Operation(summary = "Sign in user",
            description = "Authenticates a user with their username and password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "Sign in successful"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "Invalid Credentials"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "Internal server error"
                            )
                    )
            )
    })
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(
            @Parameter(
                    description = "User credentials",
                    required = true,
                    schema = @Schema(implementation = MainLoginDTO.class)
            )
            @RequestBody MainLoginDTO loginDTO) {
        Optional<User> foundUser = usernameRepo.findByUsername(loginDTO.getUsername());

        if (foundUser.isPresent() && foundUser.get().getPassword().equals(loginDTO.getPassword())) {
            return ResponseEntity.ok("Sign in successful");
        }

        return ResponseEntity.status(401).body("Invalid Credentials");
    }
}