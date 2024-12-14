package mk12.repository;

import mk12.model.Roommate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Roommate entities.
 * Provides CRUD operations and methods to find roommates by various attributes such as username, age range, occupation, lifestyle, budget range, and name.
 * Also provides a method to delete a roommate by their name.
 */
@Repository
public interface IRoommateRepository extends JpaRepository<Roommate, Long> {
    /**
     * Finds a roommate by their username.
     *
     * @param username the username of the roommate
     * @return the roommate with the specified username
     */
    Roommate findByUsername(String username);

    /**
     * Finds a list of roommates within a specified age range.
     *
     * @param minAge the minimum age
     * @param maxAge the maximum age
     * @return a list of roommates within the specified age range
     */
    List<Roommate> findByAgeBetween(int minAge, int maxAge);

    /**
     * Finds a list of roommates by their occupation.
     *
     * @param occupation the occupation of the roommates
     * @return a list of roommates with the specified occupation
     */
    List<Roommate> findByOccupation(String occupation);

    /**
     * Finds a list of roommates by their lifestyle.
     *
     * @param lifestyle the lifestyle of the roommates
     * @return a list of roommates with the specified lifestyle
     */
    List<Roommate> findByLifestyle(String lifestyle);

    /**
     * Finds a list of roommates by their budget range.
     *
     * @param budgetRange the budget range of the roommates
     * @return a list of roommates with the specified budget range
     */
    List<Roommate> findByBudgetRange(String budgetRange);

    /**
     * Finds a roommate by their name.
     *
     * @param name the name of the roommate
     * @return an Optional containing the roommate if found, or empty if not found
     */
    Optional<Roommate> findByName(String name);

    /**
     * Finds a list of roommates whose names contain the specified string, ignoring case.
     *
     * @param name the string to search for in the names
     * @return a list of roommates whose names contain the specified string, ignoring case
     */
    List<Roommate> findByNameContainingIgnoreCase(String name);

    /**
     * Deletes a roommate by their name.
     *
     * @param name the name of the roommate to be deleted
     */
    void deleteByName(String name);
}