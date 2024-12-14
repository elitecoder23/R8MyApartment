package mk12.controllers;

import mk12.dto.ReviewDTO;
import mk12.model.Apartment;
import mk12.model.Review;
import mk12.model.User;
import mk12.repository.IApartmentRepository;
import mk12.repository.IUserRepository;
import mk12.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private IApartmentRepository apartmentRepository;

    @Autowired
    private IUserRepository userRepository;



    @PostMapping("/addReviewForUser/{username}")
    public ResponseEntity<?> addReview(@RequestBody Review review, @PathVariable String username) {
        try {
            Apartment apartment = apartmentRepository.findByName(review.getApartment().getName())
                    .orElse(null);
            User user = userRepository.findByUsername(username)
                    .orElse(null);

            if (apartment == null) {
                return new ResponseEntity<>("Apartment not found", HttpStatus.NOT_FOUND);
            }
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            review.setApartment(apartment);
            review.setUser(user);  // Make sure to set the user
            review.setUsername(username);  // Set username as well
            review.setApartmentName(apartment.getName());
            review.setApartmentId(apartment.getId());

            Review createdReview = reviewService.addReview(review);
            return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllReviews")
    public ResponseEntity<?> getAllReviews() {
        try {
            List<ReviewDTO> reviews = reviewService.getAllReviews();
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   @GetMapping("/getAverageRating")
    public ResponseEntity<?> getAverageRating(@RequestParam String apartmentName) {
        try {
            double averageRating = reviewService.getAverageRating(apartmentName);
            return new ResponseEntity<>(averageRating, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/getReviewsByApartmentName")
    public ResponseEntity<?> getReviewsByApartmentName(@RequestParam String apartmentName) {
        try {
            List<Review> reviews = reviewService.getReviewsByApartmentName(apartmentName);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getReviewsByUser/{username}")
    public ResponseEntity<?> getReviewsByUser(@PathVariable String username) {
        try {
            System.out.println("Fetching reviews for username: " + username);
            List<Review> reviews = reviewService.getReviewsByUsername(username);
            System.out.println("Found " + reviews.size() + " reviews");
            reviews.forEach(review -> {
                System.out.println("Review ID: " + review.getId());
                System.out.println("Apartment: " + review.getApartmentName());  // Changed this line
            });
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/editReview")
    public ResponseEntity<?> editReview(@RequestParam int reviewId, @RequestBody Review updatedReview) {
        try {
            Optional<Review> optionalReview = reviewService.findReviewById(reviewId);
            if (optionalReview.isEmpty()) {
                return new ResponseEntity<>("Review not found", HttpStatus.NOT_FOUND);
            }

            Review review = optionalReview.get();
            review.setComment(updatedReview.getComment());
            review.setRating(updatedReview.getRating());
            review.setReviewText(updatedReview.getReviewText());

            Review savedReview = reviewService.addReview(review);
            return new ResponseEntity<>(savedReview, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteReview")
    public ResponseEntity<?> deleteReview(@RequestParam int reviewId) {
        try {
            Optional<Review> optionalReview = reviewService.findReviewById(reviewId);
            if (optionalReview.isEmpty()) {
                return new ResponseEntity<>("Review not found", HttpStatus.NOT_FOUND);
            }

            reviewService.deleteReview(optionalReview.get());
            return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
