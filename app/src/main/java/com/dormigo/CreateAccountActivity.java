package com.dormigo;

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

public class CreateAccountActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private boolean isStudent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_account);

        // Adjust for system bars
        View root = findViewById(android.R.id.content);
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
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        ImageView toggleConfirmPasswordVisibility = findViewById(R.id.toggleConfirmPasswordVisibility);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        TextView btnCreateAccount = findViewById(R.id.btnCreateAccount);

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
            togglePassword(passwordInput, togglePasswordVisibility, isPasswordVisible);
        });

        toggleConfirmPasswordVisibility.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            togglePassword(confirmPasswordInput, toggleConfirmPasswordVisibility, isConfirmPasswordVisible);
        });

        // Create Account Logic
        btnCreateAccount.setOnClickListener(v -> {
            String pass = passwordInput.getText().toString();
            String confirmPass = confirmPasswordInput.getText().toString();

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            } else {
                String role = isStudent ? "Student" : "Landlord";
                Toast.makeText(this, "Account created as " + role, Toast.LENGTH_SHORT).show();
                finish(); // Go back to login
            }
        });
    }

    private void togglePassword(EditText editText, ImageView imageView, boolean visible) {
        if (visible) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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