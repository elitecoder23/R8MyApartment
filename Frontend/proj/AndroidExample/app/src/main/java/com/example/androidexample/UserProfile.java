package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfile extends AppCompatActivity {

    private TextView username;
    private TextView userChangeInput;
    private TextView userDetails; // TextView to display user details

    private static final String URL = "http://coms-3090-057.class.las.iastate.edu:8080/users/";
    private static final String URL2 = "http://coms-3090-057.class.las.iastate.edu:8080/users/search" + "?username=";
    private static final String URL3 = "http://coms-3090-057.class.las.iastate.edu:8080/users/";

    private Button editProfile;
    private Button deleteProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String usernameForDisplay = sharedPreferences.getString("username", "");

        username = findViewById(R.id.username);
        userDetails = findViewById(R.id.userDetails); // Initialize the TextView

        username.setText("Username: " + usernameForDisplay);

        editProfile = findViewById(R.id.edit);
        deleteProfile = findViewById(R.id.deleteProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                builder.setTitle("Edit Profile")
                        .setItems(new CharSequence[]{"Change Username", "Change Password"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int choice) {
                                switch (choice) {
                                    case 0:
                                        changeChoice("Change Username");
                                        makejsonOBJRequest("username");
                                        break;
                                    case 1:
                                        changeChoice("Change Password");
                                        makejsonOBJRequest("password");
                                        break;
                                }
                            }
                        });
                builder.create().show();
            }
        });
        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUserProfile();
            }
        });

        // Fetch user details when the activity is created
        fetchUserDetails();
    }

    private void changeChoice(String choice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle(choice);
        final EditText input = new EditText(UserProfile.this);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userChangeInput.setText(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void makejsonOBJRequest(String param) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put(param, userChangeInput);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                    }
                }) {
            @Override
            public byte[] getBody() {
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put(param, userChangeInput);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(jsonObjectRequest);
    }

    private void fetchUserDetails() {
        RequestQueue queue = Volley.newRequestQueue(this);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String usernameForGet = sharedPreferences.getString("username", "");
        String url = URL2  + usernameForGet;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Assuming the response contains user details
                            String userName = response.getString("username");
                            String userEmail = response.getString("email");
                            String firstName = response.getString("firstName");
                            String lastName = response.getString("lastName");
                            String birthDate = response.getString("birthDate");
                            // Display user details
                            userDetails.setText("Username: " + userName + "\nfirstName: " + firstName + "\nlastName: " + lastName + "\nEmail: " + userEmail + "\nDOB: " + birthDate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Toast.makeText(UserProfile.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }
    private void deleteUserProfile() {
        RequestQueue queue = Volley.newRequestQueue(this);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String usernameForDelete = sharedPreferences.getString("username", "");
        String url = URL3 + "?username=" + usernameForDelete;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Delete Response", response.toString());
                        Toast.makeText(UserProfile.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                        // Optionally, redirect to another activity
                        Intent intent = new Intent(UserProfile.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserProfile.this, "Failed to delete profile", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }
}
