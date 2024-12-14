package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FriendDetailActivity extends AppCompatActivity {
    private Button chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);

        TextView usernameTextView = findViewById(R.id.friend_detail_username);
        TextView firstNameTextView = findViewById(R.id.friend_detail_first_name);
        TextView lastNameTextView = findViewById(R.id.friend_detail_last_name);
        TextView emailTextView = findViewById(R.id.friend_detail_email);
        TextView birthDateTextView = findViewById(R.id.friend_detail_birth_date);
        TextView phoneNumberTextView = findViewById(R.id.friend_detail_phone_number);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String email = intent.getStringExtra("email");


        usernameTextView.setText("Username:" +username);
        firstNameTextView.setText("Full Name:" +firstName + " " + lastName);
        emailTextView.setText("Email:" +email);

        chatButton = findViewById(R.id.chat_button);
        chatButton.setOnClickListener(v -> {
            Intent chatIntent = new Intent(this, ChatActivity.class);
            chatIntent.putExtra("username", username);
            startActivity(chatIntent);
        });
    }
}