package mk12.repository;

import mk12.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    // Additional query methods can be defined here if needed
    public List<Friend> findByUser_Username(String username);
}