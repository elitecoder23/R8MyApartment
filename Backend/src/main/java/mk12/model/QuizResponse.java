package mk12.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a quiz response.
 * Contains the ID, username, and various quiz response attributes such as morning person, hosting, liking pets, smoking, organization skills, people over, noise level, and cleanliness.
 */
@Data
@Entity
@Table(name = "quiz_responses")
public class QuizResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;  // Changed from email to username
    private int morningPerson;
    private int hosting;
    private int likingPets;
    private int smoking;
    private int organizationSkills;
    private int peopleOver;
    private int noiseLevel;
    private int cleanliness;
}