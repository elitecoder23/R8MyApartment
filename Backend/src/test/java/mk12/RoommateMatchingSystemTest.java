package mk12;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mk12.dto.QuizSubmissionDTO;
import mk12.dto.MatchResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoommateMatchingSystemTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testQuizSubmissionAndMatching() {
        // Generate unique usernames for test
        String username1 = "user1_" + UUID.randomUUID().toString().substring(0, 8);
        String username2 = "user2_" + UUID.randomUUID().toString().substring(0, 8);
        String username3 = "user3_" + UUID.randomUUID().toString().substring(0, 8);

        // Create quiz submissions with different compatibility levels
        QuizSubmissionDTO highCompatSubmission = new QuizSubmissionDTO();
        highCompatSubmission.setUsername(username1);
        highCompatSubmission.setMorningPerson(8);
        highCompatSubmission.setHosting(7);
        highCompatSubmission.setLikingPets(9);
        highCompatSubmission.setSmoking(1);
        highCompatSubmission.setOrganizationSkills(8);
        highCompatSubmission.setPeopleOver(7);
        highCompatSubmission.setNoiseLevel(6);
        highCompatSubmission.setCleanliness(9);

        QuizSubmissionDTO similarSubmission = new QuizSubmissionDTO();
        similarSubmission.setUsername(username2);
        similarSubmission.setMorningPerson(7);
        similarSubmission.setHosting(8);
        similarSubmission.setLikingPets(8);
        similarSubmission.setSmoking(1);
        similarSubmission.setOrganizationSkills(9);
        similarSubmission.setPeopleOver(6);
        similarSubmission.setNoiseLevel(7);
        similarSubmission.setCleanliness(8);

        QuizSubmissionDTO lowCompatSubmission = new QuizSubmissionDTO();
        lowCompatSubmission.setUsername(username3);
        lowCompatSubmission.setMorningPerson(2);
        lowCompatSubmission.setHosting(2);
        lowCompatSubmission.setLikingPets(1);
        lowCompatSubmission.setSmoking(9);
        lowCompatSubmission.setOrganizationSkills(3);
        lowCompatSubmission.setPeopleOver(9);
        lowCompatSubmission.setNoiseLevel(9);
        lowCompatSubmission.setCleanliness(2);

        // Submit quizzes
        submitQuiz(highCompatSubmission);
        submitQuiz(similarSubmission);
        submitQuiz(lowCompatSubmission);

        // Test getting quiz submission
        Response getResponse = given()
                .when()
                .get("/api/roommate-matching/quiz-submission/" + username1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        verifyQuizSubmission(getResponse, highCompatSubmission);

        // Test finding matches - Updated to use query parameter
        Response matchesResponse = given()
                .queryParam("username", username1)
                .when()
                .get("/api/roommate-matching/find-matches")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        List<MatchResultDTO> matches = matchesResponse.jsonPath().getList("$", MatchResultDTO.class);
        assertFalse(matches.isEmpty(), "Should find at least one match");

        // Verify match ordering (similar submission should be better match than low compat)
        int similarUserMatchIndex = findMatchIndex(matches, username2);
        int lowCompatUserMatchIndex = findMatchIndex(matches, username3);
        assertTrue(similarUserMatchIndex < lowCompatUserMatchIndex,
                "Similar user should be ranked higher than low compatibility user");

        // Test specific match calculation
        Response specificMatchResponse = given()
                .queryParam("username1", username1)
                .queryParam("username2", username2)
                .when()
                .get("/api/roommate-matching/calculate-match")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        double matchScore = specificMatchResponse.jsonPath().getDouble("starRating");
        assertTrue(matchScore >= 4.0, "Similar profiles should have high match score");

        // Test updating quiz submission
        highCompatSubmission.setCleanliness(5); // Change cleanliness preference
        given()
                .contentType(ContentType.JSON)
                .body(highCompatSubmission)
                .when()
                .put("/api/roommate-matching/quiz-submission/" + username1)
                .then()
                .statusCode(HttpStatus.OK.value());

        // Verify update
        Response updatedResponse = given()
                .when()
                .get("/api/roommate-matching/quiz-submission/" + username1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        assertEquals(5, updatedResponse.jsonPath().getInt("cleanliness"));

        // Test deleting quiz submission
        given()
                .when()
                .delete("/api/roommate-matching/quiz-submission/" + username1)
                .then()
                .statusCode(HttpStatus.OK.value());

        // Verify deletion
        given()
                .when()
                .get("/api/roommate-matching/quiz-submission/" + username1)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private void submitQuiz(QuizSubmissionDTO submission) {
        given()
                .contentType(ContentType.JSON)
                .body(submission)
                .when()
                .post("/api/roommate-matching/submit-quiz")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    private void verifyQuizSubmission(Response response, QuizSubmissionDTO expected) {
        assertEquals(expected.getUsername(), response.jsonPath().getString("username"));
        assertEquals(expected.getMorningPerson(), response.jsonPath().getInt("morningPerson"));
        assertEquals(expected.getHosting(), response.jsonPath().getInt("hosting"));
        assertEquals(expected.getLikingPets(), response.jsonPath().getInt("likingPets"));
        assertEquals(expected.getSmoking(), response.jsonPath().getInt("smoking"));
        assertEquals(expected.getOrganizationSkills(), response.jsonPath().getInt("organizationSkills"));
        assertEquals(expected.getPeopleOver(), response.jsonPath().getInt("peopleOver"));
        assertEquals(expected.getNoiseLevel(), response.jsonPath().getInt("noiseLevel"));
        assertEquals(expected.getCleanliness(), response.jsonPath().getInt("cleanliness"));
    }

    private int findMatchIndex(List<MatchResultDTO> matches, String username) {
        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i).getUsername().equals(username)) {
                return i;
            }
        }
        return -1;
    }

    @Test
    public void testInvalidQuizSubmission() {
        QuizSubmissionDTO invalidSubmission = new QuizSubmissionDTO();
        invalidSubmission.setUsername("invalid_user");
        invalidSubmission.setMorningPerson(11); // Invalid score > 10

        given()
                .contentType(ContentType.JSON)
                .body(invalidSubmission)
                .when()
                .post("/api/roommate-matching/submit-quiz")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testDuplicateQuizSubmission() {
        String username = "duplicate_user_" + UUID.randomUUID().toString().substring(0, 8);

        QuizSubmissionDTO submission = new QuizSubmissionDTO();
        submission.setUsername(username);
        submission.setMorningPerson(5);
        submission.setHosting(5);
        submission.setLikingPets(5);
        submission.setSmoking(5);
        submission.setOrganizationSkills(5);
        submission.setPeopleOver(5);
        submission.setNoiseLevel(5);
        submission.setCleanliness(5);

        // First submission should succeed
        submitQuiz(submission);

        // Second submission should fail
        given()
                .contentType(ContentType.JSON)
                .body(submission)
                .when()
                .post("/api/roommate-matching/submit-quiz")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testQuizValidationRules() {
        // Test negative values
        QuizSubmissionDTO invalidSubmission = new QuizSubmissionDTO();
        invalidSubmission.setUsername("test_user");
        invalidSubmission.setMorningPerson(-1);
        invalidSubmission.setHosting(5);
        invalidSubmission.setLikingPets(5);
        invalidSubmission.setSmoking(5);
        invalidSubmission.setOrganizationSkills(5);
        invalidSubmission.setPeopleOver(5);
        invalidSubmission.setNoiseLevel(5);
        invalidSubmission.setCleanliness(5);

        given()
                .contentType(ContentType.JSON)
                .body(invalidSubmission)
                .when()
                .post("/api/roommate-matching/submit-quiz")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        // Test values above 10
        QuizSubmissionDTO aboveMaxSubmission = new QuizSubmissionDTO();
        aboveMaxSubmission.setUsername("test_user");
        aboveMaxSubmission.setMorningPerson(11);
        aboveMaxSubmission.setAllOtherScores(5);

        given()
                .contentType(ContentType.JSON)
                .body(aboveMaxSubmission)
                .when()
                .post("/api/roommate-matching/submit-quiz")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        // Test empty username
        QuizSubmissionDTO emptyUsername = new QuizSubmissionDTO();
        emptyUsername.setMorningPerson(5);
        emptyUsername.setAllOtherScores(5);

        given()
                .contentType(ContentType.JSON)
                .body(emptyUsername)
                .when()
                .post("/api/roommate-matching/submit-quiz")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }




    @Test
    public void testQuizUpdateScenarios() {
        String username = "update_user_" + UUID.randomUUID().toString().substring(0, 8);

        // Create initial submission
        QuizSubmissionDTO initial = new QuizSubmissionDTO();
        initial.setUsername(username);
        initial.setAllScores(5);
        submitQuiz(initial);

        // Test partial update
        QuizSubmissionDTO partialUpdate = new QuizSubmissionDTO();
        partialUpdate.setUsername(username);
        partialUpdate.setMorningPerson(7);
        partialUpdate.setAllOtherScores(5);

        given()
                .contentType(ContentType.JSON)
                .body(partialUpdate)
                .when()
                .put("/api/roommate-matching/quiz-submission/" + username)
                .then()
                .statusCode(HttpStatus.OK.value());

        // Verify partial update
        Response response = given()
                .when()
                .get("/api/roommate-matching/quiz-submission/" + username)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        assertEquals(7, response.jsonPath().getInt("morningPerson"));

        // Update non-existent quiz
        given()
                .contentType(ContentType.JSON)
                .body(initial)
                .when()
                .put("/api/roommate-matching/quiz-submission/nonexistent_user")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testMatchingEdgeCases() {
        String user1 = "edge_user1_" + UUID.randomUUID().toString().substring(0, 8);
        String user2 = "edge_user2_" + UUID.randomUUID().toString().substring(0, 8);

        // Submit quizzes for both users
        QuizSubmissionDTO submission = new QuizSubmissionDTO();
        submission.setUsername(user1);
        submission.setAllScores(5);
        submitQuiz(submission);

        submission.setUsername(user2);
        submitQuiz(submission);

        // Delete first user's submission
        given()
                .when()
                .delete("/api/roommate-matching/quiz-submission/" + user1)
                .then()
                .statusCode(HttpStatus.OK.value());

        // Try to calculate match with deleted user
        given()
                .queryParam("username1", user1)
                .queryParam("username2", user2)
                .when()
                .get("/api/roommate-matching/calculate-match")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        // Try to match with non-existent user
        given()
                .queryParam("username1", "nonexistent_user")
                .queryParam("username2", user2)
                .when()
                .get("/api/roommate-matching/calculate-match")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    // Helper methods
    private QuizSubmissionDTO createDefaultSubmission(String username) {
        QuizSubmissionDTO submission = new QuizSubmissionDTO();
        submission.setUsername(username);
        submission.setMorningPerson(5);
        submission.setHosting(5);
        submission.setLikingPets(5);
        submission.setSmoking(5);
        submission.setOrganizationSkills(5);
        submission.setPeopleOver(5);
        submission.setNoiseLevel(5);
        submission.setCleanliness(5);
        return submission;
    }

    private void setAllScores(QuizSubmissionDTO submission, int score) {
        submission.setMorningPerson(score);
        submission.setHosting(score);
        submission.setLikingPets(score);
        submission.setSmoking(score);
        submission.setOrganizationSkills(score);
        submission.setPeopleOver(score);
        submission.setNoiseLevel(score);
        submission.setCleanliness(score);
    }

    private void setAllOtherScores(QuizSubmissionDTO submission, int score) {
        submission.setHosting(score);
        submission.setLikingPets(score);
        submission.setSmoking(score);
        submission.setOrganizationSkills(score);
        submission.setPeopleOver(score);
        submission.setNoiseLevel(score);
        submission.setCleanliness(score);
    }
}