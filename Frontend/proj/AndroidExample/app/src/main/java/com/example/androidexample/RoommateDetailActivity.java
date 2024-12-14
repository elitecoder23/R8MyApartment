package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RoommateDetailActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView emailTextView;
    private TextView birthDateTextView;
    private TextView phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommate_detail);

        usernameTextView = findViewById(R.id.roommate_detail_username);
        firstNameTextView = findViewById(R.id.roommate_detail_first_name);
        lastNameTextView = findViewById(R.id.roommate_detail_last_name);
        emailTextView = findViewById(R.id.roommate_detail_email);
        birthDateTextView = findViewById(R.id.roommate_detail_birth_date);
        phoneNumberTextView = findViewById(R.id.roommate_detail_phone_number);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String email = intent.getStringExtra("email");
        String birthDate = intent.getStringExtra("birthDate");
        String phoneNumber = intent.getStringExtra("phoneNumber");

        usernameTextView.setText("Username: " + username);
        firstNameTextView.setText("Full Name: " + firstName + " " + lastName);
        emailTextView.setText("Email: " + email);
        birthDateTextView.setText("Birth Date: " + birthDate);
        phoneNumberTextView.setText("Phone Number: " + phoneNumber);
    }
}