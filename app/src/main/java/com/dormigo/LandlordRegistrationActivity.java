package com.dormigo;

import android.content.Intent;
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

        // Back Button Logic
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Switch to Student Role
        if (roleStudent != null) {
            roleStudent.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateAccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        // Password Visibility Logic
        if (togglePasswordVisibility != null) {
            togglePasswordVisibility.setOnClickListener(v -> {
                isPasswordVisible = !isPasswordVisible;
                togglePassword(passwordInput, isPasswordVisible);
            });
        }

        if (toggleConfirmPasswordVisibility != null) {
            toggleConfirmPasswordVisibility.setOnClickListener(v -> {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                togglePassword(confirmPasswordInput, isConfirmPasswordVisible);
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
                String pass = passwordInput.getText().toString();
                String confirmPass = confirmPasswordInput.getText().toString();

                if (pass.isEmpty()) {
                    Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
                } else if (!Objects.equals(pass, confirmPass)) {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                } else if (uploadedFileUri == null) {
                    Toast.makeText(this, "Please upload the required documents", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Landlord account created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        // Sign In Link
        if (signInLink != null) {
            signInLink.setOnClickListener(v -> finish());
        }
    }

    private void togglePassword(EditText editText, boolean visible) {
        if (editText == null) return;
        if (visible) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editText.setSelection(editText.getText().length());
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null && uri != null && uri.getPath() != null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "Unknown file";
    }
}