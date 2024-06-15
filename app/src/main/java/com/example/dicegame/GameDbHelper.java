package com.example.dicegame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDbHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "gameStats.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_PLAYERS = "player_stats";

    // Player Table Columns
    public static final String KEY_PLAYER_ID = "id";
    public static final String KEY_PLAYER_NAME = "player_name";
    public static final String KEY_WINS = "wins";
    public static final String KEY_LOSSES = "losses";
    public static final String KEY_TOTAL_PLAYED = "total_played";

    public GameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYERS_TABLE = "CREATE TABLE " + TABLE_PLAYERS +
                "(" +
                KEY_PLAYER_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_PLAYER_NAME + " TEXT," +
                KEY_WINS + " INTEGER," +
                KEY_LOSSES + " INTEGER," +
                KEY_TOTAL_PLAYED + " INTEGER" +
                ")";

        db.execSQL(CREATE_PLAYERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);

        // Create tables again
        onCreate(db);
    }

    // Method to add a player record
    public void addPlayerStat(String playerName, int wins, int losses, int totalPlayed) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_NAME, playerName);
        values.put(KEY_WINS, wins);
        values.put(KEY_LOSSES, losses);
        values.put(KEY_TOTAL_PLAYED, totalPlayed);

        // Inserting Row
        db.insert(TABLE_PLAYERS, null, values);
        db.close(); // Closing database connection
    }

    // Method to get all players sorted by newest first
    public Cursor getAllPlayers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM player_stats ORDER BY id DESC", null);
    }
}
