package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FriendRequest extends AppCompatActivity {
    private WebSocketService webSocketService;
    private EditText friendUsernameEditText;
    private Button sendRequestButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendrequest);

        webSocketService = new WebSocketService(this);
        friendUsernameEditText = findViewById(R.id.friend_username_edit_text);
        sendRequestButton = findViewById(R.id.send_request_button);


        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendUsername = friendUsernameEditText.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                if (friendUsername.isEmpty()) {
                    Toast.makeText(FriendRequest.this, "Please enter a friend username", Toast.LENGTH_SHORT).show();
                } else {
                    RoommateFinderActivity.webSocketService.sendNewReq(username, friendUsername);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketService.closeConnection();
    }
}