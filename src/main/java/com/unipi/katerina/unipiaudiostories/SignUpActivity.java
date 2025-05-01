package com.unipi.katerina.unipiaudiostories;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button signUpButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new account
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveUserDataToFirestore(user.getUid(), email);
                            }
                            Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void saveUserDataToFirestore(String userId, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);  // Store email
        userData.put("createdAt", com.google.firebase.Timestamp.now());  // Store the account creation timestamp
        userData.put("listenCount", 0);  // Initialize listenCount to 0
        userData.put("favorites", new ArrayList<String>());  // Initialize favorites as an empty list

        // Create a new document in the users collection with userId as document ID
        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // After saving user data to Firestore, log out the user to prevent auto sign-in
                    FirebaseAuth.getInstance().signOut();  // Log out the newly created user
                    Toast.makeText(SignUpActivity.this, "User data saved successfully! Please sign in.", Toast.LENGTH_SHORT).show();

                    // Navigate to the login screen
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish(); // Close the SignUpActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUpActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}