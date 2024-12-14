package mk12.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for match result.
 * Contains the username, star rating, and matched user profile.
 */
@Data
public class MatchResultDTO {
    private String username;  // Changed from email to username
    private double starRating;
    private QuizSubmissionDTO matchedUserProfile;
}