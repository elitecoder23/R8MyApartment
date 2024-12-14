package mk12.repository;

import mk12.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for User entities.
 * Provides CRUD operations and methods to find and delete a user by their username.
 */
public interface IUserRepository extends JpaRepository<User, Integer> {
    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Deletes a user by their username.
     *
     * @param username the username of the user to be deleted
     */
    void deleteByUsername(String username);
}