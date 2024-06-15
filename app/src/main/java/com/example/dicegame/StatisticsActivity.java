package com.example.dicegame;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity {

    private TextView textViewStatistics;
    private GameDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        textViewStatistics = findViewById(R.id.textViewStatistics);
        dbHelper = new GameDbHelper(this);

        displayStatistics();

        Button buttonStartOver = findViewById(R.id.buttonStartOver);
        buttonStartOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameOver();
            }
        });
    }

    private void displayStatistics() {
        Cursor cursor = dbHelper.getAllPlayers();
        StringBuilder statsBuilder = new StringBuilder();

        while (cursor.moveToNext()) {
            String playerName = cursor.getString(cursor.getColumnIndex(GameDbHelper.KEY_PLAYER_NAME));
            int wins = cursor.getInt(cursor.getColumnIndex(GameDbHelper.KEY_WINS));
            int losses = cursor.getInt(cursor.getColumnIndex(GameDbHelper.KEY_LOSSES));
            int totalPlayed = cursor.getInt(cursor.getColumnIndex(GameDbHelper.KEY_TOTAL_PLAYED));

            statsBuilder.append("Player: ").append(playerName)
                    .append("\nWins: ").append(wins)
                    .append(", Losses: ").append(losses)
                    .append(", Total Played: ").append(totalPlayed)
                    .append("\n\n");
        }

        textViewStatistics.setText(statsBuilder.toString());
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    private void startGameOver() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}