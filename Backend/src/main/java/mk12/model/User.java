package mk12.model;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entity representing a user.
 * Contains the ID, username, first name, last name, email, birth date, phone number, password, sent and received friend requests, and a list of friends.
 */
@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String phoneNumber;
    private String password;

    @OneToMany(mappedBy = "sender")
    @JsonIgnore
    private List<FriendRequest> sentFriendRequests;

    @OneToMany(mappedBy = "receiver")
    @JsonIgnore
    private List<FriendRequest> receivedFriendRequests;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;

    // Default constructor
    public User() {}

    // Constructor with fields
    public User(String username, String firstName, String lastName, String email, String birthDate, String phoneNumber, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<FriendRequest> getSentFriendRequests() { return sentFriendRequests; }
    public void setSentFriendRequests(List<FriendRequest> sentFriendRequests) { this.sentFriendRequests = sentFriendRequests; }

    public List<FriendRequest> getReceivedFriendRequests() { return receivedFriendRequests; }
    public void setReceivedFriendRequests(List<FriendRequest> receivedFriendRequests) { this.receivedFriendRequests = receivedFriendRequests; }
    public List<User> getFriends() { return friends; }

    public boolean isPresent() {
        return true;
    }
}