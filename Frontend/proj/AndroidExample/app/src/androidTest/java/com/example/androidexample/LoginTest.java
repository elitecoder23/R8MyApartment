package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    @Before
    public void setUP(){
        Intents.init();
    }
    @After
    public void tearDown(){
        Intents.release();
    }

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testSuccessfulLogin() {
        intents.init();
        // Input valid credentials
        onView(withId(R.id.login_user))
                .perform(typeText("vsoares"), closeSoftKeyboard());

        onView(withId(R.id.login_pass))
                .perform(typeText("vini"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.login_btn)).perform(click());

        Thread.sleep(2000);

        // Verify successful login messag
        intended(hasComponent(HomePageActivity.class.getName()));

    }

    @Test
    public void testFailedLogin() {
        // Input invalid credentials
        onView(withId(R.id.login_user))
                .perform(typeText("wronguser"), closeSoftKeyboard());

        onView(withId(R.id.login_pass))
                .perform(typeText("wrongpass"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.login_btn)).perform(click());

        // Verify failed login message
        onView(withId(R.id.main_msg_txt))
                .check(matches(withText("Login Failed, username or password incorrect")));
    }



}