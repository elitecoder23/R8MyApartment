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


public class ApplyActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextEmploymentStatus, editTextMonthlyIncome, editTextMoveInDate, editTextPhoneNumber;
    private Button submitButton;
    private static final String TAG = "ApplyActivity";



    private static final String URL = "http://coms-3090-057.class.las.iastate.edu:8080/api/applications/submit/";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applyactivity);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        Log.d(TAG, "Username: " + username);

        String apartmentName = getIntent().getStringExtra("name");

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmploymentStatus = findViewById(R.id.editTextEmploymentStatus);
        editTextMonthlyIncome = findViewById(R.id.editTextMonthlyIncome);
        editTextMoveInDate = findViewById(R.id.editTextMoveInDate);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        submitButton = findViewById(R.id.buttonCreateListing);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendApplication(username, apartmentName);
            }
        });
    }
    private void sendApplication(String username, String apartmentName) {
        JSONObject applicationData = new JSONObject();
        try {
            applicationData.put("name", editTextName.getText().toString());
            applicationData.put("email", editTextEmail.getText().toString());
            applicationData.put("employmentStatus", editTextEmploymentStatus.getText().toString());
            applicationData.put("monthlyIncome", editTextMonthlyIncome.getText().toString());
            applicationData.put("moveInDate", editTextMoveInDate.getText().toString());
            applicationData.put("phoneNumber", editTextPhoneNumber.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String urlforsubmission = URL + apartmentName + "/" + username;
        Log.d(TAG, "Sending application to: " + urlforsubmission);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlforsubmission, applicationData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Application submitted successfully");
                Toast.makeText(ApplyActivity.this, "Application submitted successfully", Toast.LENGTH_SHORT).show();
                Intent returnHome = new Intent(ApplyActivity.this, HomePageActivity.class);
                startActivity(returnHome);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error submitting application: " + error.getMessage());
                Toast.makeText(ApplyActivity.this, "Error submitting application", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
