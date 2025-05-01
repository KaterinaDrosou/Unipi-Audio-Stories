package com.unipi.katerina.unipiaudiostories;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<Story> stories;
    private OnStoryClickListener listener;
    private boolean isFavoriteButtonVisible = false;
    private Context context;

    public interface OnStoryClickListener {
        void onStoryClick(Story story);
    }

    public StoryAdapter(List<Story> stories, OnStoryClickListener listener, Context context) {
        this.stories = stories;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoryViewHolder holder, int position) {
        Story story = stories.get(position);

        // Set the translated title
        String translatedTitle = getTranslatedTitle(story.getTitle());
        holder.titleText.setText(translatedTitle);

        holder.authorText.setText(story.getAuthor());

        loadStoryImage(holder, story);

        holder.itemView.setOnClickListener(v -> {
            v.setEnabled(false);
            listener.onStoryClick(story);
            v.postDelayed(() -> v.setEnabled(true), 500);
        });

        holder.favoriteButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                toggleFavorite(currentUser.getUid(), story.getId(), holder.favoriteButton);
            } else {
                Toast.makeText(holder.itemView.getContext(), "Please log in to add favorites", Toast.LENGTH_SHORT).show();
            }
        });

        if (isFavoriteButtonVisible) {
            holder.favoriteButton.setVisibility(View.VISIBLE);
            checkFavoriteStatus(story.getId(), holder.favoriteButton);
        } else {
            holder.favoriteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView storyImage;
        TextView titleText;
        TextView authorText;
        ImageView favoriteButton;

        public StoryViewHolder(View itemView) {
            super(itemView);
            storyImage = itemView.findViewById(R.id.storyImage);
            titleText = itemView.findViewById(R.id.storyTitle);
            authorText = itemView.findViewById(R.id.storyAuthor);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }

    private String getTranslatedTitle(String originalTitle) {
        int resId = context.getResources().getIdentifier(originalTitle, "string", context.getPackageName());
        if (resId != 0) {
            return context.getString(resId);
        }
        return originalTitle;
    }

    private void loadStoryImage(StoryViewHolder holder, Story story) {
        String imageName = story.getImageURL();
        if (imageName != null && !imageName.isEmpty()) {
            int imageResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            holder.storyImage.setImageResource(imageResourceId != 0 ? imageResourceId : R.drawable.default_image);
        } else {
            holder.storyImage.setImageResource(R.drawable.default_image);
        }
    }

    private void toggleFavorite(String userId, String storyId, ImageView favoriteButton) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> favorites = (List<String>) documentSnapshot.get("favorites");
                if (favorites == null) {
                    favorites = new ArrayList<>();
                }

                if (favorites.contains(storyId)) {
                    // If the story is already in the favorites, remove it
                    favorites.remove(storyId);
                    favoriteButton.setImageResource(R.drawable.favorite_icon); // Change to an empty heart icon
                } else {
                    // If the story is not in the favorites, add it
                    favorites.add(storyId);
                    favoriteButton.setImageResource(R.drawable.filled_favorite_icon); // Change to a filled heart icon
                }
                // Update the favorites array in Firestore
                userRef.update("favorites", favorites)
                        .addOnSuccessListener(aVoid -> {
                            // Optionally, show a message
                            Toast.makeText(favoriteButton.getContext(), "Favorites updated", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(favoriteButton.getContext(), "Failed to update favorites", Toast.LENGTH_SHORT).show();
                        });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(favoriteButton.getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkFavoriteStatus(String storyId, ImageView favoriteButton) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                List<String> favorites = (List<String>) documentSnapshot.get("favorites");
                favoriteButton.setImageResource((favorites != null && favorites.contains(storyId)) ?
                        R.drawable.filled_favorite_icon : R.drawable.favorite_icon);
            });
        }
    }

    public void setFavoriteButtonVisible(boolean isVisible) {
        this.isFavoriteButtonVisible = isVisible;
        notifyDataSetChanged();
    }
}