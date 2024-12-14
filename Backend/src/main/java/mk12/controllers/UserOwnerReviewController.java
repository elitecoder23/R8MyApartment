// src/main/java/mk12/controllers/UserOwnerReviewController.java
package mk12.controllers;

import mk12.dto.UserReviewDTO;
import mk12.model.UserOwnerReview;
import mk12.service.UserOwnerReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userReview")
@CrossOrigin(origins = "*")
public class UserOwnerReviewController {
    private final UserOwnerReviewService reviewService;

    public UserOwnerReviewController(UserOwnerReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<UserOwnerReview> createReview(@RequestBody UserReviewDTO reviewDTO) {
        UserOwnerReview review = new UserOwnerReview();
        review.setUserUsername(reviewDTO.getUserUsername());
        review.setReviewText(reviewDTO.getReviewText());
        // Set the ownerUsername and rating as needed
        review.setOwnerUsername("ownerUsername"); // Replace with actual owner username
        review.setRating(5); // Replace with actual rating
        return ResponseEntity.ok(reviewService.createReview(review));
    }

    @GetMapping
    public ResponseEntity<List<UserOwnerReview>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/user/{userUsername}")
    public ResponseEntity<List<UserOwnerReview>> getReviewsByUserUsername(@PathVariable String userUsername) {
        return ResponseEntity.ok(reviewService.getReviewsByUserUsername(userUsername));
    }
}