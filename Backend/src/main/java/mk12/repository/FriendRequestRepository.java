package mk12.repository;

import mk12.model.FriendRequest;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

/**
 * Repository interface for FriendRequest entities.
 * Provides CRUD operations and a method to find friend requests by the receiver's username.
 */
public interface FriendRequestRepository extends CrudRepository<FriendRequest, Long> {
    /**
     * Finds a list of friend requests by the receiver's username.
     *
     * @param username the username of the receiver
     * @return a list of friend requests received by the specified username
     */
    List<FriendRequest> findBySender_UsernameAndReceiver_Username(String senderUsername, String receiverUsername);

    List<FriendRequest> findByReceiver_Username(String username);
}