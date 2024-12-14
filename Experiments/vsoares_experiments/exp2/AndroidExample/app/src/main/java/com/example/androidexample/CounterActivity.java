package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;

import org.apache.commons.lang3.time.StopWatch;

public class CounterActivity extends AppCompatActivity {

    private TextView numberTxt; // define number textview variable
    private Button startBtn; // define increase button variable
    private Button stopBtn;
    private Button backBtn;     // define back button variable
    private Button resetBtn;

    private int counter = 0;    // counter variable
    private StopWatch stopWatch = new StopWatch();
    private Runnable update;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        /* initialize UI elements */
        numberTxt = findViewById(R.id.number);
        startBtn = findViewById(R.id.timer_start_btn);
        stopBtn = findViewById(R.id.timer_stop_btn);
        backBtn = findViewById(R.id.counter_back_btn);
        resetBtn = findViewById(R.id.timer_reset_btn);
        update = new Runnable() {
            @Override
            public void run() {
                int time = (int) stopWatch.getTime();
                time = time/1000;
                numberTxt.setText(String.valueOf(time));
                handler.postDelayed(this, 50);
            }
        };

        /* when increase btn is pressed, counter++, reset number textview */
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWatch.start();
                handler.post(update);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWatch.stop();
                handler.removeCallbacks(update);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWatch.reset();
                numberTxt.setText("0");
                handler.removeCallbacks(update);
            }
        });



        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}