package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

// Activity to display the current time for a selected time zone
public class TimeDisplayActivity extends AppCompatActivity {

    // UI element to display the current time
    private TextView timeDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_display); // Link to TimeDisplayActivity XML layout

        // Initialize back button
        Button backButton = findViewById(R.id.back_button);

        // Initialize time display text view
        timeDisplay = findViewById(R.id.time_display);

        // Set click listener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get the selected time zone ID from the Intent extra
        String timeZoneID = getIntent().getStringExtra("TIME_ZONE");

        // If a time zone ID was passed, display the current time for that zone
        if (timeZoneID != null) {
            displayTimeForZone(timeZoneID);
        }
    }

    // Helper method to display the current time for a given time zone
    private void displayTimeForZone(String timeZoneId) {

        Calendar calendar = Calendar.getInstance(); // Get the current calendar instance
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId); // Get the time zone object for the given ID
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a"); // Create a simple date format to display the time in 12-hour format

        sdf.setTimeZone(timeZone); // Set the time zone for the simple date format
        String currentTime = sdf.format(calendar.getTime());  // Format the current time using the simple date format
        timeDisplay.setText("Current time in " + timeZoneId + "  -->  " + currentTime); // Display the current time in the time display text view
    }
}