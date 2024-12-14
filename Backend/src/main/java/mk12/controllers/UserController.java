package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mk12.service.UserService;
import mk12.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * REST controller for managing users.
 * Provides endpoints for user signup, retrieval, update, deletion, sign-in, and adding friends.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "API endpoints for managing user accounts, authentication, and friend relationships")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Create new user account",
            description = "Creates a new user account with the provided user details including username, password, and profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data or username already exists",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while creating user",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        logger.info("Received signup request with data: {}", user);

        // Validate all required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return new ResponseEntity<>("Username is required", HttpStatus.BAD_REQUEST);
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return new ResponseEntity<>("Password is required", HttpStatus.BAD_REQUEST);
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return new ResponseEntity<>("Email is required", HttpStatus.BAD_REQUEST);
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            return new ResponseEntity<>("First name is required", HttpStatus.BAD_REQUEST);
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            return new ResponseEntity<>("Last name is required", HttpStatus.BAD_REQUEST);
        }

        try {
            // Check if username already exists
            if (userService.findByUsername(user.getUsername()) != null) {
                return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
            }

            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return new ResponseEntity<>("Error creating user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all registered users in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user list",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while retrieving users",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Received request to get all users");
        try {
            List<User> users = userService.getAllUsers();
            logger.info("Retrieved {} users", users.size());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving users", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Get user by username path",
            description = "Retrieves a specific user's details using their username as a path variable"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while retrieving user",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByPathVariable(
            @Parameter(description = "Username of the user to retrieve", required = true)
            @PathVariable String username) {
        logger.info("Received request to get user with path variable username: {}", username);
        return getUserResponseEntity(username);
    }

    @Operation(
            summary = "Search user by username query",
            description = "Searches for a user using their username as a query parameter"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while searching for user",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/search")
    public ResponseEntity<?> getUserByQueryParameter(
            @Parameter(description = "Username to search for", required = true)
            @RequestParam String username) {
        logger.info("Received request to get user with query parameter username: {}", username);
        return getUserResponseEntity(username);
    }

    @Operation(
            summary = "Update user profile",
            description = "Updates an existing user's profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while updating user",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "Username of the user to update", required = true)
            @PathVariable String username,
            @Parameter(description = "Updated user details", required = true)
            @RequestBody User user) {
        logger.info("Received request to update user with username: {}", username);
        try {
            User updatedUser = userService.updateUser(username, user);
            if (updatedUser != null) {
                logger.info("User updated successfully: {}", updatedUser.getUsername());
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            } else {
                logger.info("User not found with username: {}", username);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error updating user with username: {}", username, e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Delete user account",
            description = "Permanently deletes a user account and associated data"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while deleting user",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "Username of the user to delete", required = true)
            @PathVariable String username) {
        logger.info("Received request to delete user with username: {}", username);
        try {
            boolean deleted = userService.deleteUser(username);
            if (deleted) {
                logger.info("User deleted successfully: {}", username);
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                logger.info("User not found with username: {}", username);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error deleting user with username: {}", username, e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "User authentication",
            description = "Authenticates a user using their username and password"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during authentication",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/signin/{username}")
    public ResponseEntity<?> signIn(
            @Parameter(description = "Username for authentication", required = true)
            @RequestParam String username,
            @Parameter(description = "Password for authentication", required = true)
            @RequestParam String password) {
        logger.info("Received signin request for user: {}", username);
        try {
            User foundUser = userService.getUserByUsername(username);
            if (foundUser != null && foundUser.getPassword().equals(password)) {
                logger.info("User signed in successfully: {}", username);
                return new ResponseEntity<>("Sign in successful", HttpStatus.OK);
            } else {
                logger.info("Invalid credentials for user: {}", username);
                return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Error signing in user", e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Add friend relationship",
            description = "Creates a friend relationship between two users"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friend added successfully",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while adding friend",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping("/addFriend")
    public ResponseEntity<?> addFriend(
            @Parameter(description = "User initiating the friend request", required = true)
            @RequestBody User user,
            @Parameter(description = "Username of the friend to add", required = true)
            @RequestParam String friendUsername) {
        logger.info("Received request to add friend for user: {}", user.getUsername());
        try {
            User updatedUser = userService.addFriend(user.getUsername(), friendUsername);
            if (updatedUser != null) {
                logger.info("Friend added successfully for user: {}", user.getUsername());
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            } else {
                logger.info("User not found with username: {}", user.getUsername());
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error adding friend for user: {}", user.getUsername(), e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> getUserResponseEntity(String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user != null) {
                logger.info("Retrieved user: {}", user.getUsername());
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                logger.info("User not found with username: {}", username);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving user with username: {}", username, e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}