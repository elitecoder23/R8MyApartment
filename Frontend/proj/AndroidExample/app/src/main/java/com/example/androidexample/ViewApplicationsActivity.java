
package com.example.androidexample;

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



/**
 * Class that makes the view applications page for the landlord to be able to see all of the applications that have been submitted for a property
 * the top of the screen will say pending applications for the specific property
 * then the name of each applicant and their current application status will be shown with a button that says view application, once that button is pressed a dialog will show up with the information of the application
 * at the end of the application there is a button that says accept or reject, once either button is pressed a put request will update the status of the application
 */
public class ViewApplicationsActivity extends AppCompatActivity {

    private LinearLayout applicationsContainer;
    private TextView textViewApartmentName;
    private static final String TAG = "ViewApplicationsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewapplications);

        textViewApartmentName = findViewById(R.id.apartmentName);
        applicationsContainer = findViewById(R.id.applicationsContainer);

        String apartmentName = getIntent().getStringExtra("title");
        Log.d(TAG, "Apartment name: " + apartmentName);

        textViewApartmentName.setText(apartmentName);

        fetchApplications(apartmentName);
    }
    //test

    private void fetchApplications(String apartmentName) {
        String url = "http://coms-3090-057.class.las.iastate.edu:8080/api/applications/apartment/" + apartmentName;
        Log.d(TAG, "GET request to: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "GET response: " + response.toString());
                            if (response.getBoolean("success")) {
                                JSONArray applications = response.getJSONArray("data");
                                populateApplications(applications);
                            } else {
                                String message = response.getString("message");
                                showError("Error: " + message);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
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
                        Log.e(TAG, errorMessage);
                        showError(errorMessage);
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }


    private void populateApplications(JSONArray applications) {
        applicationsContainer.removeAllViews(); // Clear any existing views

        if (applications.length() == 0) {
            TextView noApplicationsView = new TextView(this);
            noApplicationsView.setText("No applications found");
            noApplicationsView.setTextSize(18);
            noApplicationsView.setPadding(16, 16, 16, 16);
            applicationsContainer.addView(noApplicationsView);
            return;
        }

        for (int i = 0; i < applications.length(); i++) {
            try {
                JSONObject application = applications.getJSONObject(i);

                View applicationView = getLayoutInflater().inflate(R.layout.application_item, null);
                TextView nameView = applicationView.findViewById(R.id.applicantName);
                TextView detailsView = applicationView.findViewById(R.id.applicationDetails);
                Button acceptButton = applicationView.findViewById(R.id.acceptButton);
                Button rejectButton = applicationView.findViewById(R.id.rejectButton);
                Button viewComments = applicationView.findViewById(R.id.viewComments);


                // Get application details
                String name = application.getString("name");
                String status = application.getString("status");
                String email = application.getString("email");
                String employmentStatus = application.getString("employmentStatus");
                String monthlyIncome = application.getString("monthlyIncome");
                String moveInDate = application.getString("moveInDate");
                String phoneNumber = application.getString("phoneNumber");
                String applicationDate = application.getString("applicationDate");

                nameView.setText(name);
                String details = String.format("Status: %s\nEmail: %s\nPhone: %s\n" +
                                "Employment: %s\nMonthly Income: $%s\n" +
                                "Desired Move-in Date: %s\n" + "Submitted On %s",
                        status, email, phoneNumber, employmentStatus,
                        monthlyIncome, moveInDate, applicationDate);
                detailsView.setText(details);

                acceptButton.setOnClickListener(v -> updateApplicationStatus(application, "ACCEPTED"));
                rejectButton.setOnClickListener(v -> updateApplicationStatus(application, "REJECTED"));
                viewComments.setOnClickListener(v -> fetchLandlordComments(name));

                applicationsContainer.addView(applicationView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateApplicationStatus(JSONObject application, String newStatus) {
        try {
            JSONObject user = application.getJSONObject("user");
            String username = user.getString("username");
            Log.d(TAG, "Updating application status for: " + username);

            JSONObject apartment = application.getJSONObject("apartment");
            String apartmentName = apartment.getString("name");
            Log.d(TAG, "Apartment Name" + apartmentName);

            String url = "http://coms-3090-057.class.las.iastate.edu:8080/api/applications/" + apartmentName + "/" + username + "/status?status=" + newStatus;
            Log.d(TAG, "PUT request to: " + url);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    Toast.makeText(ViewApplicationsActivity.this, "Application status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                                    fetchApplications(getIntent().getStringExtra("title")); // Refresh the list
                                } else {
                                    showError("Failed to update status: " + response.getString("message"));
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON parsing error: " + e.getMessage());
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
                            Log.e(TAG, errorMessage);
                            showError(errorMessage);
                        }
                    }
            );

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void fetchLandlordComments(String tenantUsername) {
        String url = "http://coms-3090-057.class.las.iastate.edu:8080/api/userReview/user/" + tenantUsername;
        Log.d(TAG, "GET request to: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                            Log.d(TAG, "GET response: " + response.toString());
                                displayLandlordComments(response);
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
                        Log.e(TAG, errorMessage);
                        showError(errorMessage);
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void displayLandlordComments(JSONArray comments) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Previous Landlord Comments");

        StringBuilder commentsText = new StringBuilder();
        for (int i = 0; i < comments.length(); i++) {
            try {
                JSONObject comment = comments.getJSONObject(i);
                String reviewText = comment.getString("reviewText");
                Log.d(TAG, "Review: " + reviewText);

                commentsText.append("Comment: ").append(reviewText).append("\n\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        builder.setMessage(commentsText.toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
