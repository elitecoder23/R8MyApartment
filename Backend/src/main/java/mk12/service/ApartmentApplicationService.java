package mk12.service;

import mk12.model.ApartmentApplication;
import mk12.model.Apartment;
import mk12.model.User;
import mk12.repository.IApartmentApplicationRepository;
import mk12.repository.IApartmentRepository;
import mk12.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Service
public class ApartmentApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApartmentApplicationService.class);

    @Autowired
    private IApartmentApplicationRepository applicationRepository;

    @Autowired
    private IApartmentRepository apartmentRepository;

    @Autowired
    private IUserRepository userRepository;

    public ApartmentApplication submitApplication(String apartmentName, String username, ApartmentApplication application) {
        Optional<Apartment> apartment = apartmentRepository.findByName(apartmentName);
        Optional<User> user = userRepository.findByUsername(username);

        if (apartment.isEmpty()) {
            logger.error("Apartment not found: {}", apartmentName);
            throw new RuntimeException("Apartment not found: " + apartmentName);
        }

        if (user.isEmpty()) {
            logger.error("User not found: {}", username);
            throw new RuntimeException("User not found: " + username);
        }

        // Check if user already has a pending application for this apartment
        Optional<ApartmentApplication> existingApplication =
                applicationRepository.findByApartmentNameAndUserUsername(apartmentName, username);

        if (existingApplication.isPresent() &&
                existingApplication.get().getStatus() == ApartmentApplication.ApplicationStatus.PENDING) {
            logger.error("User {} already has a pending application for {}", username, apartmentName);
            throw new RuntimeException("Already have a pending application for this apartment");
        }

        application.setApartment(apartment.get());
        application.setUser(user.get());
        application.setStatus(ApartmentApplication.ApplicationStatus.PENDING);

        logger.info("Submitting application for user {} to apartment {}", username, apartmentName);
        return applicationRepository.save(application);
    }

    public ApartmentApplication updateApplicationStatus(String apartmentName, String username,
                                                        ApartmentApplication.ApplicationStatus status) {
        Optional<ApartmentApplication> application =
                applicationRepository.findByApartmentNameAndUserUsername(apartmentName, username);

        if (application.isEmpty()) {
            logger.error("Application not found for apartment {} and user {}", apartmentName, username);
            throw new RuntimeException("Application not found");
        }

        ApartmentApplication existingApplication = application.get();
        existingApplication.setStatus(status);

        logger.info("Updating application status to {} for user {} apartment {}",
                status, username, apartmentName);
        return applicationRepository.save(existingApplication);
    }

    public List<ApartmentApplication> getUserApplications(String username) {
        logger.info("Retrieving applications for user {}", username);
        return applicationRepository.findByUserUsername(username);
    }

    public List<ApartmentApplication> getOwnerApplications(String ownerEmail) {
        logger.info("Retrieving applications for owner {}", ownerEmail);
        return applicationRepository.findByApartmentOwnerEmail(ownerEmail);
    }

    public List<ApartmentApplication> getApplicationsForApartment(String apartmentName) {
        logger.info("Retrieving applications for apartment {}", apartmentName);
        return applicationRepository.findByApartmentName(apartmentName);
    }
}