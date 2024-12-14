package mk12;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mk12.controllers.FriendRequestPayload;
import mk12.controllers.ReviewController;
import mk12.controllers.RoommateController;
import mk12.dto.QuizSubmissionDTO;
import mk12.model.*;
import mk12.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JustinSystemTest {

    @LocalServerPort
    private int port;


    private ReviewService reviewService;
    private ReviewController reviewController;
    private static long reviewId = 1;

    @BeforeEach
    public void setUp() {

        RestAssured.port = port;
        System.out.println("Running tests on port: " + port);  // Print port for debugging
    }

/*    @Test
    public void testAddReview() {
        Review review = new Review();
        review.setComment("Great place!");
        review.setRating(5);
        review.setReviewText("I had a wonderful stay.");
        Apartment apartment = new Apartment();
        apartment.setId(1L);
        review.setApartment(apartment);

        given()
                .contentType(ContentType.JSON)
                .body(review)
                .when()
                .post("/api/reviews/addReview")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("comment", equalTo("Great place!"))
                .body("rating", equalTo(5))
                .body("reviewText", equalTo("I had a wonderful stay."));
    }


 */
 /*   @Test
    public void testGetAllReviews() {
        Response response = given()
                .when()
                .get("/api/reviews/getAllReviews")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

    }

  */

   /* @Test
    public void testEditReview() {
        Review updatedReview = new Review();
        updatedReview.setId(69); // Use Long for reviewId
        updatedReview.setComment("Updated comment");
        updatedReview.setRating(4);
        updatedReview.setReviewText("Updated review text");

        given()
                .contentType(ContentType.JSON)
                .queryParam("reviewId", 69) // Use reviewId as query parameter
                .body(updatedReview)
                .when()
                .put("/api/reviews/editReview") // Ensure the URL matches the one used in Postman
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("comment", equalTo("Updated comment"))
                .body("rating", equalTo(4))
                .body("reviewText", equalTo("Updated review text"));
    }



    @Test
    public void testDeleteReview() {
        Response response = given()
                .queryParam("reviewId", reviewId) // Use Long for reviewId
                .when()
                .delete("/api/reviews/deleteReview")
                .then()
                .extract().response();

        System.out.println("Response body: " + response.getBody().asString());

        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("Review deleted successfully"));

        reviewId++; // Increment reviewId for the next test
    }

 */

    @Test
    public void testCreateApartment() {
        ApartmentOwner owner = new ApartmentOwner();
        owner.setId(1L);
        owner.setEmail("jrhde23@iastate.edu");
        owner.setPassword("password");
        owner.setName("justin");

        Apartment apartment = new Apartment();
        apartment.setName("Sunset Villa");
        apartment.setPrice(1200.50);
        apartment.setAddress("123 Sunset Boulevard, Los Angeles, CA");
        apartment.setAmenities("Pool, Gym, WiFi");
        apartment.setContactPhone("123-456-7890");
        apartment.setContactEmail("owner@example.com");
        apartment.setOwner(owner);

        given()
                .contentType(ContentType.JSON)
                .queryParam("owner", "justin")
                .body(apartment)
                .when()
                .post("/api/apartments/create")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("Sunset Villa"))
                .body("price", equalTo(1200.50f))
                .body("address", equalTo("123 Sunset Boulevard, Los Angeles, CA"))
                .body("amenities", equalTo("Pool, Gym, WiFi"))
                .body("contactPhone", equalTo("123-456-7890"))
                .body("contactEmail", equalTo("owner@example.com"));
    }

    @Test
    public void testGetAllApartments() {
        Response response = given()
                .when()
                .get("/api/apartments/showall")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        // Add assertions based on the expected response
        System.out.println("Response body: " + response.getBody().asString());
    }

   /* @Test
    public void testUpdateApartment() {
        ApartmentOwner owner = new ApartmentOwner();
        owner.setId(3L);
        owner.setEmail("owner@example.com");
        owner.setPassword("password");
        owner.setName("vin dog");

        Apartment updatedApartment = new Apartment();
        updatedApartment.setName("Edge Updated");
        updatedApartment.setPrice(1300.75);
        updatedApartment.setAddress("123 Sunset Boulevard, Los Angeles, CA");
        updatedApartment.setAmenities("Pool, Gym, WiFi, Sauna");
        updatedApartment.setContactPhone("123-456-7890");
        updatedApartment.setContactEmail("owner@example.com");
        updatedApartment.setOwner(owner);

        given()
                .contentType(ContentType.JSON)
                .body(updatedApartment)
                .when()
                .put("/api/apartments/update/Edge Updated")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("Edge Updated"))
                .body("price", equalTo(1300.75f))
                .body("address", equalTo("123 Sunset Boulevard, Los Angeles, CA"))
                .body("amenities", equalTo("Pool, Gym, WiFi, Sauna"))
                .body("contactPhone", equalTo("123-456-7890"))
                .body("contactEmail", equalTo("owner@example.com"));
    }

    */
  /*  @Test
    public void testGetAllFriendRequests() {
        given()
                .when()
                .get("/api/friend-requests/jrohde23")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1))
                .body("[0].senderUsername", equalTo("jane_doe"))
                .body("[0].receiverUsername", equalTo("john_doe"))
                .body("[0].status", equalTo("PENDING"));    }

   */

    @Test
    public void testSendFriendRequest() {
        FriendRequestPayload payload = new FriendRequestPayload();
        payload.setSenderUsername("jrohde23");
        payload.setReceiverUsername("anishnag");

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/friend-requests")
                .then()
                .statusCode(HttpStatus.OK.value());
        // Add assertions based on the expected response
    }

    @Test
    public void testAcceptFriendRequest() {
        given()
                .when()
                .put("/api/friend-requests/jrohde23/anishnag/accept")
                .then()
                .statusCode(HttpStatus.OK.value());
        // Add assertions based on the expected response
    }

/*    @Test
    public void testDenyFriendRequest() {
        given()
                .when()
                .put("/api/friend-requests/jane_doe/deny")
                .then()
                .statusCode(HttpStatus.OK.value());
        // Add assertions based on the expected response
    }

 */

    @Test
    public void testListFriendsByUsername() {
        given()
                .when()
                .get("/api/friend-requests/anishnag/listFriendsByUsername")
                .then()
                .statusCode(HttpStatus.OK.value());
        // Add assertions based on the expected response
    }
  /*  @Test
    public void testGetReviewsByUser() {
        // Perform the GET request
        given()
                .when()
                .get("/api/reviews/getReviewsByUser/jrohde23")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(3))
                .body("[0].id", equalTo(66))
                .body("[0].userId", equalTo(0))
                .body("[0].comment", equalTo("Gross"))
                .body("[0].rating", equalTo(3))
                .body("[0].reviewText", equalTo("I lived here my first semester and it was pretty good, nothing special, but not bad. I ended up renewing my lease and in the new semester, everything with the entire place went to shit. The management is bad, lobby was always messy, elevators constantly broken, and it's overpriced! I would not recommend this place unless they dropped the rent from 780 to 500."))
                .body("[1].id", equalTo(67))
                .body("[1].userId", equalTo(0))
                .body("[1].comment", equalTo("Kabir"))
                .body("[1].rating", equalTo(3))
                .body("[1].reviewText", equalTo("I lived here my first semester and it was pretty good, nothing special, but not bad. I ended up renewing my lease and in the new semester, everything with the entire place went to shit. The management is bad, lobby was always messy, elevators constantly broken, and it's overpriced! I would not recommend this place unless they dropped the rent from 780 to 500."))
                .body("[2].id", equalTo(68))
                .body("[2].userId", equalTo(0))
                .body("[2].comment", equalTo("Kabir"))
                .body("[2].rating", equalTo(3))
                .body("[2].reviewText", equalTo("I lived here my first semester and it was pretty good, nothing special, but not bad. I ended up renewing my lease and in the new semester, everything with the entire place went to shit. The management is bad, lobby was always messy, elevators constantly broken, and it's overpriced! I would not recommend this place unless they dropped the rent from 780 to 500."));
    }


   */
//tests create roommate
    @Test
    public void testCreateRoommate() {
        Roommate roommate = new Roommate();
        roommate.setName("John Doe");
        roommate.setAge(25);
        roommate.setOccupation("Software Engineer");
        roommate.setLifestyle("Active");
        roommate.setBudgetRange("1000-1500");
        roommate.setMatchPercentage(85);
        roommate.setUsername("johndoe");
        roommate.setMorningPerson(1);
        roommate.setHosting(2);
        roommate.setPet(0);
        roommate.setSmoking(0);
        roommate.setOrganized(3);
        roommate.setPeopleOver(2);
        roommate.setNoise(1);
        roommate.setCleanliness(4);


        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"John Doe\",\"age\":25,\"occupation\":\"Software Engineer\",\"lifestyle\":\"Active\",\"budgetRange\":\"1000-1500\",\"matchPercentage\":85,\"username\":\"johndoe\",\"morningPerson\":1,\"hosting\":2,\"pet\":0,\"smoking\":0,\"organized\":3,\"peopleOver\":2,\"noise\":1,\"cleanliness\":4}")
                .when()
                .post("/roommates")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("John Doe"))
                .body("age", equalTo(25))
                .body("occupation", equalTo("Software Engineer"))
                .body("lifestyle", equalTo("Active"))
                .body("budgetRange", equalTo("1000-1500"))
                .body("matchPercentage", equalTo(85))
                .body("username", equalTo("johndoe"))
                .body("morningPerson", equalTo(1))
                .body("hosting", equalTo(2))
                .body("pet", equalTo(0))
                .body("smoking", equalTo(0))
                .body("organized", equalTo(3))
                .body("peopleOver", equalTo(2))
                .body("noise", equalTo(1))
                .body("cleanliness", equalTo(4));
    }
   /* @Test
    public void testGetRoommateByName() {
        Roommate roommate = new Roommate();
        roommate.setName("Justin");
        roommate.setAge(22);
        roommate.setOccupation("Cyber security Engineer");
        roommate.setLifestyle("Active");
        roommate.setBudgetRange("1000-1500");
        roommate.setMatchPercentage(85);
        roommate.setUsername("jrohde23");
        roommate.setMorningPerson(1);
        roommate.setHosting(2);
        roommate.setPet(0);
        roommate.setSmoking(0);
        roommate.setOrganized(3);
        roommate.setPeopleOver(2);
        roommate.setNoise(1);
        roommate.setCleanliness(4);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/roommates/{username}", "jrohde23")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("Justin"))
                .body("age", equalTo(22))
                .body("occupation", equalTo("Software Engineer"))
                .body("lifestyle", equalTo("Active"))
                .body("budgetRange", equalTo("1000-1500"))
                .body("matchPercentage", equalTo(85))
                .body("username", equalTo("jrohde23"))
                .body("morningPerson", equalTo(1))
                .body("hosting", equalTo(2))
                .body("pet", equalTo(0))
                .body("smoking", equalTo(0))
                .body("organized", equalTo(3))
                .body("peopleOver", equalTo(2))
                .body("noise", equalTo(1))
                .body("cleanliness", equalTo(4));
    }


    */
    @Test
    public void testGetRoommateByName_NotFound() {
       // when(roommateService.getRoommateByName("NonExistent")).thenReturn(null);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/roommates/NonExistent")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
    @Test
    public void testAddReviewForUser() {
        Review review = new Review();
        review.setComment("Poop");
        review.setRating(4);
        review.setReviewText("I lived here my first semester and it was pretty good, nothing special, but not bad. I ended up renewing my lease and in the new semester, everything with the entire place went to shit. The management is bad, lobby was always messy, elevators constantly broken, and it's overpriced! I would not recommend this place unless they dropped the rent from 780 to 500.");
        Apartment apartment = new Apartment();
        apartment.setName("TheFoundry");
        review.setApartment(apartment);



        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"jrohde23\",\"comment\":\"Poop\",\"rating\":4,\"reviewText\":\"I lived here my first semester and it was pretty good, nothing special, but not bad. I ended up renewing my lease and in the new semester, everything with the entire place went to shit. The management is bad, lobby was always messy, elevators constantly broken, and it's overpriced! I would not recommend this place unless they dropped the rent from 780 to 500.\",\"apartment\":{\"name\":\"TheFoundry\"}}")
                .when()
                .post("/api/reviews/addReviewForUser/jrohde23")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("comment", equalTo("Poop"))
                .body("rating", equalTo(4))
                .body("reviewText", equalTo("I lived here my first semester and it was pretty good, nothing special, but not bad. I ended up renewing my lease and in the new semester, everything with the entire place went to shit. The management is bad, lobby was always messy, elevators constantly broken, and it's overpriced! I would not recommend this place unless they dropped the rent from 780 to 500."))
                .body("apartment.name", equalTo("TheFoundry"));
    }

   /*
   @Test
    public void testCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("1234567890");
        user.setBirthDate("1990-01-01");

        // Print the request body

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .log().all()  // Log the entire request
                .when()
                .post("/users/signup")
                .then()
                .log().all()  // Log the entire response
                .extract()
                .response();

        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody().asString());

        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body("username", equalTo("testuser"))
                .body("email", equalTo("testuser@example.com"));
    }

    */

    @Test
    public void testGetAllUsers() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testGetUserByPathVariable() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/users/testuser")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo("testuser"));
    }

    @Test
    public void testGetUserByQueryParameter() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("username", "testuser")
                .when()
                .get("/users/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo("testuser"));
    }

   /* @Test
    public void testUpdateUser() {
        User updatedUser = new User();
        updatedUser.setUsername("jrohde23");
        updatedUser.setFirstName("UpdatedFirstName");
        updatedUser.setLastName("UpdatedLastName");
        updatedUser.setEmail("updateduser@example.com");
        updatedUser.setBirthDate("2000-01-01");
        updatedUser.setPhoneNumber("1234567890");
        updatedUser.setPassword("newpassword123");

        given()
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when()
                .put("/users/jrohde23")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo("jrohde23"))
                .body("firstName", equalTo("UpdatedFirstName"))
                .body("lastName", equalTo("UpdatedLastName"))
                .body("email", equalTo("updateduser@example.com"))
                .body("birthDate", equalTo("2000-01-01"))
                .body("phoneNumber", equalTo("1234567890"))
                .body("password", equalTo("newpassword123"));
    }

    */


    @Test
    public void testFindMatches() {
        String username = "testuser";

        given()
                .contentType(ContentType.JSON)
                .queryParam("username", username)
                .when()
                .get("/api/roommate-matching/find-matches")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", not(empty()))  // Verify that matches exist
                .body("[0]", hasKey("username"))  // Verify structure of first match
                .body("[0]", hasKey("starRating"))
                .body("[0]", hasKey("matchedUserProfile"));
    }
    @Test
    public void testGetQuizSubmissionByUsername() {
        String username = "testuser";

        given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .when()
                .get("/api/roommate-matching/quiz-submission/{username}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo(username));
    }


}