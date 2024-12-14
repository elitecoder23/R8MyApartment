package mk12.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing an apartment.
 * Contains the ID, name, price, address, amenities, contact phone, contact email, and owner of the apartment.
 */
@Entity
@Table(name = "apartment")
@Data
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private double price;

    @Column(nullable = false)
    private String address;

    private String amenities;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private String contactEmail;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private ApartmentOwner owner;
}