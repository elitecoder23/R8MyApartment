// ChatActivity.java
package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import org.java_websocket.handshake.ServerHandshake;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private Button sendBtn;
    private EditText msgEtx;
    private LinearLayout messageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendBtn = findViewById(R.id.sendBtn);
        msgEtx = findViewById(R.id.msgEdt);
        messageContainer = findViewById(R.id.mesg);

        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);

        sendBtn.setOnClickListener(v -> {
            try {
                String message = msgEtx.getText().toString();
                WebSocketManager.getInstance().sendMessage(message);
                addMessageToChat(message);
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage());
            }
        });
    }

    private void addMessageToChat(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setBackgroundResource(R.drawable.chat_bubble);
        textView.setPadding(16, 16, 16, 16);
        View divider = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        params.setMargins(0, 8, 0, 8);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(Color.BLACK);
        messageContainer.addView(textView);
        messageContainer.addView(divider);
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> addMessageToChat(message));
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> addMessageToChat("Connection closed by " + closedBy + "\nReason: " + reason));
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}