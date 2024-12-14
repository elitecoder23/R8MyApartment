package mk12.service;

import mk12.model.Roommate;
import mk12.repository.IRoommateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoommateService {

    @Autowired
    private IRoommateRepository roommateRepository;

    // Create
    public Roommate createRoommate(Roommate roommate) {
        return roommateRepository.save(roommate);
    }

    // Read (all)
    public List<Roommate> getAllRoommates() {
        return roommateRepository.findAll();
    }


    // Read (single)
    public Roommate getRoommateByName(String username) {
        return roommateRepository.findByUsername(username);
    }

    // Update
    public Roommate updateRoommate(String username, Roommate roommateDetails) {
        Roommate roommate = roommateRepository.findByUsername(username);
        if (roommate != null) {
            roommate.setMorningPerson(roommateDetails.getMorningPerson());
            roommate.setHosting(roommateDetails.getHosting());
            roommate.setPet(roommateDetails.getPet());
            roommate.setSmoking(roommateDetails.getSmoking());
            roommate.setOrganized(roommateDetails.getOrganized());
            roommate.setPeopleOver(roommateDetails.getPeopleOver());
            roommate.setNoise(roommateDetails.getNoise());
            roommate.setCleanliness(roommateDetails.getCleanliness());
            return roommateRepository.save(roommate);
        }
        return null;
    }

    // Delete
    public boolean deleteRoommate(String username) {
        Roommate roommate = roommateRepository.findByUsername(username);
        if (roommate != null) {
            roommateRepository.delete(roommate);
            return true;
        }
        return false;
    }

    // Search (exact match)
    public List<Roommate> getRoommatesByExactName(String name) {
        Optional<Roommate> roommate = roommateRepository.findByName(name);
        return roommate.map(List::of).orElse(List.of());
    }

    public List<Roommate> searchRoommates(String name) {
        return roommateRepository.findByNameContainingIgnoreCase(name);
    }

}
