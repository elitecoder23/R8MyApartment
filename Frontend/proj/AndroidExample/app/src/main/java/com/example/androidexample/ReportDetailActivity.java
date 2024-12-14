package com.example.androidexample;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportDetailActivity extends AppCompatActivity {

    private Button DeleteListinBtn;
    private Button SetResolvedBtn;
    private String apartmentName;
    private int id;
    private static final String DeleteURL = "http://coms-3090-057.class.las.iastate.edu:8080/api/admin/apartments/";
    private static final String ResolveURL = "http://coms-3090-057.class.las.iastate.edu:8080/api/admin/reports/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_detail);

        TextView reportDetailsTextView = findViewById(R.id.report_detail_text);

        String reportDetails = getIntent().getStringExtra("reportDetails");
        displayReportDetails(reportDetails, reportDetailsTextView);

        DeleteListinBtn = findViewById(R.id.delete_button);
        SetResolvedBtn = findViewById(R.id.resolve_button);

        DeleteListinBtn.setOnClickListener(v -> deleteApartment());
        SetResolvedBtn.setOnClickListener(v -> resolveReport());
    }

    private void displayReportDetails(String reportDetails, TextView reportDetailsTextView) {
        try {
            JSONObject report = new JSONObject(reportDetails);
            id = report.getInt("id");
            String reporterUsername = report.getString("reporterUsername");
            String reason = report.getString("reason");
            apartmentName = report.getString("apartmentName");
            String status = report.getString("status");
            String reportDate = report.getString("reportDate");

            // Extract and clean the reason string
            reason = reason.substring(reason.indexOf("reason=") + 7, reason.indexOf("&reporterUsername"));
            reason = reason.replace("%2B", " ");

            reportDetailsTextView.setText(String.format("ID: %d\nReporter: %s\nReason: %s\nApartment: %s\nStatus: %s\nDate: %s",
                    id, reporterUsername, reason, apartmentName, status, reportDate));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteApartment() {
        String URL = DeleteURL + apartmentName;
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                URL,
                response -> {
                    setResult(RESULT_OK);
                    finish(); // Close the activity
                },
                error -> {
                    error.printStackTrace();
                }
        );
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void resolveReport() {
        String URL = ResolveURL + id + "/review?status=RESOLVED";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                URL,
                null,
                response -> {
                    setResult(RESULT_OK);
                    finish();
                },
                error -> {
                    error.printStackTrace();
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}