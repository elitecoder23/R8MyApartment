package mk12.repository;

import mk12.model.QuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

/**
 * Repository interface for QuizResponse entities.
 * Provides CRUD operations and methods to find quiz responses by username and to find quiz responses not associated with a specific username.
 */
public interface IQuizRepository extends JpaRepository<QuizResponse, Long> {
    /**
     * Finds a quiz response by the username.
     *
     * @param username the username associated with the quiz response
     * @return an Optional containing the quiz response if found, or empty if not found
     */
    Optional<QuizResponse> findByUsername(String username);

    /**
     * Finds a list of quiz responses not associated with the specified username.
     *
     * @param username the username to exclude from the search
     * @return a list of quiz responses not associated with the specified username
     */
    List<QuizResponse> findByUsernameNot(String username);
}