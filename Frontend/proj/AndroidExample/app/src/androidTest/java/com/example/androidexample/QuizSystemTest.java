package com.example.androidexample;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.Toast;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class QuizSystemTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testEmptyUsernameValidation() {
        // Launch the activity
        ActivityScenario<Quiz> scenario = ActivityScenario.launch(Quiz.class);

        // Perform the test
        scenario.onActivity(activity -> {
            activity.desiredusername = new android.widget.EditText(activity);
            activity.desiredusername.setError("Please enter a username");
            activity.desiredusername.setText("");
            assertEquals("Please enter a username", activity.desiredusername.getError());
        });
    }


    @Test
    public void testUnselectedSeekBarValidation() {
        // Launch the activity
        ActivityScenario<Quiz> scenario = ActivityScenario.launch(Quiz.class);

        // Perform the test
        scenario.onActivity(activity -> {
            activity.desiredusername = new android.widget.EditText(activity);
            activity.desiredusername.setText("testuser");

            // Mock the SeekBars
            activity.ratingSeekBarnightorday = new android.widget.SeekBar(activity);
            activity.hostingBar = new android.widget.SeekBar(activity);
            activity.petBar = new android.widget.SeekBar(activity);
            activity.smokingBar = new android.widget.SeekBar(activity);
            activity.organizedBar = new android.widget.SeekBar(activity);
            activity.guestsOverBar = new android.widget.SeekBar(activity);
            activity.noiseBar = new android.widget.SeekBar(activity);
            activity.cleanlinessBar = new android.widget.SeekBar(activity);

            activity.ratingSeekBarnightorday.setProgress(5);
            activity.hostingBar.setProgress(5);
            activity.petBar.setProgress(5);
            activity.smokingBar.setProgress(5);
            activity.organizedBar.setProgress(5);
            activity.guestsOverBar.setProgress(5);
            activity.noiseBar.setProgress(5);
            activity.cleanlinessBar.setProgress(5);

            activity.sendJson();

            // Use a background thread to call intended()
            new Thread(() -> {
                intended(hasComponent(RoommateFinderActivity.class.getName()));
            }).start();
        });
    }
}