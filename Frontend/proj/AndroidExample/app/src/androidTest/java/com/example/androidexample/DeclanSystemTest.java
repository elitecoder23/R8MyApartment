package com.example.androidexample;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Root;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.filters.LargeTest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.os.IBinder;
import android.view.WindowManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


//note that these tests will not pass unless these values are changed or removed from the database because the tests are working with the actual server
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeclanSystemTest {


    @Rule
    public ActivityScenarioRule<WelcomeActivity> activityRule =
            new ActivityScenarioRule<>(WelcomeActivity.class);




    @Test
    public void testSignup() {
        // Initialize Intents
        Intents.init();

        try {
            // Navigate to SignupActivity
            Espresso.onView(withId(R.id.buttonSignup)).perform(ViewActions.click());

            // Set default values in SignupActivity
            Espresso.onView(withId(R.id.editTextFirstName)).perform(ViewActions.replaceText("Test3"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextLastName)).perform(ViewActions.replaceText("User3"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextUsername)).perform(ViewActions.replaceText("testingSignup3"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextEmail)).perform(ViewActions.replaceText("testSign3@gmail.com"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextBirthDate)).perform(ViewActions.replaceText("01/01/2000"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextPhoneNumber)).perform(ViewActions.replaceText("1234567890"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextPassword)).perform(ViewActions.replaceText("password"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextConfirmPassword)).perform(ViewActions.replaceText("password"), ViewActions.closeSoftKeyboard());

            // Perform signup
            Espresso.onView(withId(R.id.buttonSignUp)).perform(ViewActions.click());

            // Verify that WelcomeActivity is launched
            intended(hasComponent(WelcomeActivity.class.getName()));
        } finally {
            // Release Intents
            Intents.release();
        }
    }

    @Test(timeout = 30000)
    public void testLandlordSignup() {
        // Initialize Intents
        Intents.init();

        try {
            // First navigate to landlord login from welcome activity
            Espresso.onView(withId(R.id.LandlordLogin))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.click());

            // Click on Sign Up link
            Espresso.onView(withId(R.id.textViewSignUpLink))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.click());

            // Fill in the sign up form
            Espresso.onView(withId(R.id.editTextSignUpUsername))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.replaceText("test11"), ViewActions.closeSoftKeyboard());

            Espresso.onView(withId(R.id.editTextSignUpPassword))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.replaceText("password"), ViewActions.closeSoftKeyboard());

            Espresso.onView(withId(R.id.editTextEmail))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.replaceText("test11@test.com"), ViewActions.closeSoftKeyboard());


            // Click Sign Up button
            Espresso.onView(withId(R.id.buttonSignUp))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.click());

            // Verify navigation to CreateListingActivity

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 10000) { // 10 second timeout
                try {
                    intended(hasComponent(CreateListingActivity.class.getName()));
                    break; // If we get here, the activity was found
                } catch (AssertionError e) {
                    // Keep trying until timeout
                    Thread.sleep(500);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Clean up
            Intents.release();
        }
    }

    //This test is going to test the ability to create a listing
    //I am going to sign a landlord in, and then I am going to create the listing
    //If the listing is successfully created, the test will pass

    @Test
    public void testCreateListing(){

        Intents.init();

        try {
            // Navigate to SignUp dialog
            Espresso.onView(withId(R.id.LandlordLogin)).perform(ViewActions.click());

            Espresso.onView(withId(R.id.editTextUsername)).perform(ViewActions.replaceText("test11@test.com"), ViewActions.closeSoftKeyboard());

            Espresso.onView(withId(R.id.editTextPassword)).perform(ViewActions.replaceText("password"), ViewActions.closeSoftKeyboard());

            Espresso.onView(withId(R.id.buttonLogin)).perform(ViewActions.click());

            Thread.sleep(2000);

            // Set default values in CreateListingActivity
            Espresso.onView(withId(R.id.editTextName)).perform(ViewActions.replaceText("Apartment Village"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextPrice)).perform(ViewActions.replaceText("1000"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextAddress)).perform(ViewActions.replaceText("2122 Lincoln Way, Ames, Iowa, 50014"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextAmenities)).perform(ViewActions.replaceText("Pool, Gym, Laundry"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextPhone)).perform(ViewActions.replaceText("5151234567"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextEmail)).perform(ViewActions.replaceText("rent@rentnow.com"), ViewActions.closeSoftKeyboard());

            // Perform create listing
            Espresso.onView(withId(R.id.buttonCreateListing)).perform(ViewActions.click());
            Thread.sleep(2000);

            intended(hasComponent(HomePageActivity.class.getName()));


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            Intents.release();
        }

    }
    //This last test is going to test deleting a review
    //It is going to delete the review that was just made in the previous test
    //need to login the user and then press show listings and then press delete
    @Test
    public void testDeleteListing() {

        try {
            // Navigate to SignUp dialog
            Espresso.onView(withId(R.id.LandlordLogin)).perform(ViewActions.click());

            Espresso.onView(withId(R.id.editTextUsername)).perform(ViewActions.replaceText("test11@test.com"), ViewActions.closeSoftKeyboard());

            Espresso.onView(withId(R.id.editTextPassword)).perform(ViewActions.replaceText("password"), ViewActions.closeSoftKeyboard());

            Espresso.onView(withId(R.id.buttonLogin)).perform(ViewActions.click());

            Thread.sleep(2000);

            Espresso.onView(withId(R.id.buttonViewListings)).perform(ViewActions.click());
            Thread.sleep(2000);

            Espresso.onView(withText("Delete")).check(matches(isDisplayed())).perform(ViewActions.click());

            Thread.sleep(2000);

            Espresso.onView(withId(R.id.listingsContainer))
                    .check(matches(CustomMatchers.withListSize(0)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}




