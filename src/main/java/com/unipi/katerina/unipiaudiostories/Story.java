package com.unipi.katerina.unipiaudiostories;

public class Story {

    private String id;
    private String title;
    private String author;
    private String storyText;
    private String year;
    private String imageURL;

    // Empty constructor for Firestore
    public Story() {}

    // Constructor
    public Story(String id, String title, String author, String imageURL) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.imageURL = imageURL;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getAuthor() {

        return author;
    }

    public void setAuthor(String author) {

        this.author = author;
    }

    public String getStoryText() {

        return storyText;
    }

    public void setStoryText(String storyText) {

        this.storyText = storyText;
    }

    public String getYear() {

        return year;
    }

    public void setYear(String year) {

        this.year = year;
    }

    public String getImageURL() {

        return imageURL;
    }

    public void setImageURL(String imageURL) {

        this.imageURL = imageURL;
    }
}