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
public class AdminLoginTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Rule
    public ActivityScenarioRule<AdminLoginActivity> activityRule =
            new ActivityScenarioRule<>(AdminLoginActivity.class);

    @Test
    public void testSuccessfulAdminLogin() {
        // Input valid credentials
        onView(withId(R.id.email))
                .perform(typeText("admin@iastate.edu"), closeSoftKeyboard());

        onView(withId(R.id.passwd))
                .perform(typeText("Admin123!"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.login_button)).perform(click());

        // Verify successful login
        intended(hasComponent(AdminDashboard.class.getName()));
    }

    @Test
    public void testFailedAdminLogin() {
        // Input invalid credentials
        onView(withId(R.id.email))
                .perform(typeText("wrongadmin@example.com"), closeSoftKeyboard());

        onView(withId(R.id.passwd))
                .perform(typeText("wrongpassword"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.login_button)).perform(click());

        // Verify failed login message
        onView(withId(R.id.admin_msg_txt))
                .check(matches(withText("Admin Login")));
    }
}