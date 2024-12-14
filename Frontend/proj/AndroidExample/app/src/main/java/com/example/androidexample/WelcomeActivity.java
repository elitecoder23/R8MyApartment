package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomepage);

        Button buttonSignup = findViewById(R.id.buttonSignup);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonLandlordLogin = findViewById(R.id.LandlordLogin);
        Button buttonAdminLogin = findViewById(R.id.AdminLogin);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        buttonLandlordLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent landlordLoginIntent = new Intent(WelcomeActivity.this, LandlordLoginActivity.class);
                startActivity(landlordLoginIntent);
            }
        });

        buttonAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminLoginIntent = new Intent(WelcomeActivity.this, AdminLoginActivity.class);
                startActivity(adminLoginIntent);
            }
        });
    }
}