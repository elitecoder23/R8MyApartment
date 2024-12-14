package com.example.androidexample;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReviewTest {

    @Rule
    public ActivityScenarioRule<WelcomeActivity> activityRule =
            new ActivityScenarioRule<>(WelcomeActivity.class);

    @Test
    public void testCreateReview() {
        // Initialize Intents
        Intents.init();

        try {
            // Navigate to SignupActivity
            Espresso.onView(withId(R.id.buttonLogin)).perform(ViewActions.click());

            Espresso.onView(withId(R.id.login_user)).perform(ViewActions.replaceText("DecSysTest"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.login_pass)).perform(ViewActions.replaceText("decsystest"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.login_btn)).perform(ViewActions.click());

            Thread.sleep(500);

            Espresso.onView(withId(R.id.buttonCreateReview)).perform(ViewActions.click());

            Thread.sleep(500);

            Espresso.onView(withId(R.id.buttonCreateReview)).perform(ViewActions.click());

            Thread.sleep(500);

            Espresso.onView(withId(R.id.editTextApartmentName)).perform(ViewActions.replaceText("TestApt"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextReviewSubject)).perform(ViewActions.replaceText("TestSubject"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextReview)).perform(ViewActions.replaceText("This is a test for creating a review"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.editTextRating)).perform(ViewActions.replaceText("5"), ViewActions.closeSoftKeyboard());

            Espresso.onView(withId(R.id.buttonSubmitReview)).perform(ViewActions.click());

            Thread.sleep(500);

            intended(hasComponent(ReviewActivity.class.getName()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            // Release Intents
            Intents.release();
        }
    }

    @Test
    public void testGetReviewsByUser() {
        Intents.init();

        try {
            // Navigate to SignupActivity
            Espresso.onView(withId(R.id.buttonLogin)).perform(ViewActions.click());

            Espresso.onView(withId(R.id.login_user)).perform(ViewActions.replaceText("DecSysTest"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.login_pass)).perform(ViewActions.replaceText("decsystest"), ViewActions.closeSoftKeyboard());
            Espresso.onView(withId(R.id.login_btn)).perform(ViewActions.click());

            Thread.sleep(500);

            Espresso.onView(withId(R.id.buttonCreateReview)).perform(ViewActions.click());

            Thread.sleep(500);

            Espresso.onView(withId(R.id.buttonListReviews)).perform(ViewActions.click());

            Thread.sleep(500);

            intended(hasComponent(ListAllReviews.class.getName()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            // Release Intents
            Intents.release();
        }

    }
}