package com.unipi.katerina.unipiaudiostories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "StoryDB";
    static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME = "stories";
    static final String COLUMN_ID = "id";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_IMAGE_NAME = "image_name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STORIES_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " TEXT PRIMARY KEY, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_IMAGE_NAME + " TEXT" + ")";
        db.execSQL(CREATE_STORIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Add a story to the database
    public void addStory(String id, String title, String imageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_IMAGE_NAME, imageName);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Get the image name for a given story
    public String getImageName(String storyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_IMAGE_NAME},
                COLUMN_ID + "=?",
                new String[]{storyId},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String imageName = cursor.getString(0);
            cursor.close();
            return imageName;
        }
        return null;
    }
}