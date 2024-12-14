// src/main/java/mk12/service/UserOwnerReviewService.java
package mk12.service;

import mk12.model.UserOwnerReview;
import mk12.repository.IUserOwnerReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserOwnerReviewService {
    private final IUserOwnerReviewRepository reviewRepository;

    public UserOwnerReviewService(IUserOwnerReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public UserOwnerReview createReview(UserOwnerReview review) {
        return reviewRepository.save(review);
    }

    public List<UserOwnerReview> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<UserOwnerReview> getReviewsByUserUsername(String userUsername) {
        return reviewRepository.findByUserUsername(userUsername);
    }
}