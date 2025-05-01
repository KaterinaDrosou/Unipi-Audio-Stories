package com.unipi.katerina.unipiaudiostories;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView titleTextView;
    TextView authorTextView;
    TextView yearTextView;
    TextView storyTextView;
    ImageView storyImageView;
    FirebaseFirestore db;
    TextToSpeech textToSpeech;
    boolean isTtsReady = false;
    String userId; // The current user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        db = FirebaseFirestore.getInstance();

        titleTextView = findViewById(R.id.titleTextView);
        authorTextView = findViewById(R.id.authorTextView);
        yearTextView = findViewById(R.id.yearTextView);
        storyTextView = findViewById(R.id.storyTextView);
        storyImageView = findViewById(R.id.storyImageView);

        // Retrieve the storyId and userId from the Intent
        Intent intent = getIntent();
        String storyId = intent.getStringExtra("storyId");
        userId = intent.getStringExtra("userId");

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, this);

        // Fetch the story details
        fetchStoryDetails(storyId);
    }

    private void fetchStoryDetails(String storyId) {
        if (storyId == null || storyId.isEmpty()) {
            Toast.makeText(this, "Invalid story ID!", Toast.LENGTH_LONG).show();
            return;
        }

        DocumentReference storyRef = db.collection("stories").document(storyId);

        storyRef.get(Source.SERVER).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Story story = documentSnapshot.toObject(Story.class);

                if (story != null) {
                    // Fill in the UI with the story details
                    titleTextView.setText(story.getTitle());
                    authorTextView.setText(story.getAuthor());
                    yearTextView.setText(story.getYear());
                    storyTextView.setText(story.getStoryText());

                    // Load the local image
                    DatabaseHandler dbHelper = new DatabaseHandler(this);
                    String imageName = dbHelper.getImageName(storyId);

                    if (imageName != null) {
                        Resources resources = getResources();
                        int imageResourceId = resources.getIdentifier(imageName, "drawable", getPackageName());

                        if (imageResourceId != 0) {
                            storyImageView.setImageResource(imageResourceId);
                        } else {
                            storyImageView.setImageResource(R.drawable.default_image);
                        }
                    }

                    // Speak the new story after loading
                    speakStory(story.getStoryText());

                } else {
                    Toast.makeText(this, "Story data is null!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Story not found!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load story: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int langResult = textToSpeech.setLanguage(Locale.US);
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Text-to-Speech language is not supported", Toast.LENGTH_SHORT).show();
            } else {
                isTtsReady = true;
                System.out.println("Text-to-Speech is ready!");
            }
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakStory(String storyText) {
        if (storyText != null && !storyText.isEmpty()) {
            if (isTtsReady) {
                textToSpeech.stop();  // Stop any previous speech
                textToSpeech.speak(storyText, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(this, "Text-to-Speech is not ready yet", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Story text is empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}