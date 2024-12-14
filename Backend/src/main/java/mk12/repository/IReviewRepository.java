package mk12.repository;

import mk12.dto.ReviewDTO;
import mk12.model.Apartment;
import mk12.model.Review;
import mk12.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entities.
 * Provides CRUD operations and a method to find reviews by apartment name.
 */
@Repository
public interface IReviewRepository extends JpaRepository<Review, Integer> {
    /**
     * Finds a review by the apartment name.
     *
     * @param apartmentName the name of the apartment
     * @return an Optional containing the review if found, or empty if not found
     */

    List<Review> findByApartment_Name(String apartmentName);

    List<Review> findByApartment_Id(int apartmentId);
    @Query("SELECT r FROM Review r WHERE r.user.username = :username")
    List<Review> findByUserUsername(String username);
}