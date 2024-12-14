package mk12.service;

import mk12.model.Apartment;
import mk12.model.ApartmentOwner;
import mk12.repository.IApartmentRepository;
import mk12.repository.IApartmentOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing apartments.
 * Provides methods for creating, retrieving, updating, and deleting apartments.
 */
@Service
public class ApartmentService {

    private final IApartmentRepository apartmentRepository;
    private final IApartmentOwnerRepository ownerRepository;

    @Autowired
    public ApartmentService(IApartmentRepository apartmentRepository, IApartmentOwnerRepository ownerRepository) {
        this.apartmentRepository = apartmentRepository;
        this.ownerRepository = ownerRepository;
    }

    /**
     * Creates a new apartment.
     *
     * @param apartment the apartment to be created
     * @param ownerName the name of the owner of the apartment
     * @return the created apartment
     * @throws IllegalArgumentException if any required field is missing or invalid
     */
    @Transactional
    public Apartment createApartment(Apartment apartment, String ownerName) {
        // Validate required fields
        if (apartment.getName() == null || apartment.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Apartment name is required");
        }
        if (apartment.getAddress() == null || apartment.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (apartment.getContactPhone() == null || apartment.getContactPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact phone is required");
        }
        if (apartment.getContactEmail() == null || apartment.getContactEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact email is required");
        }
        if (apartment.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        // Find owner
        ApartmentOwner owner = ownerRepository.findByName(ownerName)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        apartment.setOwner(owner);
        return apartmentRepository.save(apartment);
    }

    /**
     * Retrieves all apartments.
     *
     * @return a list of all apartments
     */
    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }

    /**
     * Retrieves an apartment by its name.
     *
     * @param apartmentName the name of the apartment
     * @return the apartment with the specified name
     * @throws RuntimeException if the apartment is not found
     */
    public Apartment getApartmentByName(String apartmentName) {
        return apartmentRepository.findByName(apartmentName)
                .orElseThrow(() -> new RuntimeException("Apartment not found with name: " + apartmentName));
    }

    /**
     * Retrieves apartments by the owner's name.
     *
     * @param ownerName the name of the owner
     * @return a list of apartments owned by the specified owner
     */
    public List<Apartment> getApartmentsByOwnerName(String ownerName) {
        return apartmentRepository.findByOwnerName(ownerName);
    }

    /**
     * Deletes an apartment by its name.
     *
     * @param name the name of the apartment to be deleted
     */
    @Transactional
    public void deleteApartment(String name) {
        apartmentRepository.deleteByName(name);
    }

    /**
     * Updates an existing apartment.
     *
     * @param apartmentName the name of the apartment to be updated
     * @param apartmentDetails the new details of the apartment
     * @return the updated apartment
     * @throws RuntimeException if the apartment is not found
     */
    public Apartment updateApartment(String apartmentName, Apartment apartmentDetails) {
        Apartment apartment = apartmentRepository.findByName(apartmentName)
                .orElseThrow(() -> new RuntimeException("Apartment not found with name: " + apartmentName));

        apartment.setName(apartmentDetails.getName());
        apartment.setPrice(apartmentDetails.getPrice());
        apartment.setAmenities(apartmentDetails.getAmenities());
        apartment.setAddress(apartmentDetails.getAddress());
        apartment.setContactPhone(apartmentDetails.getContactPhone());
        apartment.setContactEmail(apartmentDetails.getContactEmail());

        return apartmentRepository.save(apartment);
    }
}