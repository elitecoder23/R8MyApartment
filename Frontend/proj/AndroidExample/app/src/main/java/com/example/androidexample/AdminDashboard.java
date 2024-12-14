package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminDashboard extends AppCompatActivity {

    private static final String URLReports = "http://coms-3090-057.class.las.iastate.edu:8080/api/admin/reports/pending";
    private LinearLayout reportContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        reportContainer = findViewById(R.id.report_container);
        fetchReports();
    }

    private void fetchReports() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URLReports,
                null,
                this::handleResponse,
                this::handleError
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void handleResponse(JSONObject response) {
        Log.d("Reports", response.toString());
        try {
            JSONArray reports = response.getJSONObject("data").getJSONArray("content");
            Log.d("reports2", reports.toString());
            reportContainer.removeAllViews();
            for (int i = 0; i < reports.length(); i++) {
                JSONObject report = reports.getJSONObject(i);
                addReportView(report);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleError(VolleyError error) {
        Log.e("Volley Error", error.toString());
    }

    private void addReportView(JSONObject report) throws JSONException {
        int id = report.getInt("id");
        String reporterUsername = report.getString("reporterUsername");
        String reason = report.getString("reason");
        String apartmentName = report.getString("apartmentName");
        String status = report.getString("status");
        String reportDate = report.getString("reportDate");

        // Extract and clean the reason string
        reason = reason.substring(reason.indexOf("reason=") + 7, reason.indexOf("&reporterUsername"));
        reason = reason.replace("%2B", " ");

        TextView reportView = new TextView(this);
        reportView.setText(String.format("ID: %d\nReporter: %s\nReason: %s\nApartment: %s\nStatus: %s\nDate: %s",
                id, reporterUsername, reason, apartmentName, status, reportDate));
        reportView.setPadding(16, 16, 16, 16);
        reportView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

        reportView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportDetailActivity.class);
            intent.putExtra("reportDetails", report.toString());
            startActivityForResult(intent, 1);
        });

        reportContainer.addView(reportView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchReports();
        }
    }
}