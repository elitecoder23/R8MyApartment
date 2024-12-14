package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    // Define UI elements
    private TextView messageText;     // Display message to user
    private Button estButton, cstButton, mstButton, pstButton;     // Time zone selection buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // Link to Main activity XML layout

        // Initialize UI elements
        messageText = findViewById(R.id.main_msg_txt);      // Link to message textview in the Main activity XML
        estButton = findViewById(R.id.est_button);          // Link to Eastern Time button
        cstButton = findViewById(R.id.cst_button);          // Link to Central Time button
        mstButton = findViewById(R.id.mst_button);          // Link to Mountain Time button
        pstButton = findViewById(R.id.pst_button);          // Link to Pacific Time button


        messageText.setText("Select a Time Zone");  //initial message

        // Set click listeners for time zone buttons
        estButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to TimeDisplayActivity with Eastern Time zone
                navigateToTimeDisplayActivity("America/New_York");
            }
        });

        cstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to TimeDisplayActivity with Central Time zone
                navigateToTimeDisplayActivity("America/Chicago");
            }
        });

        mstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to TimeDisplayActivity with Mountain Time zone
                navigateToTimeDisplayActivity("America/Denver");
            }
        });

        pstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to TimeDisplayActivity with Pacific Time zone
                navigateToTimeDisplayActivity("America/Los_Angeles");
            }
        });
    }

    // Helper method to navigate to TimeDisplayActivity with selected time zone
    private void navigateToTimeDisplayActivity(String timeZoneId) {
        Intent intent = new Intent(MainActivity.this, TimeDisplayActivity.class); //create new intent to start TimeDisplayActivity
        intent.putExtra("TIME_ZONE", timeZoneId); // Pass time zone ID as extra, timeZoneId was obtained above
        startActivity(intent); //start TimeDisplayActivity with the Intent, launches the activity and passes the timeZoneId variable
    }
}