package com.dormigo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewBoardingHouseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_boarding_houses);

        // Adjust for system bars
        View mainLayout = findViewById(R.id.mainLayout);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);

                View bottomActions = findViewById(R.id.bottomActions);
                if (bottomActions != null) {
                    bottomActions.setPadding(bottomActions.getPaddingLeft(), 
                        bottomActions.getPaddingTop(), 
                        bottomActions.getPaddingRight(), 
                        systemBars.bottom);
                }
                return insets;
            });
        }

        setupClickListeners();
    }

    @SuppressWarnings("deprecation")
    private void setupClickListeners() {
        ImageView btnBack = findViewById(R.id.btnBack);
        TextView btnMessage = findViewById(R.id.btnMessage);
        TextView btnRequestBooking = findViewById(R.id.btnRequestBooking);
        View btnDirections = findViewById(R.id.btnDirections);

        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        
        btnMessage.setOnClickListener(v -> showToast("Message landlord"));
        
        btnRequestBooking.setOnClickListener(v -> showToast("Booking request sent"));

        if (btnDirections != null) {
            btnDirections.setOnClickListener(v -> {
                String coordinates = getString(R.string.sampaguita_coordinates);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + coordinates);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback to web browser if Google Maps is not installed
                    Uri webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + coordinates);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                    startActivity(webIntent);
                }
            });
        }

        View locationCard = findViewById(R.id.locationCard);
        if (locationCard != null) {
            locationCard.setOnClickListener(v -> {
                String coordinates = getString(R.string.sampaguita_coordinates);
                String label = getString(R.string.sampaguita_residences);
                Uri gmmIntentUri = Uri.parse("geo:" + coordinates + "?q=" + coordinates + "(" + Uri.encode(label) + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback to web browser
                    Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + coordinates);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                    startActivity(webIntent);
                }
            });
        }

        loadStaticMap();
    }

    private void loadStaticMap() {
        ImageView imgStaticMap = findViewById(R.id.imgStaticMap);
        if (imgStaticMap == null) return;

        String coordinates = getString(R.string.sampaguita_coordinates);
        String apiKey = "YOUR_GOOGLE_MAPS_API_KEY"; // TODO: Replace with real API Key
        int zoom = 16;
        int width = 600;
        int height = 300;
        String markerColor = "0x0B4A3A";

        // Construct Google Static Maps URL
        // Format: https://maps.googleapis.com/maps/api/staticmap?center=LAT,LNG&zoom=ZOOM&size=WIDTHxHEIGHT&markers=color:COLOR|LAT,LNG&key=API_KEY
        String staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=" + coordinates +
                "&zoom=" + zoom +
                "&size=" + width + "x" + height +
                "&markers=color:" + markerColor + "|" + coordinates +
                "&key=" + apiKey;

        Glide.with(this)
                .load(staticMapUrl)
                .centerCrop()
                .placeholder(R.drawable.bg_map_placeholder)
                .into(imgStaticMap);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}