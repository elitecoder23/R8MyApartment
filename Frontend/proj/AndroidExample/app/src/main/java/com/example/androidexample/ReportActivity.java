package com.example.androidexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    private TextView reportTextView;
    private String URL = "http://coms-3090-057.class.las.iastate.edu:8080/api/listing-reports/submit/";
    private String apartmentName;
    private EditText reportEditText;
    private Button sendReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        reportTextView = findViewById(R.id.report_title);
        reportEditText = findViewById(R.id.report_edit_text);
        sendReportButton = findViewById(R.id.send_report_button);
        apartmentName = getIntent().getStringExtra("apartmentName");

        reportTextView.setText("Reason for reporting " + apartmentName);

        sendReportButton.setOnClickListener(v -> {
            String report = reportEditText.getText().toString();
            makeReport(report, apartmentName);
        });
    }

    public void makeReport(String report, String apartmentName) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");

        String sendURL = URL + apartmentName;
        Log.d("URL", sendURL);

        // Create a string request to send the report text
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                sendURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        reportTextView.setText("Report submitted successfully");
                        Log.d("ReportResponse", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        reportTextView.setText("Failed to submit report");
                        Log.e("Report Error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("reason", URLEncoder.encode(report, "UTF-8"));
                    params.put("reporterUsername", URLEncoder.encode(username, "UTF-8"));
                    params.put("reporterEmail", URLEncoder.encode(email, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}