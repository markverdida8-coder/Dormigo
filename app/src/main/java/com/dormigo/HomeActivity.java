package com.dormigo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private TextView locationLabel;
    private FusedLocationProviderClient fusedLocationClient;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_page_layout);

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

        bindViews();
        setupBottomNavigation();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (hasLocationPermission()) {
            fetchCurrentLocationLabel();
        } else {
            requestLocationPermission();
        }
    }

    private void bindViews() {
        locationLabel = findViewById(R.id.locationLabel);
        bottomNav = findViewById(R.id.bottomNav);
        
        TextView wavingHand = findViewById(R.id.wavingHand);
        if (wavingHand != null) {
            Animation wave = AnimationUtils.loadAnimation(this, R.anim.wave);
            wavingHand.startAnimation(wave);
        }
        
        EditText homeSearchInput = findViewById(R.id.homeSearchInput);
        ImageView btnSearchSubmit = findViewById(R.id.btnSearchSubmit);
        
        if (homeSearchInput != null) {
            homeSearchInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(homeSearchInput.getText().toString());
                    return true;
                }
                return false;
            });
        }
        
        if (btnSearchSubmit != null) {
            btnSearchSubmit.setOnClickListener(v -> {
                if (homeSearchInput != null) {
                    performSearch(homeSearchInput.getText().toString());
                }
            });
        }
        
        View cardListing1 = findViewById(R.id.cardListing1);
        View cardListing2 = findViewById(R.id.cardListing2);
        
        if (cardListing1 != null) {
            cardListing1.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ViewBoardingHouseActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
        
        if (cardListing2 != null) {
            cardListing2.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ViewBoardingHouseActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        View btnSeeAll = findViewById(R.id.btnSeeAll);
        if (btnSeeAll != null) {
            btnSeeAll.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, BoardingHouseListingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Already on Home — nothing to do.
                return true;
            } else if (id == R.id.nav_explore) {
                Intent intent = new Intent(HomeActivity.this, BoardingHouseListingsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_chats) {
                showToast("Chats");
                return true;
            } else if (id == R.id.nav_requests) {
                showToast("Requests");
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            return false;
        });
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
        locationLabel.setText(R.string.getting_location);

        CancellationTokenSource cts = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        updateLocationLabel(location);
                    } else {
                        locationLabel.setText(R.string.location_unavailable);
                    }
                })
                .addOnFailureListener(this, e -> locationLabel.setText(R.string.location_unavailable));
    }

    private void updateLocationLabel(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1, addresses -> {
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        runOnUiThread(() -> locationLabel.setText(buildNearestPlaceLabel(address)));
                    } else {
                        runOnUiThread(() -> locationLabel.setText(R.string.location_unavailable));
                    }
                });
            } else {
                // Fallback for older versions
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    locationLabel.setText(buildNearestPlaceLabel(address));
                } else {
                    locationLabel.setText(R.string.location_unavailable);
                }
            }
        } catch (Exception e) {
            locationLabel.setText(R.string.location_unavailable);
        }
    }

    private String buildNearestPlaceLabel(Address address) {
        String subLocality = address.getSubLocality(); // Barangay
        String locality = address.getLocality();       // City/Municipality

        StringBuilder sb = new StringBuilder();
        if (subLocality != null && !subLocality.isEmpty()) {
            sb.append(subLocality);
        }
        if (locality != null && !locality.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(locality);
        }

        String place = sb.length() > 0 ? sb.toString() : null;
        return getString(R.string.near_location, Objects.requireNonNullElseGet(place, () -> getString(R.string.your_location)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocationLabel();
            } else {
                locationLabel.setText(R.string.location_permission_needed);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    private void performSearch(String query) {
        Intent intent = new Intent(HomeActivity.this, BoardingHouseListingsActivity.class);
        intent.putExtra("SEARCH_QUERY", query);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}