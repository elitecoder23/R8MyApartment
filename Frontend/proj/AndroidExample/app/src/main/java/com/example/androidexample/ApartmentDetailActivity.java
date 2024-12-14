package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ApartmentDetailActivity extends AppCompatActivity {

    private Button Report;
    private Button ApplyButton;
    private Button goToReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment_detail);

        TextView nameTextView = findViewById(R.id.apartment_detail_name);
        TextView priceTextView = findViewById(R.id.apartment_detail_price);
        TextView addressTextView = findViewById(R.id.apartment_detail_address);
        TextView amenitiesTextView = findViewById(R.id.apartment_detail_amenities);
        TextView contactTextView = findViewById(R.id.apartment_detail_contact);

        String name = getIntent().getStringExtra("name");
        double price = getIntent().getDoubleExtra("price", 0);
        String address = getIntent().getStringExtra("address");
        String amenities = getIntent().getStringExtra("amenities");
        String contact = getIntent().getStringExtra("contact");

        getAverageRating(name);

        nameTextView.setText(name);
        priceTextView.setText(String.format("$%.2f", price));
        addressTextView.setText(address);
        amenitiesTextView.setText(amenities);
        contactTextView.setText(contact);



        ApplyButton = findViewById(R.id.apply_button);
        Report = findViewById(R.id.report_button);
        goToReviews =  findViewById(R.id.view_reviews_button);

        Report.setOnClickListener(v -> {
             Intent intent = new Intent(this, ReportActivity.class);
             intent.putExtra("apartmentName", name);
             startActivity(intent);
        });


        ApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent applyIntent = new Intent(ApartmentDetailActivity.this, ApplyActivity.class);
                applyIntent.putExtra("name", name);
                startActivity(applyIntent);
            }
        });

        goToReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviewsIntent = new Intent(ApartmentDetailActivity.this, AptReviewsActivity.class);
                reviewsIntent.putExtra("title", name);
                startActivity(reviewsIntent);
            }
        });

    }

    private void getAverageRating(String apartmentName) {
        String url = "http://coms-3090-057.class.las.iastate.edu:8080/api/reviews/getAverageRating?apartmentName=" + apartmentName;
        Log.d("ApartmentDetailActivity", "URL for average rating: " + url);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ApartmentDetailActivity", "Response: " + response.toString());
                        try {
                            double averageRating = Double.parseDouble(response);
                            TextView ratingTextView = findViewById(R.id.apartment_detail_rating);
                            ratingTextView.setText("Average Rating: " + averageRating);
                        } catch (NumberFormatException e) {
                            Log.e("Parsing Error", "Error parsing response: " + e.getMessage());
                            showError("Error parsing response");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Network Error";
                        if (error.networkResponse != null) {
                            errorMessage = String.format("Error: %d - %s",
                                    error.networkResponse.statusCode,
                                    new String(error.networkResponse.data));
                        }
                        Log.e("Volley Error", errorMessage);
                        showError(errorMessage);
                    }
                }
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}