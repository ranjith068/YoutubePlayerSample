package com.videokenplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranjith on 31/5/17.
 */

public class Database extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "videoManager";

    // Contacts table name
    private static final String TABLE_VIDEO = "videos";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "video";
    private static final String KEY_TIME = "time";
    private static final String KEY_TEXT = "speachtext";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setWriteAheadLoggingEnabled(true);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            db.enableWriteAheadLogging();
        } else {


            Log.d("DATABASE", "WAL is not supported on API levels below 11.");

        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_VIDEO_TABLE = "CREATE TABLE " + TABLE_VIDEO + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"+KEY_TEXT + " TEXT,"+ KEY_TIME + " TEXT"
                + ")";
        db.execSQL(CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO);

        // Create tables again
        onCreate(db);
    }


    public void addLocation(VideoModel video) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, video.get_video());
        values.put(KEY_TIME, video.get_time());
        values.put(KEY_TEXT, video.get_speachtext());
        // Inserting Row
        db.insert(TABLE_VIDEO, null, values);
        db.close(); // Closing database connection
    }

    public List<VideoModel> getAllVideos() {
        List<VideoModel> contactList = new ArrayList<VideoModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_VIDEO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                VideoModel contact = new VideoModel();
                contact.set_id(Integer.parseInt(cursor.getString(0)));
                contact.set_video(cursor.getString(1));
                contact.set_time(cursor.getString(2));
                contact.set_speachtext(cursor.getString(3));

                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Getting events Count
    public int getVideoCount() {
        String countQuery = "SELECT  * FROM " + TABLE_VIDEO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
//
//
//    public void deleteAll() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("delete from " + TABLE_LOCATION);
//        db.close();
//    }
}