package mk12.service;

import mk12.dto.ReviewDTO;
import mk12.model.Review;
import mk12.model.User;
import mk12.repository.IReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private IReviewRepository reviewRepository;

    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public Optional<Review> findReviewById(int reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByApartmentName(String apartmentName) {
        return reviewRepository.findByApartment_Name(apartmentName);
    }

   /* public double getAverageRating(String apartmentName) {
        List<Review> reviews = reviewRepository.findByApartment_Name(apartmentName);
        if (!reviews.isEmpty()) {
            return reviews.stream()
                    .mapToDouble(ReviewDTO::getRating)
                    .average()
                    .orElse(0.0);
        }
        return 0.0;
    }

    */
    public void deleteReview(Review review) {
        reviewRepository.delete(review);
    }
    public List<ReviewDTO> getReviewsByApartmentId(int apartmentId) {
        return reviewRepository.findByApartment_Id(apartmentId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getUser().getId(),
                review.getComment(),
                review.getRating(),
                review.getReviewText(),
                review.getApartment(),
                review.getApartment() != null ? review.getApartment().getName() : null
        );
    }
    public List<Review> getReviewsByUsername(String username) {
        return reviewRepository.findByUserUsername(username);
    }

    public double getAverageRating(String apartmentName) {
        List<Review> reviews = reviewRepository.findByApartment_Name(apartmentName);
        if (!reviews.isEmpty()) {
            return reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
        }
        return 0.0;
    }
}