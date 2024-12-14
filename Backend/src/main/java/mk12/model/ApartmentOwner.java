package mk12.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing an apartment owner.
 * Contains the ID, email, password, and name of the apartment owner.
 */
@Entity
@Table(name = "apartment_owners")
@Data
public class ApartmentOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
}