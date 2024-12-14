package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class editRMFprofileActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView nightordayTextView;
    private TextView hostingTextView;
    private TextView petTextView;
    private TextView smokingTextView;
    private TextView organizedTextView;
    private TextView guestsOverTextView;
    private TextView noiseTextView;
    private TextView cleanlinessTextView;
    private Button deleteProfileButton;
    private Button editQuizAnswers;

    private static final String URL = "http://coms-3090-057.class.las.iastate.edu:8080/api/roommate-matching/quiz-submission";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editrmfprofile);


        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");


        // Initialize TextViews
        usernameTextView = findViewById(R.id.usernameTextView);
        nightordayTextView = findViewById(R.id.nightordayTextView);
        hostingTextView = findViewById(R.id.hostingTextView);
        petTextView = findViewById(R.id.petTextView);
        smokingTextView = findViewById(R.id.smokingTextView);
        organizedTextView = findViewById(R.id.organizedTextView);
        guestsOverTextView = findViewById(R.id.guestsOverTextView);
        noiseTextView = findViewById(R.id.noiseTextView);
        cleanlinessTextView = findViewById(R.id.cleanlinessTextView);
        deleteProfileButton = findViewById(R.id.deleteProfileButton);
        editQuizAnswers = findViewById(R.id.editQuizAnswersButton);


        // Fetch user details
        fetchUserDetails(username);

        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserProfile(username);
            }
        });

        editQuizAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showEditDialog(username);
            }
        });

    }

    private void fetchUserDetails(String username) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL + "/" + username;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Assuming the response contains user details
                            String username = response.getString("username");
                            int nightorday = response.getInt("morningPerson");
                            int hosting = response.getInt("hosting");
                            int pet = response.getInt("likingPets");
                            int smoking = response.getInt("smoking");
                            int organized = response.getInt("organizationSkills");
                            int guestsOver = response.getInt("peopleOver");
                            int noise = response.getInt("noiseLevel");
                            int cleanliness = response.getInt("cleanliness");
                            Log.d("RESPONSE FROM SERVER", response.toString());


                            // Set values to TextViews
                            usernameTextView.setText("Username: " + username);
                            nightordayTextView.setText("Night or Day: " + nightorday);
                            hostingTextView.setText("Hosting: " + hosting);
                            petTextView.setText("Pet: " + pet);
                            smokingTextView.setText("Smoking: " + smoking);
                            organizedTextView.setText("Organized: " + organized);
                            guestsOverTextView.setText("Guests Over: " + guestsOver);
                            noiseTextView.setText("Noise: " + noise);
                            cleanlinessTextView.setText("Cleanliness: " + cleanliness);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(editRMFprofileActivity.this, "Failed to fetch user details, User needs to take quiz", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(editRMFprofileActivity.this, Quiz.class);
                        startActivity(intent);
                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
    private void deleteUserProfile(String username) {
        String url = URL + "/" + username;
        CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(this, "User deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void showEditDialog(String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Profile");

        // Inflate the custom layout/view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_quiz_scores, null);
        builder.setView(dialogView);

        // Initialize the dialog views
        SeekBar seekBarNightOrDay = dialogView.findViewById(R.id.seekBarNightOrDay);
        TextView textViewNightOrDayValue = dialogView.findViewById(R.id.textViewNightOrDayValue);
        SeekBar seekBarHosting = dialogView.findViewById(R.id.seekBarHosting);
        TextView textViewHostingValue = dialogView.findViewById(R.id.textViewHostingValue);
        SeekBar seekBarPet = dialogView.findViewById(R.id.seekBarPet);
        TextView textViewPetValue = dialogView.findViewById(R.id.textViewPetValue);
        SeekBar seekBarSmoking = dialogView.findViewById(R.id.seekBarSmoking);
        TextView textViewSmokingValue = dialogView.findViewById(R.id.textViewSmokingValue);
        SeekBar seekBarOrganized = dialogView.findViewById(R.id.seekBarOrganized);
        TextView textViewOrganizedValue = dialogView.findViewById(R.id.textViewOrganizedValue);
        SeekBar seekBarGuestsOver = dialogView.findViewById(R.id.seekBarGuestsOver);
        TextView textViewGuestsOverValue = dialogView.findViewById(R.id.textViewGuestsOverValue);
        SeekBar seekBarNoise = dialogView.findViewById(R.id.seekBarNoise);
        TextView textViewNoiseValue = dialogView.findViewById(R.id.textViewNoiseValue);
        SeekBar seekBarCleanliness = dialogView.findViewById(R.id.seekBarCleanliness);
        TextView textViewCleanlinessValue = dialogView.findViewById(R.id.textViewCleanlinessValue);

        // Pre-fill the dialog views with current values
        seekBarNightOrDay.setProgress(Integer.parseInt(nightordayTextView.getText().toString().split(": ")[1]));
        textViewNightOrDayValue.setText(String.valueOf(seekBarNightOrDay.getProgress()));
        seekBarHosting.setProgress(Integer.parseInt(hostingTextView.getText().toString().split(": ")[1]));
        textViewHostingValue.setText(String.valueOf(seekBarHosting.getProgress()));
        seekBarPet.setProgress(Integer.parseInt(petTextView.getText().toString().split(": ")[1]));
        textViewPetValue.setText(String.valueOf(seekBarPet.getProgress()));
        seekBarSmoking.setProgress(Integer.parseInt(smokingTextView.getText().toString().split(": ")[1]));
        textViewSmokingValue.setText(String.valueOf(seekBarSmoking.getProgress()));
        seekBarOrganized.setProgress(Integer.parseInt(organizedTextView.getText().toString().split(": ")[1]));
        textViewOrganizedValue.setText(String.valueOf(seekBarOrganized.getProgress()));
        seekBarGuestsOver.setProgress(Integer.parseInt(guestsOverTextView.getText().toString().split(": ")[1]));
        textViewGuestsOverValue.setText(String.valueOf(seekBarGuestsOver.getProgress()));
        seekBarNoise.setProgress(Integer.parseInt(noiseTextView.getText().toString().split(": ")[1]));
        textViewNoiseValue.setText(String.valueOf(seekBarNoise.getProgress()));
        seekBarCleanliness.setProgress(Integer.parseInt(cleanlinessTextView.getText().toString().split(": ")[1]));
        textViewCleanlinessValue.setText(String.valueOf(seekBarCleanliness.getProgress()));

        // Set listeners to update the TextViews when SeekBar progress changes
        seekBarNightOrDay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewNightOrDayValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarHosting.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewHostingValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarPet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewPetValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarSmoking.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSmokingValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarOrganized.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewOrganizedValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarGuestsOver.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewGuestsOverValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarNoise.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewNoiseValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarCleanliness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewCleanlinessValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save the changes
                try {
                    JSONObject updatedProfile = new JSONObject();
                    updatedProfile.put("username", username);
                    updatedProfile.put("morningPerson", seekBarNightOrDay.getProgress());
                    updatedProfile.put("hosting", seekBarHosting.getProgress());
                    updatedProfile.put("likingPets", seekBarPet.getProgress());
                    updatedProfile.put("smoking", seekBarSmoking.getProgress());
                    updatedProfile.put("organizationSkills", seekBarOrganized.getProgress());
                    updatedProfile.put("peopleOver", seekBarGuestsOver.getProgress());
                    updatedProfile.put("noiseLevel", seekBarNoise.getProgress());
                    updatedProfile.put("cleanliness", seekBarCleanliness.getProgress());

                    updateProfile(updatedProfile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateProfile(JSONObject updatedProfile) {
        String username;
        try {
            username = updatedProfile.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        String url = URL + "/" + username;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, updatedProfile,
                response -> {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    fetchUserDetails(updatedProfile.optString("username")); // Refresh the profile details
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}