package mk12.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for quiz submission.
 * Contains the username and various attributes related to the user's preferences and habits.
 */
@Data
public class QuizSubmissionDTO {
    private String username;  // Changed from email to username
    private int morningPerson;
    private int hosting;
    private int likingPets;
    private int smoking;
    private int organizationSkills;
    private int peopleOver;
    private int noiseLevel;
    private int cleanliness;

    public void setAllOtherScores(int i) {
    }

    public void setAllScores(int i) {
    }
}