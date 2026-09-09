package com.dormigo;

import android.os.Bundle;
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

    private void setupClickListeners() {
        ImageView btnBack = findViewById(R.id.btnBack);
        TextView btnMessage = findViewById(R.id.btnMessage);
        TextView btnRequestBooking = findViewById(R.id.btnRequestBooking);

        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        
        btnMessage.setOnClickListener(v -> showToast("Message landlord"));
        
        btnRequestBooking.setOnClickListener(v -> showToast("Booking request sent"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}