package com.example.androidexample;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketService {
    private WebSocket webSocket;
    private static final String WS_URL = "ws://coms-3090-057.class.las.iastate.edu:8080/ws/NewReq/username/";
    private static final String WS_URLDC = "ws://coms-3090-057.class.las.iastate.edu:8080/chat/";
    private OkHttpClient client = new OkHttpClient();
    private Context context;
    private String username;


    public WebSocketService(Context context) {
        this.context = context;
    }


    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            WebSocketService.this.webSocket = webSocket;
            Log.d("Connection", "Connected to " + WS_URL);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("FriendReqReceived", "Received message: " + text);
            new Handler(Looper.getMainLooper()).post(() -> showSimpleDialog(text));
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            Log.e("Error happened", "Error occurred", t);
        }
    };

    public void establishConnection(String friendUsername) {
        Request request = new Request.Builder().url(WS_URL + friendUsername).build();
        username = friendUsername;
        client.newWebSocket(request, webSocketListener);
        client.dispatcher().executorService().shutdown(); // Not strictly necessary here but good practice
    }

    public void establishConnectionChat(String usernameToConnect) {
        Request request = new Request.Builder().url(WS_URLDC + usernameToConnect).build();
        client.newWebSocket(request, webSocketListener);
        client.dispatcher().executorService().shutdown();
    }

    public void sendNewReq(String sender, String friendUsername) {
        webSocket.send(sender + "," + friendUsername+ ",pending");
        Log.d("FriendReqSent", "Sent message: " + sender + "," + friendUsername+ ",pending");
    }

    public void closeConnection() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing Connection");
            webSocket = null;
        }
    }


    private void showSimpleDialog(String str) {
        String target = "";
        Pattern pattern = Pattern.compile("from\\s+(\\w+)");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            target = matcher.group(1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String finalTarget = target;
        builder.setMessage(str)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        webSocket.send(finalTarget +","+username+",accept");
                        Log.d("FriendReqAccepted", "Sent message: " + finalTarget +","+username+",accept");

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        webSocket.send(finalTarget +","+username+",deny");
                        Log.d("FriendReqDeclined", "Sent message: " + finalTarget +","+username+",deny");

                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}