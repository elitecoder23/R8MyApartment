package mk12.model;

import jakarta.persistence.*;

/**
 * Entity representing an amenity.
 * Contains the ID and name of the amenity.
 */
@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Constructors

    /**
     * Default constructor.
     */
    public Amenity() {}

    /**
     * Constructs an Amenity with the specified name.
     *
     * @param name the name of the amenity
     */
    public Amenity(String name) {
        this.name = name;
    }

    // Getters and setters

    /**
     * Gets the ID of the amenity.
     *
     * @return the ID of the amenity
     */
    public Long getId() { return id; }

    /**
     * Sets the ID of the amenity.
     *
     * @param id the ID to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the name of the amenity.
     *
     * @return the name of the amenity
     */
    public String getName() { return name; }

    /**
     * Sets the name of the amenity.
     *
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; }
}