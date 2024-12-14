package com.example.androidexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsActivity extends AppCompatActivity {
    public static final String PendingFriendReqURL = "http://coms-3090-057.class.las.iastate.edu:8080/api/friend-requests/";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity);
        JsonReq();

    }


    public void showAllFriendRequests() {


    }

    public void JsonReq() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String usernameD = sharedPreferences.getString("username", "");
        String URL = "http://coms-3090-057.class.las.iastate.edu:8080/api/friend-requests/" + usernameD;
        Log.d("URL", URL);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("FriendReq", response.toString());
                        try {
                            LinearLayout container = findViewById(R.id.friend_request);
                            container.removeAllViews();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject friendReq = response.getJSONObject(i);
                                JSONObject receiver = friendReq.getJSONObject("receiver");
                                JSONObject sender = friendReq.getJSONObject("sender");
                                String senderName = sender.getString("firstName") + " " + sender.getString("lastName");
                                String senderUsername = sender.getString("username");
                                String receiverUsername = receiver.getString("username");

                                TextView senderNameView = new TextView(NotificationsActivity.this);
                                senderNameView.setText(senderName);
                                senderNameView.setPadding(16, 16, 16, 16);
                                senderNameView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

                                Button acceptButton = new Button(NotificationsActivity.this);
                                acceptButton.setText("Accept");
                                acceptButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                            handleFriendRequest(senderUsername,receiverUsername, true);
                                    }
                                });

                                Button denyButton = new Button(NotificationsActivity.this);
                                denyButton.setText("Deny");
                                denyButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        handleFriendRequest(senderUsername,receiverUsername, false);
                                    }
                                });

                                LinearLayout requestLayout = new LinearLayout(NotificationsActivity.this);
                                requestLayout.setOrientation(LinearLayout.HORIZONTAL);
                                requestLayout.addView(senderNameView);
                                requestLayout.addView(acceptButton);
                                requestLayout.addView(denyButton);

                                container.addView(requestLayout);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void handleFriendRequest(String senderusername,String receiverusername,  boolean accept) {
        String action = accept ? "accept" : "deny";
        String URL = "http://coms-3090-057.class.las.iastate.edu:8080/api/friend-requests/" + senderusername + "/" + receiverusername + "/"+ action;
        Log.d("FriendReqURL", URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("FriendReqResponse", response.toString());
                        JsonReq(); // Refresh the friend requests list
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
    }

