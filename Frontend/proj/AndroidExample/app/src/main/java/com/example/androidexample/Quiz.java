package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

/**
Thhis Class holds the Quiz page for the app
@author Vinicius Malaman Soares
 */
public class Quiz extends AppCompatActivity {

    public static final String URL = "http://coms-3090-057.class.las.iastate.edu:8080/api/roommate-matching/submit-quiz";
    public SeekBar ratingSeekBarnightorday;
    public TextView numberOfnightordayText;
    public TextView numberOfnightordayQuestion;

    public SeekBar hostingBar;
    public TextView hostingText;
    public TextView hostingQuestion;

    public SeekBar petBar;
    public TextView petText;
    public TextView petQuestion;

    public SeekBar smokingBar;
    public TextView smokingText;
    public TextView smokingQuestion;

    public SeekBar organizedBar;
    public TextView organizedText;
    public TextView organizedQuestion;

    public SeekBar guestsOverBar;
    public TextView guestsOverText;
    public TextView guestsOverQuestion;

    public SeekBar noiseBar;
    public TextView noiseText;
    public TextView noiseQuestion;


    public SeekBar cleanlinessBar;
    public TextView cleanlinessText;
    public TextView cleanlinessQuestion;

    public EditText desiredusername;

    public String username;
    public Button submit;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        ratingSeekBarnightorday = findViewById(R.id.ratingSeekBarnightorday);
        numberOfnightordayText = findViewById(R.id.numberOfnightordayText);


        hostingBar = findViewById(R.id.hostingBar);
        hostingText = findViewById(R.id.hostingText);
        hostingBar.setProgress(0);
        hostingQuestion = findViewById(R.id.hostingQuestion);

        petBar = findViewById(R.id.petBar);
        petText = findViewById(R.id.petText);
        petQuestion = findViewById(R.id.petQuestion);
        petBar.setProgress(0);

        smokingBar = findViewById(R.id.smokingBar);
        smokingText = findViewById(R.id.smokingText);
        smokingBar.setProgress(0);
        smokingQuestion = findViewById(R.id.smokingQuestion);

        organizedBar = findViewById(R.id.organizedBar);
        organizedText = findViewById(R.id.organizedText);
        organizedBar.setProgress(0);
        // organizedQuestion = findViewById(R.id.organizedQuestion);

        guestsOverBar = findViewById(R.id.guestsOverBar);
        guestsOverText = findViewById(R.id.guestsOverText);
        guestsOverBar.setProgress(0);
        // guestsOverQuestion = findViewById(R.id.guestsOverQuestion);

        noiseBar = findViewById(R.id.noiseBar);
        noiseText = findViewById(R.id.noiseText);
        noiseBar.setProgress(0);


        cleanlinessBar = findViewById(R.id.cleanlinessBar);
        cleanlinessText = findViewById(R.id.cleanlinessText);
        cleanlinessBar.setProgress(0);

        desiredusername = findViewById(R.id.desiredusername);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the submit button is clicked and it sends the Json Post request and sets the desired username
             * @param v
             */
            @Override
            public void onClick(View v) {
                sendJson();

            }
        });




        ratingSeekBarnightorday.setProgress(0); // Initial progress
        updateBar(0);
        hostingBar.setProgress(0);



        ratingSeekBarnightorday.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateBar(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        hostingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hostingText.setText(" "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        petBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                petText.setText(" "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        smokingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                smokingText.setText(" "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        organizedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                organizedText.setText(" "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        guestsOverBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                guestsOverText.setText(" "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        noiseBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                noiseText.setText(" "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        cleanlinessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cleanlinessText.setText(" "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }


    /**
     * This method sends the Json Post request to the server, it sends the infomation entered by the user to the server
     */
    public void sendJson() {
        // Check if username is empty
        if (TextUtils.isEmpty(desiredusername.getText().toString())) {
            desiredusername.setError("Please enter a username");
            return;
        }

        // Check if any of the SeekBars are at 0 (default/unselected state)
        if (ratingSeekBarnightorday.getProgress() == 0 ||
                hostingBar.getProgress() == 0 ||
                petBar.getProgress() == 0 ||
                smokingBar.getProgress() == 0 ||
                organizedBar.getProgress() == 0 ||
                guestsOverBar.getProgress() == 0 ||
                noiseBar.getProgress() == 0 ||
                cleanlinessBar.getProgress() == 0) {

            // Show an error message
            Toast.makeText(this, "Please complete all sections of the quiz", Toast.LENGTH_LONG).show();
            return;
        }

        // If all validations pass, proceed with creating the JSON
        JSONObject json = new JSONObject();
        try {
            json.put("username", desiredusername.getText().toString());
            json.put("morningPerson", ratingSeekBarnightorday.getProgress());
            json.put("hosting", hostingBar.getProgress());
            json.put("likingPets", petBar.getProgress());
            json.put("smoking", smokingBar.getProgress());
            json.put("organizationSkills", organizedBar.getProgress());
            json.put("peopleOver", guestsOverBar.getProgress());
            json.put("noiseLevel", noiseBar.getProgress());
            json.put("cleanliness", cleanlinessBar.getProgress());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());
                Intent intent = new Intent(Quiz.this, RoommateFinderActivity.class);
                intent.putExtra("username", desiredusername.getText().toString());
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    /**
     * This method updates the bar
     * @param i
     */
    public void updateBar(int i) {
        numberOfnightordayText.setText(" "+i);



    }
}









