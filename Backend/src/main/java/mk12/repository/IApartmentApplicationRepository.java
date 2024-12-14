package mk12.repository;

import mk12.model.ApartmentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IApartmentApplicationRepository extends JpaRepository<ApartmentApplication, Long> {
    List<ApartmentApplication> findByUserUsername(String username);
    List<ApartmentApplication> findByApartmentName(String apartmentName);
    List<ApartmentApplication> findByApartmentOwnerEmail(String ownerEmail);
    Optional<ApartmentApplication> findByApartmentNameAndUserUsername(String apartmentName, String username);
}