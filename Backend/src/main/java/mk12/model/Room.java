package mk12.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity representing a room.
 * Contains the ID, owner, and a list of roommates associated with the room.
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Roommate owner;

    @ManyToMany
    @JoinTable(
            name = "room_roommates",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "roommate_id")
    )
    private List<Roommate> roommates;

    // Constructors, getters, and setters
    public Room() {}

    public Room(Roommate owner) {
        this.owner = owner;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Roommate getOwner() { return owner; }
    public void setOwner(Roommate owner) { this.owner = owner; }

    public List<Roommate> getRoommates() { return roommates; }
    public void setRoommates(List<Roommate> roommates) { this.roommates = roommates; }
}