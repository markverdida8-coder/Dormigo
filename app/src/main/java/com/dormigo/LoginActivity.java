package com.dormigo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private boolean isStudent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("DormigoPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);

        // Adjust for system bars
        View root = findViewById(R.id.login_page);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize UI components
        LinearLayout roleStudent = findViewById(R.id.roleStudent);
        LinearLayout roleLandlord = findViewById(R.id.roleLandlord);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        TextView btnSignIn = findViewById(R.id.btnSignIn);
        TextView createAccount = findViewById(R.id.createAccount);

        // Role Selection Logic
        roleStudent.setOnClickListener(v -> {
            isStudent = true;
            updateRoleUI(roleStudent, roleLandlord);
        });

        roleLandlord.setOnClickListener(v -> {
            isStudent = false;
            updateRoleUI(roleStudent, roleLandlord);
        });

        // Forgot Password Logic
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Password Visibility Logic
        togglePasswordVisibility.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            togglePassword(passwordInput, togglePasswordVisibility, isPasswordVisible);
        });

        // Sign In Logic
        btnSignIn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String pass = passwordInput.getText().toString();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Signed in as " + (isStudent ? "Student" : "Landlord"), Toast.LENGTH_SHORT).show();
                
                // Save login state
                SharedPreferences.Editor editor = getSharedPreferences("DormigoPrefs", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                // Navigate to Home Page
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overrideActivityFade();
            }
        });

        // Create Account Logic
        createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
            overrideActivitySlide();
        });
    }

    @SuppressWarnings("deprecation")
    private void overrideActivityFade() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @SuppressWarnings("deprecation")
    private void overrideActivitySlide() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void togglePassword(EditText editText, ImageView imageView, boolean visible) {
        if (visible) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.drawable.ic_eye_off);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageView.setImageResource(R.drawable.ic_eye);
        }
        editText.setSelection(editText.getText().length());
    }

    private void updateRoleUI(LinearLayout studentLayout, LinearLayout landlordLayout) {
        if (isStudent) {
            studentLayout.setBackgroundResource(R.drawable.bg_role_selected);
            landlordLayout.setBackgroundResource(R.drawable.bg_role_unselected);
        } else {
            studentLayout.setBackgroundResource(R.drawable.bg_role_unselected);
            landlordLayout.setBackgroundResource(R.drawable.bg_role_selected);
        }
    }
}