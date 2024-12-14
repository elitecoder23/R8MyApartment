package com.example.androidexample;
import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;



public class AptReviewsActivity extends AppCompatActivity{
    private LinearLayout reviewsContainer;
    private TextView textViewApartmentName;
    private static final String TAG = "AptReviewsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apartment_reviews);

        textViewApartmentName = findViewById(R.id.apartmentName);
        reviewsContainer = findViewById(R.id.reviewsContainer);

        String apartmentName = getIntent().getStringExtra("title");
        Log.d(TAG, "Apartment name: " + apartmentName);

        textViewApartmentName.setText(apartmentName);

        fetchReviews(apartmentName);
    }

    private void fetchReviews(String apartmentName) {
        String url = "http://coms-3090-057.class.las.iastate.edu:8080/api/reviews/getReviewsByApartmentName?apartmentName=" + apartmentName;
        Log.d(TAG, "URL: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Response: " + response.toString());
                        populateReviews(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);

    }

    private void populateReviews(JSONArray reviews) {
        reviewsContainer.removeAllViews();

        if(reviews.length() == 0){
            TextView noReviews = new TextView(this);
            noReviews.setText("No reviews found for this apartment");
            reviewsContainer.addView(noReviews);
        }

        for (int i = 0; i < reviews.length(); i++) {
            try {
                JSONObject review = reviews.getJSONObject(i);

                View reviewView = getLayoutInflater().inflate(R.layout.review_item, null);
                TextView authorView = reviewView.findViewById(R.id.reviewAuthor);
                TextView reviewsView = reviewView.findViewById(R.id.reviewDetails);

                String author = review.getString("username");
                String apartmentName = review.getString("apartmentName");
                String reviewSubject = review.getString("comment");
                String reviewText = review.getString("reviewText");
                String rating = review.getString("rating");

                authorView.setText(author);
                String details = String.format("Apartment Name: %s\nReview Subject: %s\nReview: %s\nRating: %s", apartmentName, reviewSubject, reviewText, rating);
                reviewsView.setText(details);

                reviewsContainer.addView(reviewView);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    /*
    private void reportReview(String reviewId) {

    }

     */

}
