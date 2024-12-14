package mk12.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a review.
 * Contains the ID, comment, rating, username, review text, associated apartment, and user.
 */
@Entity
@Table(name = "reviews")
public class Review {
    // Getters and setters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter @Setter
    private String comment;

    @Getter @Setter
    private int rating;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String reviewText;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "apartment_name")
    private Apartment apartment;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Review() {}

    public void setId(int id) {
        this.id = id;
    }

    public void setApartmentName(String name) {
        this.apartment.setName(name);
    }

    public void setApartmentId(Long id) {
        this.apartment.setId(id);
    }

    public String getApartmentName() {
        return apartment.getName();
    }

   /* public void setUserId(int userId) {
        this.user.setId(userId);
    }

    */
}