package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {
    private Button sendBtn;
    private EditText msgEtx;
    private TextView msgTv;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
    private List<String> messageList = new ArrayList<>();
    private Date sessionStartDate;
    private TextView readStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        /* initialize UI elements */
        sendBtn = (Button) findViewById(R.id.sendBtn);
        msgEtx = (EditText) findViewById(R.id.msgEdt);
        msgTv = (TextView) findViewById(R.id.tx1);
        readStatus = (TextView) findViewById(R.id.readStatus);

        /* connect this activity to the websocket instance */
        WebSocketManager.getInstance().setWebSocketListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String usernameWeb = sharedPreferences.getString("username", "");
        String serverUrl = "ws://coms-3090-057.class.las.iastate.edu:8080/chat/" + usernameWeb;
        WebSocketManager.getInstance().connectWebSocket(serverUrl);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            try {
                String message = msgEtx.getText().toString();
                if (!message.trim().isEmpty()) {
                    Log.d("ChatActivity", "Sending message: " + message);
                    WebSocketManager.getInstance().sendMessage(message);
                    msgEtx.setText(""); // Clear input field after sending
                }
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage());
            }
        });
        sessionStartDate = new Date();
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            try {
                JSONObject jsonMessage = new JSONObject(message);

                String sender = jsonMessage.getString("sender");
                String content = jsonMessage.getString("content");
                String timestamp = jsonMessage.getString("timestamp");
                String type = jsonMessage.getString("type");
                String messageId = jsonMessage.getString("messageId");
                String readBy = jsonMessage.getString("readBy");



                if (type.equals("READ_RECEIPT")) {
                    readStatus.setText("Read by: " + readBy);
                    return;
                }

                if (sender == null || content == null || timestamp == null) {
                    Log.d("ChatActivity", "Invalid message received, ignoring.");
                    return;
                }


                // Format the timestamp to just show time in AM/PM
//                try {
//                    Date date = inputFormat.parse(timestamp);
//                    timestamp = outputFormat.format(date);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

                Date messageDate = inputFormat.parse(timestamp);
                if (messageDate.before(sessionStartDate)) {
                    return;
                }

                timestamp = outputFormat.format(messageDate);

                // Create a formatted message with proper spacing and styling
                StringBuilder formattedMessage = new StringBuilder();
                formattedMessage.append(sender).append("      ").append(timestamp).append("\n");
                formattedMessage.append(content).append("\n\n");

                messageList.add(formattedMessage.toString());

                StringBuilder allMessages = new StringBuilder();
                for (String msg : messageList) {
                    allMessages.append(msg);
                }

                msgTv.setText(allMessages.toString());



                String sentReceipt = new String();
                sentReceipt = "READ:" + messageId;
                WebSocketManager.getInstance().sendMessage(sentReceipt);

//                msgTv.setText(formattedMessage.toString());

//                String currentText = msgTv.getText().toString();
//                msgTv.setText(currentText + formattedMessage.toString());
//
//                // Auto-scroll to the bottom
//                int scrollAmount = msgTv.getLayout().getLineTop(msgTv.getLineCount()) - msgTv.getHeight();
//                if (scrollAmount > 0) {
//                    msgTv.scrollTo(0, scrollAmount);
//                }

                Log.d("ChatActivity", "onWebSocketMessage: Displayed message: " + formattedMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "\nConnection closed by " + closedBy + "\n" + reason);
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("ChatActivity", "onWebSocketOpen: Connection opened");
        runOnUiThread(() -> {
            msgTv.setText("Welcome to the chat room\n\n");
        });
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("ChatActivity", "WebSocket Error: " + ex.getMessage());
    }
}