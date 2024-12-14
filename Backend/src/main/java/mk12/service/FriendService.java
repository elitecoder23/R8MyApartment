package mk12.service;

import mk12.model.Friend;
import mk12.model.User;
import mk12.repository.FriendRepository;
import mk12.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final IUserRepository userRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository, IUserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void acceptFriendRequest(String senderUsername, String receiverUsername) {
        Optional<User> senderOptional = userRepository.findByUsername(senderUsername);
        Optional<User> receiverOptional = userRepository.findByUsername(receiverUsername);

        if (senderOptional.isEmpty() || receiverOptional.isEmpty()) {
            throw new IllegalArgumentException("Sender or receiver does not exist.");
        }

        User sender = senderOptional.get();
        User receiver = receiverOptional.get();

        Friend friend = new Friend();
        friend.setUser(receiver);
        friend.setFriend(sender);

        friendRepository.save(friend);
    }
    public List<Friend> listFriendsByUsername(String username) {
        return friendRepository.findByUser_Username(username);
    }
}