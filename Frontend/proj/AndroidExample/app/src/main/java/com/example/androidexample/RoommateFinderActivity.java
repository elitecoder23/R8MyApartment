package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.handshake.ServerHandshake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class RoommateFinderActivity extends AppCompatActivity implements WebSocketListener {

    private EditText searchBar;
    private ImageButton searchButton, settingsButton;
    private ImageButton notificationsButton;
    private Button takeQuizButton;
    private TextView recommendedTitle;
    private RecyclerView recommendedList;
    private Button SendRequestButton;

    public static WebSocketService webSocketService;
    public static String usernameD;
    public final String URLAllFriends = "http://coms-3090-057.class.las.iastate.edu:8080/api/friend-requests/";
    public final String URLRoommateRecomendation = "http://coms-3090-057.class.las.iastate.edu:8080/api/roommate-matching/find-matches?username=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roommatefinder);

        // Initialize views
        settingsButton = findViewById(R.id.settings_button);
        notificationsButton = findViewById(R.id.notifications_button);
        takeQuizButton = findViewById(R.id.take_quiz_button);
        recommendedTitle = findViewById(R.id.recommended_title);
        recommendedList = findViewById(R.id.recommended_list);
        SendRequestButton = findViewById(R.id.sendNewReq);

        // Set LayoutManager to RecyclerView
        recommendedList.setLayoutManager(new LinearLayoutManager(this));

        // Set empty adapter initially to avoid "No adapter attached" error
        recommendedList.setAdapter(new FriendAdapter(new ArrayList<>(), new FriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Friend friend) {
                showFriendDetail(friend);
            }
        }));

        fetchFriends();
        fetchRecommendedRoommates();

        // Set onClickListeners for buttons
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoommateFinderActivity.this, editRMFprofileActivity.class);
                startActivity(intent);
            }
        });

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoommateFinderActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });

        takeQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoommateFinderActivity.this, Quiz.class);
                startActivity(intent);
            }
        });

        SendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoommateFinderActivity.this, FriendRequest.class);
                SendRequestButton.setEnabled(false);
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                String friendUsername = username;
                webSocketService = new WebSocketService(RoommateFinderActivity.this);
                if (!friendUsername.isEmpty() && friendUsername.length() >= 3 && friendUsername.length() <= 20) {
                    try {
                        webSocketService.establishConnection(friendUsername);
                    } catch (Exception e) {
                        Log.d("FriendReqSent", "Error occurred", e);
                    }
                } else {
                    SendRequestButton.setEnabled(true);
                }
                startActivity(intent);
            }
        });
    }

    // this method will display all the friends of the user
    private void fetchFriends() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String url = URLAllFriends + username + "/listFriendsByUsername";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Friends", response.toString());
                        try {
                            List<Friend> friendsList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject friendObj = response.getJSONObject(i).getJSONObject("friend");
                                Log.d("Friends", friendObj.toString());
                                Friend friend = new Friend(
                                        friendObj.getString("username"),
                                        friendObj.getString("firstName"),
                                        friendObj.getString("lastName"),
                                        friendObj.getString("email"),
                                        friendObj.getString("birthDate"),
                                        friendObj.getString("phoneNumber")
                                );
                                friendsList.add(friend);
                            }
                            displayFriends(friendsList);
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

    private void displayFriends(List<Friend> friendsList) {
        RecyclerView.Adapter adapter = new FriendAdapter(friendsList, new FriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Friend friend) {
                showFriendDetail(friend);
            }
        });
        recommendedList.setAdapter(adapter);
    }
    private void fetchRecommendedRoommates() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String url = URLRoommateRecomendation + username;
        Log.d("URL", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Roommate> roommatesList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject roommateObj = response.getJSONObject(i);
                                JSONObject profileObj = roommateObj.getJSONObject("matchedUserProfile");
                                Roommate.MatchedUserProfile profile = new Roommate.MatchedUserProfile(
                                        profileObj.getString("username"),
                                        profileObj.getInt("morningPerson"),
                                        profileObj.getInt("hosting"),
                                        profileObj.getInt("likingPets"),
                                        profileObj.getInt("smoking"),
                                        profileObj.getInt("organizationSkills"),
                                        profileObj.getInt("peopleOver"),
                                        profileObj.getInt("noiseLevel"),
                                        profileObj.getInt("cleanliness")
                                );
                                Roommate roommate = new Roommate(
                                        roommateObj.getString("username"),
                                        roommateObj.getDouble("starRating"),
                                        profile
                                );
                                roommatesList.add(roommate);
                            }
                            displayRecommendedRoommates(roommatesList);
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
    private void displayRecommendedRoommates(List<Roommate> roommatesList) {
        RecyclerView recommendedRoommatesList = findViewById(R.id.recommended_roommates_list);
        recommendedRoommatesList.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter adapter = new RoommateAdapter(roommatesList, new RoommateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Roommate roommate) {
                showRoommateDetail(roommate);
            }
        });
        recommendedRoommatesList.setAdapter(adapter);
    }

    private void showRoommateDetail(Roommate roommate) {
        Intent intent = new Intent(RoommateFinderActivity.this, RoommateDetailActivity.class);
        intent.putExtra("username", roommate.getUsername());
        intent.putExtra("starRating", roommate.getStarRating());
        Roommate.MatchedUserProfile profile = roommate.getMatchedUserProfile();
        intent.putExtra("morningPerson", profile.getMorningPerson());
        intent.putExtra("hosting", profile.getHosting());
        intent.putExtra("likingPets", profile.getLikingPets());
        intent.putExtra("smoking", profile.getSmoking());
        intent.putExtra("organizationSkills", profile.getOrganizationSkills());
        intent.putExtra("peopleOver", profile.getPeopleOver());
        intent.putExtra("noiseLevel", profile.getNoiseLevel());
        intent.putExtra("cleanliness", profile.getCleanliness());
        startActivity(intent);
    }

    private void showFriendDetail(Friend friend) {
        Intent intent = new Intent(RoommateFinderActivity.this, FriendDetailActivity.class);
        intent.putExtra("username", friend.getUsername());
        intent.putExtra("firstName", friend.getFirstName());
        intent.putExtra("lastName", friend.getLastName());
        intent.putExtra("email", friend.getEmail());
        intent.putExtra("birthDate", friend.getBirthDate());
        intent.putExtra("phoneNumber", friend.getPhoneNumber());
        startActivity(intent);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketMessage(String message) {}

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}