package com.example.androidexample;

import androidx.test.espresso.idling.CountingIdlingResource;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.view.LayoutInflater;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LandlordLoginActivity extends AppCompatActivity {



    private TextView landlordLoginTextView;
    private static final String URL_JSON_OBJECT = "http://coms-3090-057.class.las.iastate.edu:8080/api/owners/login";
    private static final String URL_SIGN_UP = "http://coms-3090-057.class.las.iastate.edu:8080/api/owners/register";
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_TOKEN = "jwt_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landlordlogin);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        TextView editTextEmail = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        TextView textViewSignUpLink = findViewById(R.id.textViewSignUpLink);

        landlordLoginTextView = findViewById(R.id.landlordLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String pass = editTextPassword.getText().toString();

                // Create login request body
                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("email", email);
                    loginData.put("password", pass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, URL_JSON_OBJECT, loginData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // Extract token from response
                                    String token = response.getString("token");
                                    String name = response.getString("name");

                                    // Save token to SharedPreferences
                                    SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("owner_name", name);
                                    editor.putString(KEY_TOKEN, token);
                                    editor.apply();

                                    // Show success message
                                    Toast.makeText(LandlordLoginActivity.this,
                                            "Welcome " + name, Toast.LENGTH_SHORT).show();

                                    // Navigate to next screen
                                    Intent intent = new Intent(LandlordLoginActivity.this, CreateListingActivity.class);
                                    startActivity(intent);
                                    finish(); // Close login activity

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(LandlordLoginActivity.this,
                                            "Login failed: Invalid response", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String errorMessage = "Login failed";
                                if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                                    errorMessage = "Invalid email or password";
                                }
                                Toast.makeText(LandlordLoginActivity.this,
                                        errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });

                Volley.newRequestQueue(LandlordLoginActivity.this).add(jsonObjectRequest);
            }
        });

        textViewSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });
    }

    private void showSignUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_sign_up, null);
        builder.setView(dialogView);

        EditText editTextSignUpUsername = dialogView.findViewById(R.id.editTextSignUpUsername);
        EditText editTextSignUpPassword = dialogView.findViewById(R.id.editTextSignUpPassword);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        Button buttonSignUp = dialogView.findViewById(R.id.buttonSignUp);

        AlertDialog dialog = builder.create();

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextSignUpUsername.getText().toString();
                String password = editTextSignUpPassword.getText().toString();
                String email = editTextEmail.getText().toString();

                JSONObject signUpData = new JSONObject();
                try {
                    signUpData.put("email", email);
                    signUpData.put("name", username);
                    signUpData.put("password", password);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, URL_SIGN_UP, signUpData, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        // Extract token from response
                                        String token = response.getString("token");
                                        String name = response.getString("name");

                                        // Save token
                                        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString(KEY_TOKEN, token);
                                        editor.apply();

                                        Toast.makeText(LandlordLoginActivity.this,
                                                "Welcome " + name, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                        // Navigate to next screen
                                        Intent intent = new Intent(LandlordLoginActivity.this, CreateListingActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(LandlordLoginActivity.this,
                                                "Registration failed: Invalid response", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    String errorMessage = "Registration failed";
                                    if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                                        errorMessage = "Email already registered";
                                    }
                                    Toast.makeText(LandlordLoginActivity.this,
                                            errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });

                    Volley.newRequestQueue(LandlordLoginActivity.this).add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
    }
}