package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity that allows landlords to post listings on our app. These listings are the ones that populate the map on the home page.
 * The user can enter the name, price, address, amenities, phone number, and email of the listing.
 * The user can also view their listings, edit them, and delete them.
 * @author dcreadon
 */
public class CreateListingActivity extends AppCompatActivity {

    /** fields to allow the landlord to enter the name, price and address*/
    private EditText editTextName, editTextPrice, editTextAddress;
    /** fields to allow the landlord to enter the amenities, phone, and email*/
    private EditText editTextAmenities, editTextPhone, editTextEmail;
    /** button to create the listing*/
    private Button buttonCreateListing;
    /** button to view the listings*/
    private Button viewMyListings;

    private Button createReview;

    private static final String PREF_NAME = "LoginPrefs";
    /** variable that holds the URL */
    private static final String BASE_URL = "http://coms-3090-057.class.las.iastate.edu:8080/api/apartments";

    /**
     * This is called when the activity is first created. It creates the variables that are needed for this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createlisting);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextAmenities = findViewById(R.id.editTextAmenities);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonCreateListing = findViewById(R.id.buttonCreateListing);
        viewMyListings = findViewById(R.id.buttonViewListings);
        createReview = findViewById(R.id.createReview);

        buttonCreateListing.setOnClickListener(v -> createApartmentListing());

        createReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewDialog();
            }
        });

        viewMyListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListings();
            }
        });
    }

    /**
     * Creates a JSON object with the name, prive, address, amenities, phone number, and email of the listing.
     * Sends the JSON object to the server to create the listing using a POST request.
     */
    private void createApartmentListing() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }

            // Create JSON object for apartment data
            JSONObject apartmentData = new JSONObject();
            apartmentData.put("name", editTextName.getText().toString().trim());
            apartmentData.put("price", Double.parseDouble(editTextPrice.getText().toString().trim()));
            apartmentData.put("address", editTextAddress.getText().toString().trim());
            apartmentData.put("amenities", editTextAmenities.getText().toString().trim());
            apartmentData.put("contactPhone", editTextPhone.getText().toString().trim());
            apartmentData.put("contactEmail", editTextEmail.getText().toString().trim());

            // Get owner name from SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String ownerName = prefs.getString("owner_name", "");
            String token = prefs.getString("jwt_token", "");

            String createUrl = BASE_URL + "/create?owner=" + ownerName;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, createUrl, apartmentData,
                    response -> {
                        Toast.makeText(this, "Apartment listed successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateListingActivity.this, HomePageActivity.class);
                        startActivity(intent); // Return to previous screen
                    },
                    error -> {
                        String message = "Failed to create listing";
                        if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                            message = "Please check your input values";
                        }
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Validates the input fields for creating a new apartment listing.
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInputs() {
        if (editTextName.getText().toString().trim().isEmpty()) {
            showError("Please enter apartment name");
            return false;
        }
        if (editTextPrice.getText().toString().trim().isEmpty()) {
            showError("Please enter price");
            return false;
        }
        if (editTextAddress.getText().toString().trim().isEmpty()) {
            showError("Please enter address");
            return false;
        }
        if (editTextPhone.getText().toString().trim().isEmpty()) {
            showError("Please enter contact phone");
            return false;
        }
        if (editTextEmail.getText().toString().trim().isEmpty()) {
            showError("Please enter contact email");
            return false;
        }
        return true;
    }

    /**
     * Displays an error message as a Toast.
     * @param message The error message to display.
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows the user's apartment listings along with an edit and delete button in a dialog.
     * The user can click the edit button to edit the listing, or the delete button to delete the listing.
     * Uses a JSON Array to display all of the apartment listings
     * The user's listings are retrieved from the server using a GET request.
     * The user's listings are filtered by the owner's name.
     * The user's name is retrieved from SharedPreferences.
     */
    private void showListings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_listings, null);
        builder.setView(dialogView);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String ownerName = prefs.getString("owner_name", "");
        String token = prefs.getString("jwt_token", "");

        LinearLayout listingsContainer = dialogView.findViewById(R.id.listingsContainer);

        AlertDialog dialog = builder.create();
        dialog.show();

        String url = "http://coms-3090-057.class.las.iastate.edu:8080/api/apartments/owner/name/" + ownerName;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject listing = response.getJSONObject(i);
                                String title = listing.getString("name");
                                String address = listing.getString("address");
                                String rent = listing.getString("price");
                                String amenities = listing.getString("amenities");
                                String phoneNumber = listing.getString("contactPhone");
                                String email = listing.getString("contactEmail");

                                TextView listingView = new TextView(CreateListingActivity.this);
                                listingView.setText("Title: " + title + "\nAddress: " + address + "\nRent: " + rent +
                                        "\nAmenities: " + amenities + "\nPhone Number: " + phoneNumber +
                                        "\nEmail: " + email + "\n");

                                Button editButton = new Button(CreateListingActivity.this);
                                editButton.setText("Edit");
                                editButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showEditDialog(listing);
                                    }
                                });

                                Button deleteButton = new Button(CreateListingActivity.this);
                                deleteButton.setText("Delete");
                                deleteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteListing(title);
                                    }
                                });

                                Button viewApplications = new Button(CreateListingActivity.this);
                                viewApplications.setText("View Submitted Applications");
                                viewApplications.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        viewApplications(title);
                                    }
                                });


                                listingsContainer.addView(listingView);
                                listingsContainer.addView(editButton);
                                listingsContainer.addView(deleteButton);
                                listingsContainer.addView(viewApplications);

                                Log.d("currentListing", listing.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CreateListingActivity.this, "Failed to load listings", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(CreateListingActivity.this, "Failed to load listings", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    /**
     * Deletes an apartment listing by sending a DELETE request to the server.
     * @param title The title of the listing to delete.
     */
    private void deleteListing(String title) {
        String url = BASE_URL + "/" + title + "/delete";

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(this, "Listing deleted successfully!", Toast.LENGTH_SHORT).show();
                    showListings(); // Refresh the listings
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to delete listing", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * Shows a dialog to edit an existing apartment listing.
     * Allows a user to edit the name, price, address, amenities, phone number, and email of the listing.
     * @param listing The JSON object representing the listing to edit.
     */
    private void showEditDialog(JSONObject listing) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Listing");

        // Inflate the custom layout/view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_listing, null);
        builder.setView(dialogView);

        // Initialize the dialog views

        EditText editTextName = dialogView.findViewById(R.id.editTextTitle);

        EditText editTextPrice = dialogView.findViewById(R.id.editTextRent);

        EditText editTextAddress = dialogView.findViewById(R.id.editTextAddress);

        EditText editTextAmenities = dialogView.findViewById(R.id.editTextAmenities);

        EditText editTextPhone = dialogView.findViewById(R.id.editTextPhoneNumber);

        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);

        // Pre-fill the dialog views with current values
        try {
            editTextName.setText(listing.getString("name"));
            editTextPrice.setText(listing.getString("price"));
            editTextAddress.setText(listing.getString("address"));
            editTextAmenities.setText(listing.getString("amenities"));
            editTextPhone.setText(listing.getString("contactPhone"));
            editTextEmail.setText(listing.getString("contactEmail"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save the changes
                try {
                    listing.put("name", editTextName.getText().toString().trim());
                    listing.put("price", Double.parseDouble(editTextPrice.getText().toString().trim()));
                    listing.put("address", editTextAddress.getText().toString().trim());
                    listing.put("amenities", editTextAmenities.getText().toString().trim());
                    listing.put("contactPhone", editTextPhone.getText().toString().trim());
                    listing.put("contactEmail", editTextEmail.getText().toString().trim());

                    updateListing(listing);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Updates an existing apartment listing by sending a PUT request to the server.
     * @param listing The JSON object representing the listing to update.
     */
    private void updateListing(JSONObject listing) {
        String title;
        try {
            title = listing.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = BASE_URL + "/update/" + title;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, listing,
                response -> {
                    Toast.makeText(this, "Listing updated successfully!", Toast.LENGTH_SHORT).show();
                    showListings(); // Refresh the listings
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to update listing", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void viewApplications(String title){
        Intent intent = new Intent(CreateListingActivity.this, ViewApplicationsActivity.class);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private void showReviewDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_landlord_review, null);
        builder.setView(dialogView);

        EditText editTextTenantUsername = dialogView.findViewById(R.id.editTextTenantUsername);
        EditText editTextComment = dialogView.findViewById(R.id.editTextComment);


        builder.setTitle("Create Review")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tenantUsername = editTextTenantUsername.getText().toString().trim();
                        String comment = editTextComment.getText().toString().trim();

                        if (tenantUsername.isEmpty() || comment.isEmpty()) {
                            Toast.makeText(CreateListingActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        submitReview(tenantUsername, comment);
                    }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitReview(String tenantUsername, String comment) {
        try {
            JSONObject reviewData = new JSONObject();
            reviewData.put("userUsername", tenantUsername);
            reviewData.put("reviewText", comment);

            String url = "http://coms-3090-057.class.las.iastate.edu:8080/api/userReview";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, reviewData,
                    response -> {
                        Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                    }) {
            };
            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}