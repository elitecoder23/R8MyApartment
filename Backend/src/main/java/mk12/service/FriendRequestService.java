package mk12.service;

import mk12.model.FriendRequest;
import mk12.model.User;
import mk12.repository.FriendRequestRepository;
import mk12.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing friend requests.
 * Provides methods for sending, retrieving, accepting, and denying friend requests.
 */
@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final IUserRepository iUserRepository;

    @Autowired
    public FriendRequestService(FriendRequestRepository friendRequestRepository, IUserRepository iUserRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.iUserRepository = iUserRepository;
    }

    /**
     * Sends a friend request from one user to another.
     *
     * @param senderUsername the username of the sender
     * @param receiverUsername the username of the receiver
     * @return the sent friend request
     * @throws IllegalArgumentException if the sender or receiver does not exist
     */
    public FriendRequest sendFriendRequest(String senderUsername, String receiverUsername) {
        Optional<User> sender = iUserRepository.findByUsername(senderUsername);
        Optional<User> receiver = iUserRepository.findByUsername(receiverUsername);

        if (sender.isEmpty() || receiver.isEmpty()) {
            throw new IllegalArgumentException("Sender or receiver does not exist.");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender.get());
        friendRequest.setReceiver(receiver.get());
        friendRequest.setStatus(FriendRequest.FriendRequestStatus.PENDING);

        return friendRequestRepository.save(friendRequest);
    }

    /**
     * Retrieves all friend requests for a given user.
     *
     * @param username the username of the receiver
     * @return a list of friend requests
     */
    public List<FriendRequest> getAllFriendRequests(String username) {
        return friendRequestRepository.findByReceiver_Username(username);
    }

    /**
     * Accepts a friend request for a given user.
     *
     * @param username the username of the receiver
     * @return the accepted friend request
     * @throws IllegalArgumentException if the friend request is not found
     */
    public FriendRequest acceptFriendRequestByUsername(String senderUsername, String receiverUsername) {
        List<FriendRequest> friendRequests = friendRequestRepository.findBySender_UsernameAndReceiver_Username(senderUsername, receiverUsername);
        if (friendRequests.isEmpty()) {
            throw new IllegalArgumentException("Friend request not found.");
        }

        FriendRequest friendRequest = friendRequests.get(0); // Assuming only one pending request per user
        friendRequest.setStatus(FriendRequest.FriendRequestStatus.ACCEPTED);
        return friendRequestRepository.save(friendRequest);
    }

    /**
     * Denies a friend request for a given user.
     *
     * @param username the username of the receiver
     * @return the denied friend request
     * @throws IllegalArgumentException if the friend request is not found
     */
    public FriendRequest denyFriendRequestByUsername(String username) {
        List<FriendRequest> friendRequests = friendRequestRepository.findByReceiver_Username(username);
        if (friendRequests.isEmpty()) {
            throw new IllegalArgumentException("Friend request not found.");
        }

        FriendRequest friendRequest = friendRequests.get(0); // Assuming only one pending request per user
        friendRequest.setStatus(FriendRequest.FriendRequestStatus.REJECTED);
        return friendRequestRepository.save(friendRequest);
    }

    public void markFriendRequestPending(String senderUsername, String receiverUsername) {
        Optional<User> sender = iUserRepository.findByUsername(senderUsername);
        Optional<User> receiver = iUserRepository.findByUsername(receiverUsername);

        if (sender.isEmpty() || receiver.isEmpty()) {
            throw new IllegalArgumentException("Sender or receiver does not exist.");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender.get());
        friendRequest.setReceiver(receiver.get());
        friendRequest.setStatus(FriendRequest.FriendRequestStatus.PENDING);

        friendRequestRepository.save(friendRequest);
    }

    public void denyFriendRequest(String senderUsername, String receiverUsername) {
        Optional<User> sender = iUserRepository.findByUsername(senderUsername);
        Optional<User> receiver = iUserRepository.findByUsername(receiverUsername);

        if (sender.isEmpty() || receiver.isEmpty()) {
            throw new IllegalArgumentException("Sender or receiver does not exist.");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender.get());
        friendRequest.setReceiver(receiver.get());
        friendRequest.setStatus(FriendRequest.FriendRequestStatus.REJECTED);

        friendRequestRepository.save(friendRequest);
    }
}