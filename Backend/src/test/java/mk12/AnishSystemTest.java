package mk12;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mk12.dto.*;
import mk12.model.Apartment;
import mk12.model.*;
import mk12.model.ChatMessage;
import mk12.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mk12.dto.UserReviewDTO;
import mk12.model.UserOwnerReview;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnishSystemTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // Apartment Management Tests

    @Test
    public void testApartmentLifecycle() {
        // Generate unique identifiers for test data
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String ownerEmail = "test." + uniqueId + "@example.com";
        String ownerPassword = "password123";
        String ownerName = "Test Owner " + uniqueId;

        // Step 1: Register an owner
        given()
                .contentType(ContentType.JSON)
                .body(new ApartmentOwnerDTO() {{
                    setEmail(ownerEmail);
                    setPassword(ownerPassword);
                    setName(ownerName);
                }})
                .when()
                .post("/api/owners/register")
                .then()
                .statusCode(HttpStatus.OK.value());

        // Step 2: Create new apartment
        String apartmentName = "Test Apartment " + uniqueId;
        float initialPrice = 1500.0f;
        given()
                .contentType(ContentType.JSON)
                .body(new Apartment() {{
                    setName(apartmentName);
                    setPrice(initialPrice);
                    setAddress("123 Test St");
                    setAmenities("Wifi, Parking");
                    setContactPhone("123-456-7890");
                    setContactEmail("apartment@test.com");
                }})
                .queryParam("owner", ownerName)
                .when()
                .post("/api/apartments/create")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(apartmentName))
                .body("price", equalTo(initialPrice))
                .body("owner.name", equalTo(ownerName));

        // Step 3: Verify apartment was created and can be retrieved
        Response getResponse = given()
                .when()
                .get("/api/apartments/name/" + apartmentName)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        assertEquals(apartmentName, getResponse.path("name"));
        assertEquals("123 Test St", getResponse.path("address"));
        assertEquals("Wifi, Parking", getResponse.path("amenities"));
        assertEquals("123-456-7890", getResponse.path("contactPhone"));
        assertEquals("apartment@test.com", getResponse.path("contactEmail"));

        float returnedPrice = getResponse.path("price");
        assertEquals(initialPrice, returnedPrice, 0.001);

        // Step 4: Update apartment
        float newPrice = 1600.0f;
        given()
                .contentType(ContentType.JSON)
                .body(new Apartment() {{
                    setName(apartmentName);
                    setPrice(newPrice);
                    setAddress("123 Test St");
                    setAmenities("Wifi, Parking, Pool");
                    setContactPhone("123-456-7890");
                    setContactEmail("apartment@test.com");
                }})
                .when()
                .put("/api/apartments/update/" + apartmentName)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("price", equalTo(newPrice))
                .body("amenities", equalTo("Wifi, Parking, Pool"));

        // Step 5: Verify the update
        Response updatedResponse = given()
                .when()
                .get("/api/apartments/name/" + apartmentName)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        float updatedPrice = updatedResponse.path("price");
        assertEquals(newPrice, updatedPrice, 0.001);
        assertEquals("Wifi, Parking, Pool", updatedResponse.path("amenities"));

        // Step 6: Delete apartment
        given()
                .when()
                .delete("/api/apartments/" + apartmentName + "/delete")
                .then()
                .statusCode(HttpStatus.OK.value());

        // Step 7: Verify apartment no longer exists
        given()
                .when()
                .get("/api/apartments/name/" + apartmentName)
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void testMultipleApartmentsPerOwner() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String ownerEmail = "multi." + uniqueId + "@example.com";
        String ownerPassword = "password123";
        String ownerName = "Multi Owner " + uniqueId;

        // Register owner
        given()
                .contentType(ContentType.JSON)
                .body(new ApartmentOwnerDTO() {{
                    setEmail(ownerEmail);
                    setPassword(ownerPassword);
                    setName(ownerName);
                }})
                .when()
                .post("/api/owners/register")
                .then()
                .statusCode(HttpStatus.OK.value());

        // Create first apartment
        String firstAptName = "First Apartment " + uniqueId;
        given()
                .contentType(ContentType.JSON)
                .body(new Apartment() {{
                    setName(firstAptName);
                    setPrice(1000.0);
                    setAddress("First St");
                    setContactPhone("111-111-1111");
                    setContactEmail("first@test.com");
                }})
                .queryParam("owner", ownerName)
                .when()
                .post("/api/apartments/create")
                .then()
                .statusCode(HttpStatus.OK.value());

        // Create second apartment
        String secondAptName = "Second Apartment " + uniqueId;
        given()
                .contentType(ContentType.JSON)
                .body(new Apartment() {{
                    setName(secondAptName);
                    setPrice(1200.0);
                    setAddress("Second St");
                    setContactPhone("222-222-2222");
                    setContactEmail("second@test.com");
                }})
                .queryParam("owner", ownerName)
                .when()
                .post("/api/apartments/create")
                .then()
                .statusCode(HttpStatus.OK.value());

        // Verify owner has both apartments
        Response ownerApartments = given()
                .when()
                .get("/api/apartments/owner/name/" + ownerName)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        List<String> apartmentNames = ownerApartments.jsonPath().getList("name");
        assertEquals(2, apartmentNames.size());
        assertTrue(apartmentNames.contains(firstAptName));
        assertTrue(apartmentNames.contains(secondAptName));
    }

    @Test
    public void testApartmentValidation() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String ownerEmail = "validation." + uniqueId + "@example.com";
        String ownerPassword = "password123";
        String ownerName = "Validation Owner " + uniqueId;

        // Register owner
        given()
                .contentType(ContentType.JSON)
                .body(new ApartmentOwnerDTO() {{
                    setEmail(ownerEmail);
                    setPassword(ownerPassword);
                    setName(ownerName);
                }})
                .when()
                .post("/api/owners/register")
                .then()
                .statusCode(HttpStatus.OK.value());

        // Test invalid price (negative)
        given()
                .contentType(ContentType.JSON)
                .body(new Apartment() {{
                    setName("Invalid Price Apt " + uniqueId);
                    setPrice(-100.0);
                    setAddress("Test St");
                    setContactPhone("123-456-7890");
                    setContactEmail("test@test.com");
                }})
                .queryParam("owner", ownerName)
                .when()
                .post("/api/apartments/create")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Test missing required fields
        given()
                .contentType(ContentType.JSON)
                .body(new Apartment() {{
                    setName("Missing Fields Apt " + uniqueId);
                    setPrice(1000.0);
                    // Missing address, contact phone, and email
                }})
                .queryParam("owner", ownerName)
                .when()
                .post("/api/apartments/create")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void testApartmentListingAndSearch() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String ownerEmail = "listing." + uniqueId + "@example.com";
        String ownerPassword = "password123";
        String ownerName = "Listing Owner " + uniqueId;

        // Register owner
        given()
                .contentType(ContentType.JSON)
                .body(new ApartmentOwnerDTO() {{
                    setEmail(ownerEmail);
                    setPassword(ownerPassword);
                    setName(ownerName);
                }})
                .when()
                .post("/api/owners/register")
                .then()
                .statusCode(HttpStatus.OK.value());

        String[] apartmentNames = {
                "Luxury Apt " + uniqueId,
                "Budget Apt " + uniqueId,
                "Mid-range Apt " + uniqueId
        };
        double[] prices = {2000.0, 800.0, 1200.0};

        for (int i = 0; i < apartmentNames.length; i++) {
            int finalI = i;
            given()
                    .contentType(ContentType.JSON)
                    .body(new Apartment() {{
                        setName(apartmentNames[finalI]);
                        setPrice(prices[finalI]);
                        setAddress(finalI + " Test St");
                        setContactPhone("123-456-789" + finalI);
                        setContactEmail("apt" + finalI + "@test.com");
                    }})
                    .queryParam("owner", ownerName)
                    .when()
                    .post("/api/apartments/create")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        // Test get all apartments
        Response allApartments = given()
                .when()
                .get("/api/apartments/showall")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        List<String> retrievedNames = allApartments.jsonPath().getList("name");
        assertTrue(retrievedNames.containsAll(Arrays.asList(apartmentNames)));

        // Test finding specific apartment
        given()
                .when()
                .get("/api/apartments/name/" + apartmentNames[0])
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("price", equalTo(2000.0f));
    }

    // Roommate Matching Tests

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

        // Test finding matches
        Response matchesResponse = given()
                .queryParam("username", username1)
                .when()
                .get("/api/roommate-matching/find-matches")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        List<MatchResultDTO> matches = matchesResponse.jsonPath().getList("$", MatchResultDTO.class);
        assertFalse(matches.isEmpty(), "Should find at least one match");

        // Verify match ordering
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
        setAllOtherScores(aboveMaxSubmission, 5);

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
        setAllOtherScores(emptyUsername, 5);

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
        setAllScores(initial, 5);
        submitQuiz(initial);

        // Test partial update
        QuizSubmissionDTO partialUpdate = new QuizSubmissionDTO();
        partialUpdate.setUsername(username);
        partialUpdate.setMorningPerson(7);
        setAllOtherScores(partialUpdate, 5);

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
        setAllScores(submission, 5);
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
    //put new tests under here

    @Test
    public void testAdminAccountManagement() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String adminEmail = "admin." + uniqueId + "@example.com";
        String adminPassword = "adminPass123!";

        // Test admin signup
        Response signupResponse = given()
                .contentType(ContentType.JSON)
                .body(new AdminLoginDTO() {{
                    setEmail(adminEmail);
                    setPassword(adminPassword);
                    setName("Test Admin " + uniqueId);
                }})
                .when()
                .post("/api/admin/signup")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        // Extract and verify token
        String adminToken = signupResponse.path("data.token");
        assertNotNull(adminToken, "Admin token should not be null");

        // Test admin login with valid credentials
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(new AdminLoginDTO() {{
                    setEmail(adminEmail);
                    setPassword(adminPassword);
                }})
                .when()
                .post("/api/admin/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        // Use assertEquals instead of assertTrue for checking response values
        assertEquals(true, loginResponse.path("success"));
        assertEquals(adminEmail, loginResponse.path("data.email"));

        // Test admin login with invalid credentials
        given()
                .contentType(ContentType.JSON)
                .body(new AdminLoginDTO() {{
                    setEmail(adminEmail);
                    setPassword("wrongPassword");
                }})
                .when()
                .post("/api/admin/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

//    @Test
//    public void testAdminReportManagement() {
//        // Setup: Create admin, apartment, and reports
//        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
//        String adminEmail = "admin." + uniqueId + "@example.com";
//        String adminToken = setupAdmin(adminEmail, "adminPass123!");
//        String apartmentName = setupApartment(uniqueId);
//
//        // Create multiple test reports
//        createTestReport(apartmentName, "user1", "Inappropriate content");
//        createTestReport(apartmentName, "user2", "Misleading information");
//        createTestReport(apartmentName, "user3", "Spam listing");
//
//        // Test getting all reports with pagination
//        Response allReportsResponse = given()
//                .header("Authorization", "Bearer " + adminToken)
//                .queryParam("page", 0)
//                .queryParam("size", 10)
//                .when()
//                .get("/api/admin/reports")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .extract().response();
//
//        // Use assertEquals instead of assertTrue
//        assertEquals(true, allReportsResponse.path("success"));
//
//        List<Map<String, Object>> reports = allReportsResponse.path("data.content");
//        assertFalse(reports.isEmpty());
//
//        // Test getting pending reports
//        Response pendingReportsResponse = given()
//                .header("Authorization", "Bearer " + adminToken)
//                .queryParam("page", 0)
//                .queryParam("size", 10)
//                .when()
//                .get("/api/admin/reports/pending")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .extract().response();
//
//        List<Map<String, Object>> pendingReports = pendingReportsResponse.path("data.content");
//
//        // Verify all reports are pending
//        for (Map<String, Object> report : pendingReports) {
//            assertEquals("PENDING", report.get("status"));
//        }
//
//        // Test reviewing a report
//        Long reportId = Long.valueOf(pendingReports.get(0).get("id").toString());
//        Response reviewResponse = given()
//                .header("Authorization", "Bearer " + adminToken)
//                .queryParam("status", "REVIEWED")
//                .when()
//                .put("/api/admin/reports/" + reportId + "/review")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .extract().response();
//
//        assertEquals("REVIEWED", reviewResponse.path("data.status"));
//    }

    private String setupApartment(String uniqueId) {
        return uniqueId;
    }



    private void createTestReport(String apartmentName, String user3, String testReport) {
    }

    private void createTestReview(String apartmentName, String user1, int i, String goodApartment) {
    }

    private String setupAdmin(String adminEmail, String s) {
        return adminEmail;
    }




@Test
public void testChatMessageCreation() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);

    ChatMessage message = new ChatMessage(
            "sender_" + uniqueId,
            "Test message content",
            "receiver_" + uniqueId
    );

    // Verify message properties
    assertEquals("sender_" + uniqueId, message.getSender());
    assertEquals("Test message content", message.getContent());
    assertEquals("receiver_" + uniqueId, message.getRecipient());
    assertNotNull(message.getTimestamp());
    assertNotNull(message.getMessageId());
}
//more new tests below this
@Test
public void testUserOwnerReviewCreation() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);

    // Create review
    UserReviewDTO reviewDTO = new UserReviewDTO();
    reviewDTO.setUserUsername("user_" + uniqueId);
    reviewDTO.setReviewText("Great experience with this owner!");

    Response response = given()
            .contentType(ContentType.JSON)
            .body(reviewDTO)
            .when()
            .post("/api/userReview")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract().response();

    // Verify the review was created with correct data
    assertNotNull(response.path("id"));
    assertEquals(reviewDTO.getUserUsername(), response.path("userUsername"));
    assertEquals(reviewDTO.getReviewText(), response.path("reviewText"));
}

    @Test
    public void testGetAllUserOwnerReviews() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        // Create multiple reviews
        UserReviewDTO review1 = new UserReviewDTO();
        review1.setUserUsername("user1_" + uniqueId);
        review1.setReviewText("First test review");

        UserReviewDTO review2 = new UserReviewDTO();
        review2.setUserUsername("user2_" + uniqueId);
        review2.setReviewText("Second test review");

        // Submit reviews
        given()
                .contentType(ContentType.JSON)
                .body(review1)
                .post("/api/userReview");

        given()
                .contentType(ContentType.JSON)
                .body(review2)
                .post("/api/userReview");

        // Get all reviews
        Response response = given()
                .when()
                .get("/api/userReview")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        List<Map<String, Object>> reviews = response.path("$");
        assertTrue(reviews.size() >= 2, "Should have at least 2 reviews");
    }

    @Test
    public void testGetReviewsByUserUsername() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String testUsername = "user_" + uniqueId;

        // Create multiple reviews for the same user
        UserReviewDTO review1 = new UserReviewDTO();
        review1.setUserUsername(testUsername);
        review1.setReviewText("First review by user");

        UserReviewDTO review2 = new UserReviewDTO();
        review2.setUserUsername(testUsername);
        review2.setReviewText("Second review by user");

        // Submit reviews
        given()
                .contentType(ContentType.JSON)
                .body(review1)
                .post("/api/userReview");

        given()
                .contentType(ContentType.JSON)
                .body(review2)
                .post("/api/userReview");

        // Get reviews for specific user
        Response response = given()
                .when()
                .get("/api/userReview/user/" + testUsername)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        List<Map<String, Object>> reviews = response.path("$");
        assertFalse(reviews.isEmpty(), "Should have reviews for the user");

        // Verify all reviews belong to the test user
        for (Map<String, Object> review : reviews) {
            assertEquals(testUsername, review.get("userUsername"));
        }
    }





    private UserOwnerReview createTestUserOwnerReviewObject(String uniqueId) {
        UserOwnerReview review = new UserOwnerReview();
        review.setOwnerUsername("owner_" + uniqueId);
        review.setUserUsername("user_" + uniqueId);
        review.setReviewText("Test review text for " + uniqueId);
        review.setRating(4);
        return review;
    }


}