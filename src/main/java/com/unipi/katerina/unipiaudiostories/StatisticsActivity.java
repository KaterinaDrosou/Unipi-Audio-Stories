package com.unipi.katerina.unipiaudiostories;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    TextView listenCountTextView, favoritesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        listenCountTextView = findViewById(R.id.listenCountTextView);
        favoritesTextView = findViewById(R.id.favoritesTextView);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            fetchUserStatistics(userId); // Fetch from "users" collection
        } else {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserStatistics(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch listen count with null check
                        Long listenCountLong = documentSnapshot.getLong("listenCount");
                        int listenCount = (listenCountLong != null) ? listenCountLong.intValue() : 0;

                        // Fetch favorites list with null check
                        List<String> favorites = (List<String>) documentSnapshot.get("favorites");
                        if (favorites == null) {
                            favorites = new ArrayList<>();
                        }

                        // Convert story IDs to titles based on the current language
                        List<String> favoriteTitles = new ArrayList<>();
                        for (String storyId : favorites) {
                            Story story = StoryUtils.getHardcodedStory(storyId, getCurrentLanguage());
                            if (story != null) {
                                favoriteTitles.add(story.getTitle());
                            }
                        }

                        // Update UI with statistics
                        listenCountTextView.setText("Listen Count: " + listenCount);
                        favoritesTextView.setText("Favorites: " + (!favoriteTitles.isEmpty() ? listToString(favoriteTitles) : "None"));
                    } else {
                        Toast.makeText(this, "No statistics found! Initialize your profile.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch statistics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to get the currently selected language from MainActivity
    private String getCurrentLanguage() {
        return getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("selectedLanguage", "en");
    }

    private String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}