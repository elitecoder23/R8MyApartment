package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateReviewActivity extends AppCompatActivity {

    private EditText editTextReview;
    private EditText editTextRating;
    private EditText editTextApartmentName;
    private EditText editTextReviewSubject;
    private RequestQueue requestQueue;
    private static final String REVIEW_URL = "http://coms-3090-057.class.las.iastate.edu:8080/api/reviews/addReviewForUser/";
    private static final String TAG = "CreateReviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createreviewpage);

        editTextReview = findViewById(R.id.editTextReview);
        editTextRating = findViewById(R.id.editTextRating);
        editTextApartmentName = findViewById(R.id.editTextApartmentName);
        editTextReviewSubject = findViewById(R.id.editTextReviewSubject);
        Button buttonSubmitReview = findViewById(R.id.buttonSubmitReview);
        requestQueue = Volley.newRequestQueue(this);

        buttonSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = editTextReview.getText().toString();
                String rating = editTextRating.getText().toString();
                String apartmentName = editTextApartmentName.getText().toString();

                if (review.isEmpty() || rating.isEmpty() || apartmentName.isEmpty()) {
                    Toast.makeText(CreateReviewActivity.this, "Please enter both review and rating", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject reviewJson = createReviewJson();
                Log.d(TAG, "JSON Being Sent: " + reviewJson);
                if (reviewJson != null) {
                    sendReviewRequest(reviewJson);
                    Intent returnHome = new Intent(CreateReviewActivity.this, ReviewActivity.class);
                    startActivity(returnHome);
                }
            }
        });
    }

    private JSONObject createReviewJson() {
        JSONObject reviewJson = new JSONObject();
        JSONObject apartmentJson = new JSONObject();

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        Log.d(TAG, "Username: " + username);

        try {
            reviewJson.put("username", username);
            reviewJson.put("comment", editTextReviewSubject.getText().toString());
            reviewJson.put("reviewText", editTextReview.getText().toString());
            reviewJson.put("rating", editTextRating.getText().toString());

            apartmentJson.put("name", editTextApartmentName.getText().toString());
            reviewJson.put("apartment", apartmentJson);

            return reviewJson;
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating review", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void sendReviewRequest(JSONObject reviewJson) {
        Log.d("CreateReviewActivity", "Sending JSON: " + reviewJson.toString());
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        Log.d(TAG, "Username: " + username);

        String url = REVIEW_URL + username;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, reviewJson,
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

        requestQueue.add(jsonObjectRequest);
    }

    private void handleServerResponse(JSONObject response) {
        Log.d(TAG, "Response: " + response.toString());
        try {
            String reviewId = response.getString("id");
            Toast.makeText(this, "Review submitted with ID: " + reviewId, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleServerError(VolleyError error) {
        String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error occurred");
        Log.e("CreateReviewActivity", errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}