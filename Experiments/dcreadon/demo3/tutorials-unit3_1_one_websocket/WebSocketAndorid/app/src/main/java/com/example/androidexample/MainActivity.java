package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.java_websocket.handshake.ServerHandshake;

public class MainActivity extends AppCompatActivity implements WebSocketListener{

    private Button connectBtn;
    private EditText usernameEtx;
    private static final String SERVER_URL = "ws://coms-3090-057.class.las.iastate.edu:8080/chat/";
    //ws://10.0.2.2:8080/chat/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* initialize UI elements */
        connectBtn = (Button) findViewById(R.id.connectBtn);
        usernameEtx = (EditText) findViewById(R.id.unameEdt);

        /* connect button listener */
        connectBtn.setOnClickListener(view -> {
            String serverUrl = SERVER_URL + usernameEtx.getText().toString();

            // Establish WebSocket connection and set listener
            WebSocketManager.getInstance().connectWebSocket(serverUrl);
            WebSocketManager.getInstance().setWebSocketListener(MainActivity.this);

            // got to chat activity
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });
    }


    @Override
    public void onWebSocketMessage(String message) {}

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {}

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}