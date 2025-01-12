package com.example.gympro;

import static com.google.firebase.auth.FirebaseAuth.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; 

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private Button resetPasswordButton, loginButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.forgatPassword);
        loginButton = findViewById(R.id.loginButton);

        resetPasswordButton.setOnClickListener(v -> resetPassword());
        loginButton.setOnClickListener(v -> startActivity(new Intent(ResetPasswordActivity.this, MainActivity.class)));
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Şifre sıfırlama maili gönder
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Mail hesabınız kayıtlı ise mailinizi kontrol edin", Toast.LENGTH_LONG).show();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error occurred.";
                        Toast.makeText(this, "Failed to send email: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
