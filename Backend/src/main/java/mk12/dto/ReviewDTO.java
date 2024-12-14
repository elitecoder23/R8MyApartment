package mk12.dto;

import mk12.model.Apartment;

public class ReviewDTO {
    private int id;
    private int userId;
    private String comment;
    private int rating;
    private String reviewText;
    private Apartment apartment;
    private String apartmentName;
    public ReviewDTO(int id, int userId, String comment, int rating, String reviewText, Apartment apartment, String apartmentName) {
        this.id = id;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
        this.reviewText = reviewText;
        this.apartment = apartment;
        this.apartmentName = apartmentName;
    }
    // Getters and setters

    public String getApartmentName() {
        return apartmentName;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public void setApartment(Apartment apartment) {
        this.apartment=apartment;
    }

    public void setApartmentName(String name) {
        this.apartment.setName(name);
    }
    public void setApartmentId(Long id) {
        this.apartment.setId(id);
    }
}