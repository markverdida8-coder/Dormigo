package com.dormigo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class LandlordRegistrationActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private Uri uploadedFileUri;
    private TextView uploadText;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    uploadedFileUri = result.getData().getData();
                    if (uploadedFileUri != null) {
                        String fileName = getFileName(uploadedFileUri);
                        uploadText.setText(fileName);
                        Toast.makeText(this, "File selected: " + fileName, Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.landlord_login_page);

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
        LinearLayout btnBack = findViewById(R.id.btnBack);
        LinearLayout roleStudent = findViewById(R.id.roleStudent);
        LinearLayout uploadDropzone = findViewById(R.id.uploadDropzone);
        uploadText = findViewById(R.id.uploadText);
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        ImageView toggleConfirmPasswordVisibility = findViewById(R.id.toggleConfirmPasswordVisibility);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        TextView btnCreateAccount = findViewById(R.id.btnCreateAccount);
        TextView signInLink = findViewById(R.id.signInLink);

        EditText fullNameInput = findViewById(R.id.fullNameInput);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText mobileNumberInput = findViewById(R.id.mobileNumberInput);
        com.google.android.material.checkbox.MaterialCheckBox termsCheckbox = findViewById(R.id.termsCheckbox);

        // Back Button Logic
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
                overrideActivityFade();
            });
        }

        // Switch to Student Role
        if (roleStudent != null) {
            roleStudent.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateAccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overrideActivityFade();
            });
        }

        // Password Visibility Logic
        if (togglePasswordVisibility != null) {
            togglePasswordVisibility.setOnClickListener(v -> {
                isPasswordVisible = !isPasswordVisible;
                togglePassword(passwordInput, togglePasswordVisibility, isPasswordVisible);
            });
        }

        if (toggleConfirmPasswordVisibility != null) {
            toggleConfirmPasswordVisibility.setOnClickListener(v -> {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                togglePassword(confirmPasswordInput, toggleConfirmPasswordVisibility, isConfirmPasswordVisible);
            });
        }

        // Upload Dropzone Logic
        if (uploadDropzone != null) {
            uploadDropzone.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                filePickerLauncher.launch(Intent.createChooser(intent, "Select Document"));
            });
        }

        // Create Account Logic
        if (btnCreateAccount != null) {
            btnCreateAccount.setOnClickListener(v -> {
                String fullName = fullNameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String mobile = mobileNumberInput.getText().toString().trim();
                String pass = passwordInput.getText().toString();
                String confirmPass = confirmPasswordInput.getText().toString();

                if (fullName.isEmpty() || email.isEmpty() || mobile.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!Objects.equals(pass, confirmPass)) {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                } else if (uploadedFileUri == null) {
                    Toast.makeText(this, "Please upload the required documents", Toast.LENGTH_SHORT).show();
                } else if (!termsCheckbox.isChecked()) {
                    Toast.makeText(this, "Please agree to the terms", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Landlord account created successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Save login state
                    SharedPreferences.Editor editor = getSharedPreferences("DormigoPrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    // Navigate to Home Page
                    Intent intent = new Intent(LandlordRegistrationActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }

        // Sign In Link
        if (signInLink != null) {
            signInLink.setOnClickListener(v -> {
                finish();
                overrideActivitySlideBack();
            });
        }
    }

    @SuppressWarnings("deprecation")
    private void overrideActivityFade() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @SuppressWarnings("deprecation")
    private void overrideActivitySlideBack() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void togglePassword(EditText editText, ImageView imageView, boolean visible) {
        if (editText == null || imageView == null) return;
        if (visible) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.drawable.ic_eye_off);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageView.setImageResource(R.drawable.ic_eye);
        }
        editText.setSelection(editText.getText().length());
    }

    private String getFileName(Uri uri) {
        if (uri == null) return "Unknown file";

        String name = null;
        String scheme = uri.getScheme();
        if ("content".equals(scheme)) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        name = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (name == null && uri.getPath() != null) {
            String path = uri.getPath();
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                name = path.substring(cut + 1);
            } else {
                name = path;
            }
        }

        return Objects.requireNonNullElse(name, "Unknown file");
    }
}