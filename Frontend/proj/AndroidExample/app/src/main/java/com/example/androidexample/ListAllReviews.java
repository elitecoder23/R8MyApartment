package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListAllReviews extends AppCompatActivity {

    private RequestQueue requestQueue;

    private static final String URL_JSON_OBJECT = "http://coms-3090-057.class.las.iastate.edu:8080/api/reviews/getReviewsByUser/";
    private static final String URL_DELETE_REVIEW = "http://coms-3090-057.class.las.iastate.edu:8080/api/reviews/deleteReview";
    private static final String URL_EDIT_REVIEW = "http://coms-3090-057.class.las.iastate.edu:8080/api/reviews/editReview";
    private static final String TAG = "ListAllReviews";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listallreviews);

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(this);
        // Call the method to make the request without needing to press a button, they just show up right when the list reviews button is pressed
        makeJsonObjReq();
    }

    /**
     * Making json object request
     */
    private void makeJsonObjReq() {


        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        Log.d(TAG, "Username: " + username);

        String url = URL_JSON_OBJECT + username;
        Log.d(TAG, "URL: " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            updateReviewsUI(response);
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

        // Adding request to request queue
        requestQueue.add(jsonArrayReq);
    }

    /**
     * Method to delete a review
     */
    private void deleteReview(String reviewId) {
        String url = URL_DELETE_REVIEW + "?reviewId=" + reviewId;
        Log.d("Delete Review", "URL: " + url); // Log the URL

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response from the server

                        Toast.makeText(ListAllReviews.this, response, Toast.LENGTH_SHORT).show();
                        makeJsonObjReq(); // Refresh the reviews list
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        requestQueue.add(deleteRequest);
        Log.d("Delete Review", "Request added to queue"); // Log request addition
    }

    /**
     * Method to update the reviews UI
     */
    private void updateReviewsUI(JSONArray reviewsArray) throws JSONException {
        LinearLayout reviewsContainer = findViewById(R.id.reviewsContainer);
        reviewsContainer.removeAllViews(); // Clear previous views

        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject review = reviewsArray.getJSONObject(i);
            JSONObject apartment = review.getJSONObject("apartment");
            String apartmentName = apartment.getString("name");
            String reviewId = review.getString("id");
            String comment = review.getString("comment");
            String reviewText = review.getString("reviewText");
            String rating = review.getString("rating");

            // Create a TextView for the review
            TextView reviewView = new TextView(ListAllReviews.this);
            reviewView.setText("Review ID: " + reviewId + "\nApartment Name: " + apartmentName + "\nComment: " + comment + "\nReview: " + reviewText + "\nRating: " + rating);
            reviewView.setPadding(16, 16, 16, 16);

            // Create a Button for deleting the review
            Button deleteButton = new Button(ListAllReviews.this);
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteReview(reviewId);
                }
            });

            // Create a Button for editing the review
            Button editButton = new Button(ListAllReviews.this);
            editButton.setText("Edit");
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(reviewId);
                }
            });

            // Add the TextView and Button to the container
            reviewsContainer.addView(reviewView);
            reviewsContainer.addView(deleteButton);
            reviewsContainer.addView(editButton);
        }
    }

    /**
     * Method to show the edit dialog
     */
    private void showEditDialog(String reviewId) {
        // Create a dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_review);
        dialog.setTitle("Edit Review");

        // Get the input fields and button from the dialog layout
        EditText editTextNewComment = dialog.findViewById(R.id.editTextNewComment);
        EditText editTextNewReview = dialog.findViewById(R.id.editTextNewReview);
        EditText editTextNewRating = dialog.findViewById(R.id.editTextNewRating);
        Button buttonSubmitEdit = dialog.findViewById(R.id.buttonSubmitEdit);

        // Set the button click listener
        buttonSubmitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newComment = editTextNewComment.getText().toString();
                String newReviewText = editTextNewReview.getText().toString();
                String newRating = editTextNewRating.getText().toString();

                if (newReviewText.isEmpty() || newRating.isEmpty()) {
                    Toast.makeText(ListAllReviews.this, "Please enter both review and rating", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call the editReview method with the new values
                editReview(reviewId, newComment, newReviewText, newRating);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Method to edit a review
     */
    private void editReview(String reviewId, String newComment, String newReviewText, String newRating) {
        String url = URL_EDIT_REVIEW + "?reviewId=" + reviewId;
        Log.d("Edit Review", "URL: " + url); // Log the URL

        JSONObject reviewJson = new JSONObject();
        try {
            reviewJson.put("comment", newComment);
            reviewJson.put("reviewText", newReviewText);
            reviewJson.put("rating", newRating);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest editRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                reviewJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response from the server
                        Log.d("Edit Response", response.toString());
                            makeJsonObjReq();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        requestQueue.add(editRequest);
        Log.d("Edit Review", "Request added to queue"); // Log request addition
    }
}