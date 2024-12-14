package mk12.repository;

import mk12.model.ApartmentOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for ApartmentOwner entities.
 * Provides CRUD operations and methods to find an apartment owner by email or name.
 */
public interface IApartmentOwnerRepository extends JpaRepository<ApartmentOwner, Long> {
    /**
     * Finds an apartment owner by their email.
     *
     * @param email the email of the apartment owner
     * @return an Optional containing the apartment owner if found, or empty if not found
     */
    Optional<ApartmentOwner> findByEmail(String email);

    /**
     * Finds an apartment owner by their name.
     *
     * @param name the name of the apartment owner
     * @return an Optional containing the apartment owner if found, or empty if not found
     */
    Optional<ApartmentOwner> findByName(String name);
}