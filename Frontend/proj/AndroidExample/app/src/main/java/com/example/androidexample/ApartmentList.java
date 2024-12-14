package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApartmentList extends AppCompatActivity {
    public static final String URL = "http://coms-3090-057.class.las.iastate.edu:8080/api/apartments/showall";
    private LinearLayout apartmentContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apartmentlist);

        apartmentContainer = findViewById(R.id.apartment_container);
        fetchApartments();
    }

    private void fetchApartments() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            apartmentContainer.removeAllViews();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject apartment = response.getJSONObject(i);
                                String name = apartment.getString("name");
                                double price = apartment.getDouble("price");
                                String address = apartment.getString("address");
                                String amenities = apartment.getString("amenities");
                                String contactInfo = String.format("Phone: %s\nEmail: %s",
                                        apartment.getString("contactPhone"), apartment.getString("contactEmail"));

                                // Create a new TextView for each apartment
                                TextView apartmentView = new TextView(ApartmentList.this);
                                apartmentView.setText(name + "\n" + address + "\n" + String.format("$%.2f", price));
                                apartmentView.setPadding(16, 16, 16, 16);
                                apartmentView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                                apartmentView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showApartmentProfile(apartment);
                                    }
                                });

                                // Add the TextView to the container
                                apartmentContainer.addView(apartmentView);
                            }
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

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void showApartmentProfile(JSONObject apartment) {
        try {
            Intent intent = new Intent(ApartmentList.this, ApartmentDetailActivity.class);
            intent.putExtra("name", apartment.getString("name"));
            intent.putExtra("price", apartment.getDouble("price"));
            intent.putExtra("address", apartment.getString("address"));
            intent.putExtra("amenities", apartment.getString("amenities"));
            intent.putExtra("contact", String.format("Phone: %s\nEmail: %s",
                    apartment.getString("contactPhone"), apartment.getString("contactEmail")));
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    }
