package com.dormigo;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private boolean isStudent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);

        View mainView = findViewById(R.id.login_page);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize UI components
        LinearLayout roleStudent = findViewById(R.id.roleStudent);
        LinearLayout roleLandlord = findViewById(R.id.roleLandlord);
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        EditText passwordInput = findViewById(R.id.passwordInput);
        TextView btnSignIn = findViewById(R.id.btnSignIn);
        TextView createAccount = findViewById(R.id.createAccount);
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        // Role Selection Logic
        roleStudent.setOnClickListener(v -> {
            isStudent = true;
            updateRoleUI(roleStudent, roleLandlord);
        });

        roleLandlord.setOnClickListener(v -> {
            isStudent = false;
            updateRoleUI(roleStudent, roleLandlord);
        });

        // Password Visibility Logic
        togglePasswordVisibility.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            togglePasswordVisibility.setImageResource(R.drawable.ic_eye);
            passwordInput.setSelection(passwordInput.getText().length());
        });

        // Sign In Logic
        btnSignIn.setOnClickListener(v -> {
            String role = isStudent ? "Student" : "Landlord";
            Toast.makeText(this, "Signing in as " + role, Toast.LENGTH_SHORT).show();
        });

        // Forgot Password Logic
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Create Account Navigation
        createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });
    }

    private void updateRoleUI(LinearLayout studentLayout, LinearLayout landlordLayout) {
        if (isStudent) {
            studentLayout.setBackgroundResource(R.drawable.bg_role_selected);
            landlordLayout.setBackgroundResource(R.drawable.bg_role_unselected);
            // Also update icon tints if needed, but let's stick to background first
        } else {
            studentLayout.setBackgroundResource(R.drawable.bg_role_unselected);
            landlordLayout.setBackgroundResource(R.drawable.bg_role_selected);
        }
    }
}