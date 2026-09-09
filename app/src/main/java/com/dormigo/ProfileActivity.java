package com.dormigo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_settings);

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

        setupBottomNavigation();
        setupClickListeners();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_explore) {
                Intent intent = new Intent(ProfileActivity.this, BoardingHouseListingsActivity.class);
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
            }
            return id == R.id.nav_profile;
        });
    }

    private void setupClickListeners() {
        LinearLayout btnTransactionHistory = findViewById(R.id.btnTransactionHistory);
        LinearLayout btnNotifications = findViewById(R.id.btnNotifications);
        LinearLayout btnAccountDetails = findViewById(R.id.btnAccountDetails);
        LinearLayout btnSearchPreferences = findViewById(R.id.btnSearchPreferences);
        LinearLayout btnVerification = findViewById(R.id.btnVerification);
        LinearLayout btnMyReviews = findViewById(R.id.btnMyReviews);
        LinearLayout btnSignOut = findViewById(R.id.btnSignOut);

        btnTransactionHistory.setOnClickListener(v -> showToast("Transaction History"));
        btnNotifications.setOnClickListener(v -> showToast("Notifications"));
        btnAccountDetails.setOnClickListener(v -> showToast("Account Details"));
        btnSearchPreferences.setOnClickListener(v -> showToast("Search Preferences"));
        btnVerification.setOnClickListener(v -> showToast("Verification"));
        btnMyReviews.setOnClickListener(v -> showToast("My Reviews"));

        btnSignOut.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}