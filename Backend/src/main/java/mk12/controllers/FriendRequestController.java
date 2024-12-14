package mk12.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import mk12.model.Friend;
import mk12.repository.FriendRepository;
import mk12.repository.FriendRequestRepository;
import mk12.service.FriendRequestService;
import mk12.model.FriendRequest;
import mk12.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST controller for managing friend requests.
 * Provides endpoints for sending, accepting, denying, and retrieving friend requests.
 */
@RestController
@RequestMapping("/api/friend-requests")
@Tag(name = "Friend Requests",
        description = "APIs for managing friend requests between users")
@SecurityRequirement(name = "bearerAuth")  // Assumes JWT authentication
public class FriendRequestController {
    private final FriendService friendService;
    private final FriendRequestService friendRequestService;
    private final FriendRepository friendRepository;
    /**
     * Constructor to initialize FriendRequestService.
     *
     * @param friendRequestService the friend request service
     */
    @Autowired
    public FriendRequestController(FriendRequestService friendRequestService, FriendRepository friendRepository, FriendService friendService) {
        this.friendRequestService = friendRequestService;
        this.friendRepository = friendRepository;
        this.friendService = friendService;
    }

    @Operation(summary = "Get all friend requests for a user",
            description = "Retrieves all incoming friend requests for the specified username")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved friend requests",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FriendRequest.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(type = "string"))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized access",
                    content = @Content(schema = @Schema(type = "string"))
            )
    })
    @GetMapping("/{username}")
    public List<FriendRequest> getAllFriendRequests(
            @Parameter(description = "Username of the user to get friend requests for",
                    required = true,
                    example = "john_doe")
            @PathVariable String username) {
        return friendRequestService.getAllFriendRequests(username);
    }

    @Operation(summary = "Send a friend request",
            description = "Sends a friend request from one user to another")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friend request sent successfully",
                    content = @Content(schema = @Schema(implementation = FriendRequest.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request (e.g., sending to self, already friends)",
                    content = @Content(schema = @Schema(type = "string"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(type = "string"))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Friend request already exists",
                    content = @Content(schema = @Schema(type = "string"))
            )
    })
    @PostMapping
    public FriendRequest sendFriendRequest(
            @Parameter(description = "Friend request details",
                    required = true)
            @RequestBody FriendRequestPayload payload) {
        return friendRequestService.sendFriendRequest(payload.getSenderUsername(),
                payload.getReceiverUsername());
    }

    @Operation(summary = "Accept a friend request",
            description = "Accepts a pending friend request from the specified user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friend request accepted successfully",
                    content = @Content(schema = @Schema(implementation = FriendRequest.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Friend request not found",
                    content = @Content(schema = @Schema(type = "string"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request state",
                    content = @Content(schema = @Schema(type = "string"))
            )
    })
    @PutMapping("/{sender}/{receiver}/accept")
    public FriendRequest acceptFriendRequest(
            @Parameter(description = "Username of the friend request sender",
                    required = true,
                    example = "jrohde23")
            @PathVariable String sender, @PathVariable String receiver) {
        return friendRequestService.acceptFriendRequestByUsername(sender, receiver);
    }

    @Operation(summary = "Deny a friend request",
            description = "Denies a pending friend request from the specified user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friend request denied successfully",
                    content = @Content(schema = @Schema(implementation = FriendRequest.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Friend request not found",
                    content = @Content(schema = @Schema(type = "string"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request state",
                    content = @Content(schema = @Schema(type = "string"))
            )
    })
    @PutMapping("/{username}/deny")
    public FriendRequest denyFriendRequest(
            @Parameter(description = "Username of the friend request sender",
                    required = true,
                    example = "jane_doe")
            @PathVariable String username) {
        return friendRequestService.denyFriendRequestByUsername(username);
    }

    @GetMapping("/{username}/listFriendsByUsername")
    public List<Friend> listFriendsByUsername(@PathVariable String username) {
        return friendService.listFriendsByUsername(username);
    }

    }
