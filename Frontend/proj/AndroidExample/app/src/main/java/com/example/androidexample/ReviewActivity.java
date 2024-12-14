package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviewpage);

        Button buttonCreateReview = findViewById(R.id.buttonCreateReview);
        Button buttonListReviews = findViewById(R.id.buttonListReviews);

        buttonCreateReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, CreateReviewActivity.class);
                startActivity(intent);
            }
        });

        buttonListReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, ListAllReviews.class);
                startActivity(intent);
            }
        });
    }
}