package mk12.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Entity representing a roommate.
 * Contains the ID, name, age, occupation, lifestyle, budget range, match percentage, username, and various attributes such as morning person, hosting, pet, smoking, organized, people over, noise, and cleanliness.
 * Also contains a list of friends associated with the roommate.
 */
@Entity
@Table(name = "roommates")
public class Roommate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private int age;

    @Getter @Setter
    private String occupation;

    @Getter @Setter
    private String lifestyle;

    @Getter @Setter
    private String budgetRange;

    @Getter @Setter
    private int matchPercentage;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private int morningPerson;

    @Getter @Setter
    private int hosting;

    @Getter @Setter
    private int pet;

    @Getter @Setter
    private int smoking;

    @Getter @Setter
    private int organized;

    @Getter @Setter
    private int peopleOver;

    @Getter @Setter
    private int noise;

    @Getter @Setter
    private int cleanliness;

    @ManyToMany
    @JoinTable(
            name = "roommate_friends",
            joinColumns = @JoinColumn(name = "roommate_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Roommate> friends;

    // Constructor
    public Roommate() {}
}