package com.unipi.katerina.unipiaudiostories;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements StoryAdapter.OnStoryClickListener {

    RecyclerView storiesRecyclerView;
    StoryAdapter adapter;
    List<Story> storiesList;
    Button signUpButton, loginButton, statisticsButton, SignOutbutton;
    FirebaseAuth auth;
    Spinner languageSpinner;
    String selectedLanguage = "en"; // Default language

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView and adapter
        storiesRecyclerView = findViewById(R.id.storiesRecyclerView);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        storiesList = new ArrayList<>();
        adapter = new StoryAdapter(storiesList, this, this);
        storiesRecyclerView.setAdapter(adapter);

        // Initialize buttons
        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.loginButton);
        statisticsButton = findViewById(R.id.statisticsButton);
        SignOutbutton = findViewById(R.id.SignOutbutton);
        languageSpinner = findViewById(R.id.languageSpinner);

        // Initially hide the SignOut button and favorite button in adapter
        SignOutbutton.setVisibility(View.GONE);

        // Firebase authentication
        auth = FirebaseAuth.getInstance();

        fetchStories(selectedLanguage);

        // Info Button click listener
        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Final Assignment");
            builder.setMessage("Aikaterini Maria Drosou\nmppl2304");
            builder.setPositiveButton("OK", null);
            builder.show();
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        selectedLanguage = "de"; // German
                        break;
                    case 2:
                        selectedLanguage = "it"; // Italian
                        break;
                    default:
                        selectedLanguage = "en"; // English (default)
                        break;
                }
                // Save selected language
                getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
                        .putString("selectedLanguage", selectedLanguage).apply();

                fetchStories(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    DatabaseHandler dbHelper = new DatabaseHandler(this);
    // Fetch stories from Firestore
    private void fetchStories(String language) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("stories").get().addOnSuccessListener(documents -> {
            storiesList.clear();

            for (DocumentSnapshot document : documents) {
                String storyId = document.getId(); // Get only the story ID

                // Match with hardcoded titles and authors
                Story story = StoryUtils.getHardcodedStory(storyId, language);
                if (story != null) {
                    storiesList.add(story);

                    // Save the image name in the local database
                    String imageName = story.getImageURL(); // Assuming imageURL is the drawable name
                    if (imageName != null && !imageName.isEmpty()) {
                        dbHelper.addStory(story.getId(), story.getTitle(), imageName);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch stories: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    // Check user authentication status and update UI accordingly
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is logged in
            SignOutbutton.setVisibility(View.VISIBLE);
            adapter.setFavoriteButtonVisible(true);
        } else {
            // User is not logged in
            SignOutbutton.setVisibility(View.GONE);
            adapter.setFavoriteButtonVisible(false);
        }
    }


    // SignUp Button click listener
    public void SignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    // SignIn Button click listener
    public void SignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // SignOut Button click listener
    public void SignOut(View view) {
        // Sign out the current user from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Display a message indicating the user has signed out
        Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();

        // Hide the SignOut button after signing out
        SignOutbutton.setVisibility(View.GONE);

        // Update the favorite button visibility for each story item to gone (invisible)
        adapter.setFavoriteButtonVisible(false);
    }

    public void ViewStat(View view) {
        // Get the current logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Create an Intent to open StatisticsActivity
            Intent intent = new Intent(this, StatisticsActivity.class);
            String userId = currentUser.getUid();
            intent.putExtra("userId", userId); // Pass the current user's UID
            startActivity(intent);
        } else {
            // If the user is not logged in, show a toast message
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStoryClick(Story story) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && story.getId() != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);

            // Increment listenCount every click
            userRef.update("listenCount", com.google.firebase.firestore.FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Listen count updated!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Failed to update listen count.", Toast.LENGTH_SHORT).show();
                    });

            // Navigate to the story details
            Intent intent = new Intent(this, MainActivity2.class);
            intent.putExtra("storyId", story.getId());
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
        }
    }
}