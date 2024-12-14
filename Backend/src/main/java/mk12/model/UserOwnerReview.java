// src/main/java/mk12/model/UserOwnerReview.java
package mk12.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_owner_reviews")
@Data
public class UserOwnerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ownerUsername;

    @Column(nullable = false)
    private String userUsername;

    @Column(nullable = false)
    private String reviewText;

    @Column(nullable = false)
    private int rating;
}