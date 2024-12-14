package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity"; // Add this for logging
    private TextView messageText;
    private ImageView imageView;
    public static EditText loginuser;
    private EditText loginpass;
    private Button loginbtn;
    private static final String URL_JSON_OBJECT = "http://coms-3090-057.class.las.iastate.edu:8080/api/auth/signin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        messageText = findViewById(R.id.main_msg_txt);
        messageText.setText("Welcome to CyFinder");

        loginuser = findViewById(R.id.login_user);
        loginpass = findViewById(R.id.login_pass);
        loginbtn = findViewById(R.id.login_btn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = loginuser.getText().toString();
                String pass = loginpass.getText().toString();

                // Log the input values (remove in production)
                Log.d(TAG, "Attempting login with username: " + user);

                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("username", user);
                    loginData.put("password", pass);
                    Log.d(TAG, "Request JSON: " + loginData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    messageText.setText("Error creating JSONData: " + e.toString());
                    return;
                }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_JSON_OBJECT,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response","Raw server response: "+ response);

                                // First check if the response contains "successful"
                                if (response.contains("successful")) {
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    // Get the username from the EditText since it's not in the response
                                    String username = loginuser.getText().toString();
                                    String email = " ";
                                    messageText.setText("Welcome " + username);


                                    SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username", username);
                                    editor.putString("email", email);

                                    // Navigate to HomePage
                                    Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                    startActivity(intent);
                                    return;
                                }

                                // If it's not a success message, try parsing as JSON
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    if (jsonResponse.has("accessToken")) {
                                        String token = jsonResponse.getString("accessToken");
                                        String name = jsonResponse.getString("username");

                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        messageText.setText("Welcome " + name);

                                        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                        startActivity(intent);
                                    } else {
                                        messageText.setText("Login Failed: Invalid response format");
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                                    e.printStackTrace();
                                    messageText.setText("Login Failed: " + response);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Network error: " + error.toString());
                        error.printStackTrace();
                        messageText.setText("Login Failed, username or password incorrect");
                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        return loginData.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                Log.d(TAG, "Sending request to server...");
                Volley.newRequestQueue(LoginActivity.this).add(stringRequest);
            }
        });
    }
}