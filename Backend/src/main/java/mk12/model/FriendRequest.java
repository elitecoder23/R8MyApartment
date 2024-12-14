package mk12.model;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * Entity representing a friend request.
 * Contains the ID, sender, receiver, and status of the friend request.
 */
@Getter
@Entity
@Table(name = "friend_requests")
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    /**
     * Enum representing the status of the friend request.
     */
    public enum FriendRequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    public boolean isPending() {
        return status == FriendRequestStatus.PENDING;
    }

    public boolean isAccepted() {
        return status == FriendRequestStatus.ACCEPTED;
    }

    public boolean isRejected() {
        return status == FriendRequestStatus.REJECTED;
    }
}