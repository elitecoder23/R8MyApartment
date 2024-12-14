package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminLoginActivity extends AppCompatActivity {

    private TextView adminLoginTextView;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private static final String URL_JSON_OBJECT = "http://coms-3090-057.class.las.iastate.edu:8080/api/admin/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        buttonLogin = findViewById(R.id.login_button);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.passwd);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String pass = editTextPassword.getText().toString();


                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("email", email);
                    loginData.put("password", pass);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("PUT", loginData.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, URL_JSON_OBJECT, loginData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("Response", response.toString());
                                    JSONObject data = response.getJSONObject("data");
                                    String token = data.getString("token");
                                    String name = data.getString("name");
                                    String email = data.getString("email");
                                    Log.d("AdminLogin", "Login successful");
                                    Intent intent = new Intent(AdminLoginActivity.this, AdminDashboard.class);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String errorMessage = "Login failed";
                                Log.d("ErrorLogin", "server error"+ error.toString());
                                if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                                    errorMessage = "Invalid email or password";
                                    Log.d("ErrorLogin", "Invalid email or password");
                                }
                                Toast.makeText(AdminLoginActivity.this,
                                        errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });

                Volley.newRequestQueue(AdminLoginActivity.this).add(jsonObjectRequest);
            }
        });


    }



}
