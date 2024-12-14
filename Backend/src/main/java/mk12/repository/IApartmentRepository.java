package mk12.repository;

import mk12.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Apartment entities.
 * Provides CRUD operations and methods to find apartments by owner ID, name, and owner name.
 * Also provides a method to delete an apartment by its name.
 */
@Repository
public interface IApartmentRepository extends JpaRepository<Apartment, Long> {
    /**
     * Finds a list of apartments by the owner's ID.
     *
     * @param ownerId the ID of the owner
     * @return a list of apartments owned by the specified owner ID
     */
    List<Apartment> findByOwnerId(Long ownerId);

    /**
     * Finds an apartment by its name.
     *
     * @param name the name of the apartment
     * @return an Optional containing the apartment if found, or empty if not found
     */
    Optional<Apartment> findByName(String name);

    /**
     * Finds a list of apartments by the owner's name.
     *
     * @param ownerName the name of the owner
     * @return a list of apartments owned by the specified owner name
     */
    List<Apartment> findByOwnerName(String ownerName);

    /**
     * Deletes an apartment by its name.
     *
     * @param name the name of the apartment to be deleted
     */
    void deleteByName(String name);
}