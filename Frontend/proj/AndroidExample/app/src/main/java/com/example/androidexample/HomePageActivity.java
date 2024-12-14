package com.example.androidexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Home page of the app. This page contains a google maps api along with 5 buttons.
 * The buttons are used to navigate to different pages of the app.
 * The google maps api is used to display the location of apartments using markers/pins.
 * The user can also create a review and view their profile from the home page.
 * The user can also navigate to the roommate finder page from the home page.
 */
public class HomePageActivity extends AppCompatActivity implements OnMapReadyCallback {
    /** TAG for the logs*/
    private static final String TAG = "HomePageActivity";
    /** textview for the username*/
    private TextView username;
    /** GoogleMap variable created to make the map*/
    private GoogleMap mMap;
    /** PlacesClient variable created to make the map*/
    private PlacesClient placesClient;
    /** Marker variable created to make the map*/
    private Marker currentMarker;
    private WebSocketService webSocketService;
   // private final static String = "sde ";

    /**
     * Called when the activity is first created. This method initializes the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String usernameWeb = sharedPreferences.getString("username", "");
        webSocketService = new WebSocketService(this);
        webSocketService.establishConnection(usernameWeb);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d(TAG, "An error occurred: " + status);
            }
        });

        username = findViewById(R.id.username);
        username.setText(usernameWeb);

        ImageButton buttonEditProfile = findViewById(R.id.buttonEditProfile);
        ImageButton buttonReviewPage = findViewById(R.id.buttonCreateReview);
        ImageButton buttonWaypoint = findViewById(R.id.buttonWaypoint);
        ImageButton buttonRoommateFinder = findViewById(R.id.buttonRoommateFinder);
        ImageButton listAllApartments = findViewById(R.id.buttonListAll);

        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(HomePageActivity.this, UserProfile.class);
                startActivity(editProfileIntent);
            }
        });

        listAllApartments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listAllApartmentsIntent = new Intent(HomePageActivity.this, ApartmentList.class);
                startActivity(listAllApartmentsIntent);
            }
        });

        buttonWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListingData();
            }
        });

        buttonReviewPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createReviewIntent = new Intent(HomePageActivity.this, ReviewActivity.class);
                startActivity(createReviewIntent);
            }
        });

        buttonRoommateFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUsernameDialog();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Fetches all of the listings from the server to be used to create the pins on the map.
     */
    private void getListingData() {
        String url = "http://coms-3090-057.class.las.iastate.edu:8080/api/apartments/showall";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject listing = response.getJSONObject(i);
                                String address = listing.getString("address");
                                String title = listing.getString("name");
                                String rent = listing.getString("price");
                                String amenities = listing.getString("amenities");
                                String phoneNumber = listing.getString("contactPhone");
                                String email = listing.getString("contactEmail");

                                Log.d(TAG, "Processing listing: " + address);

                                String snippet = "Title: " + title + "\nRent: " + rent + "\nAmenities: " + amenities + "\nPhone Number: " + phoneNumber + "\nEmail: " + email;
                                addMarkerOnMap(address, snippet, builder);
                            }
                            LatLngBounds bounds = builder.build();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomePageActivity.this, "Failed to load listings", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    /**
     * Called when the homepage is navigated to. This method initializes the map.
     * @param googleMap The GoogleMap object.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng ames = new LatLng(42.0308, -93.6319);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ames, 10));

        // Set custom InfoWindowAdapter
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
    }

    /**
     * Adds a marker on the map for the given address.
     * @param address The address to add a marker for.
     * @param snippet The snippet text to display in the marker's info window.
     * @param builder The LatLngBounds.Builder object to include the marker's position.
     */
    private void addMarkerOnMap(String address, String snippet, LatLngBounds.Builder builder) {
        Geocoder geocoder = new Geocoder(HomePageActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);

                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title(address).snippet(snippet));
                    builder.include(latLng);
                    Log.d(TAG, "Marker added: " + address);
                }
            } else {
                Log.d(TAG, "Address not found: " + address);
                Toast.makeText(HomePageActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(HomePageActivity.this, "Geocoding failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Custom InfoWindowAdapter for displaying custom info windows on map markers.
     */
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mWindow;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        private void render(Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = view.findViewById(R.id.title);
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = view.findViewById(R.id.snippet);
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }
        }
    }

    /**
     * Shows a dialog to enter the username for the Roommate Finder.
     */
    private void showUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Roommate Finder Username");

        // Inflate the custom layout/view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_username, null);
        builder.setView(dialogView);

        // Initialize the dialog views
        EditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the username
                String username = editTextUsername.getText().toString().trim();
                if (!username.isEmpty()) {
                    // Start RoommateFinderActivity with the username
                    SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();

                    Intent roommateFinderIntent = new Intent(HomePageActivity.this, RoommateFinderActivity.class);
                    startActivity(roommateFinderIntent);
                } else {
                    Toast.makeText(HomePageActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
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
}