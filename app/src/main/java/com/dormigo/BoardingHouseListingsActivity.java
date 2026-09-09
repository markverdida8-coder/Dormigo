package com.dormigo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

public class BoardingHouseListingsActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private TextView locationLabel;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.boarding_house_listings);

        // Adjust for system bars
        View mainLayout = findViewById(R.id.mainLayout);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                
                View bottomNav = findViewById(R.id.bottomNav);
                if (bottomNav != null) {
                    bottomNav.setPadding(0, 0, 0, systemBars.bottom);
                }
                return insets;
            });
        }

        locationLabel = findViewById(R.id.locationLabel);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (hasLocationPermission()) {
            fetchCurrentLocationLabel();
        } else {
            requestLocationPermission();
        }

        setupBottomNavigation();
        setupClickListeners();
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SEARCH_QUERY")) {
            String query = intent.getStringExtra("SEARCH_QUERY");
            EditText searchInput = findViewById(R.id.searchInput);
            if (searchInput != null && query != null) {
                searchInput.setText(query);
                // In a real app, you would also trigger the filter logic here
                showToast("Searching for: " + query);
            }
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_explore);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            // Explore, Chats, Requests - stay here for now or show toast
            if (id != R.id.nav_explore) {
                showToast(item.getTitle().toString());
            }
            return id == R.id.nav_explore;
        });
    }

    private void setupClickListeners() {
        View cardListing1 = findViewById(R.id.cardListing1);
        if (cardListing1 != null) {
            cardListing1.setOnClickListener(v -> {
                Intent intent = new Intent(this, ViewBoardingHouseActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    // ---------- Location handling ----------

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
               ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    @SuppressWarnings("MissingPermission")
    private void fetchCurrentLocationLabel() {
        if (locationLabel != null) {
            locationLabel.setText(R.string.getting_location);
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                updateLocationLabel(location);
            } else if (locationLabel != null) {
                locationLabel.setText(R.string.location_unavailable);
            }
        }).addOnFailureListener(this, e -> {
            if (locationLabel != null) {
                locationLabel.setText(R.string.location_unavailable);
            }
        });
    }

    private void updateLocationLabel(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                if (locationLabel != null) {
                    locationLabel.setText(buildNearestPlaceLabel(address));
                }
            } else if (locationLabel != null) {
                locationLabel.setText(R.string.location_unavailable);
            }
        } catch (Exception e) {
            if (locationLabel != null) {
                locationLabel.setText(R.string.location_unavailable);
            }
        }
    }

    private String buildNearestPlaceLabel(Address address) {
        String feature = address.getFeatureName();
        String subLocality = address.getSubLocality();
        String locality = address.getLocality();

        String place = feature != null ? feature : (subLocality != null ? subLocality : locality);
        return getString(R.string.near_location, place != null ? place : getString(R.string.your_location));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocationLabel();
            } else if (locationLabel != null) {
                locationLabel.setText(R.string.location_permission_needed);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}