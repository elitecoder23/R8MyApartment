package mk12.service;

import mk12.model.User;
import mk12.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private IUserRepository userRepository;

    public User createUser(User user) {
        logger.info("Attempting to create user: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        logger.info("User saved with username: {}", savedUser.getUsername());
        return savedUser;
    }

    public List<User> getAllUsers() {
        logger.info("Retrieving all users");
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        logger.info("Retrieving user with username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    public User updateUser(String username, User userDetails) {
        logger.info("Updating user with username: {}", username);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setFirstName(userDetails.getFirstName());
            existingUser.setLastName(userDetails.getLastName());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setBirthDate(userDetails.getBirthDate());
            existingUser.setPhoneNumber(userDetails.getPhoneNumber());
            existingUser.setPassword(userDetails.getPassword());
            return userRepository.save(existingUser);
        }
        return null;
    }

    public boolean deleteUser(String username) {
        logger.info("Deleting user with username: {}", username);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            userRepository.deleteByUsername(username);
            return true;
        }
        return false;
    }

    public User addFriend(String username, String friendUsername) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<User> friendOptional = userRepository.findByUsername(friendUsername);
        if (userOptional.isPresent() && friendOptional.isPresent()) {
            User user = userOptional.get();
            User friend = friendOptional.get();
            user.getFriends().add(friend);
            userRepository.save(user);
            return user;
        }
        return null;
    }


    public User getUserById(int l) {
        return userRepository.findById(l).orElse(null);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}