// src/main/java/mk12/repository/IUserOwnerReviewRepository.java
package mk12.repository;

import mk12.model.UserOwnerReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserOwnerReviewRepository extends JpaRepository<UserOwnerReview, Long> {
    List<UserOwnerReview> findByUserUsername(String userUsername);
}