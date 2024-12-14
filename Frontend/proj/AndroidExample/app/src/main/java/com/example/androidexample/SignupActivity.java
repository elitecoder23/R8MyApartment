package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This activity provides a user interface for new users to create an account by entering their
 * personal information including name, email, phone number, and password.
 * @author dcreadon
 */

public class SignupActivity extends AppCompatActivity {



    private String signupSuccessMessage;


    /** EditText field for user's first name input */
    private EditText editTextFirstName;

    /** EditText field for user's last name input */
    private EditText editTextLastName;

    /** EditText field for user's email address input */
    private EditText editTextEmail;

    /** EditText field for user's birth date input */
    private EditText editTextBirthDate;

    /** EditText field for user's phone number input */
    private EditText editTextPhoneNumber;

    /** EditText field for user's password input */
    private EditText editTextPassword;

    /** EditText field for password confirmation input */
    private EditText editTextConfirmPassword;

    /** EditText field for username input */
    private EditText editTextUsername;

    /** RequestQueue for handling network requests */
    private RequestQueue requestQueue;

    /** URL endpoint for user signup */
    private static final String SIGNUP_URL = "http://coms-3090-057.class.las.iastate.edu:8080/users/signup";


    /**
     * This method initializes the activity and sets up the UI elements
     * this also sets the clickable behavior for the sign up button and the login link
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signuppage);

        // Initialize UI elements
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextBirthDate = findViewById(R.id.editTextBirthDate);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        TextView alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        TextView textViewLoginLink = findViewById(R.id.textViewLoginLink);

        requestQueue = Volley.newRequestQueue(this);


        // setDefaultValues();

        /**
         * when pressed this button will sign the user up and direct them to back to the welcome
         * page
         */
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextPhoneNumber.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                if (phoneNumber.length() != 10) {
                    Toast.makeText(SignupActivity.this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject userJson = createUserJson();
                if (userJson != null) {
                    sendSignupRequest(userJson);
                }
            }
        });

        /**
         * clickable text that allows the user to go to the login page if they already have an account
         */
        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

//    public void setDefaultValues(){
//        editTextFirstName.setText("System");
//        editTextLastName.setText("Test");
//        editTextUsername.setText("sysTest");
//        editTextEmail.setText("sysTest@gmail.com");
//        editTextBirthDate.setText("01/01/2000");
//        editTextPhoneNumber.setText("1234567890");
//        editTextPassword.setText("password");
//        editTextConfirmPassword.setText("password");
//    }


    /**
     * creates a json object containing the user's information they just entered
     * @return
     */
    private JSONObject createUserJson() {
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("username", editTextUsername.getText().toString());
            userJson.put("firstName", editTextFirstName.getText().toString());
            userJson.put("lastName", editTextLastName.getText().toString());
            userJson.put("email", editTextEmail.getText().toString());
            userJson.put("birthDate", editTextBirthDate.getText().toString());
            userJson.put("phoneNumber", editTextPhoneNumber.getText().toString());
            userJson.put("password", editTextPassword.getText().toString());
            return userJson;
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating user", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    /**
     * sends the sign up request to the server, uses volley along with a POST request to do this.
     * @param userJson
     */
    private void sendSignupRequest(JSONObject userJson) {
        Log.d("SignupActivity", "Sending user JSON: " + userJson.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SIGNUP_URL, userJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleServerResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleServerError(error);
                    }
                });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }


    /**
     * Handles the response of the server if the user was created successfully
     * @param response
     */
    private void handleServerResponse(JSONObject response) {
        String displayText = "Server Response:\n" + response.toString();
        Log.d("SignupActivity", "Server Response: " + response.toString());
        signupSuccessMessage = "User created";
        Toast.makeText(this, "User created", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Signup request sent successfully", Toast.LENGTH_SHORT).show();

        Intent returnToWelcome = new Intent(SignupActivity.this, WelcomeActivity.class);
        startActivity(returnToWelcome);
    }

    /**
     * this handles any errors that could potentially occur and displays them to the user
     * @param error
     */
    private void handleServerError(VolleyError error) {
        String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error occurred");
        Log.e("SignupActivity", errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}