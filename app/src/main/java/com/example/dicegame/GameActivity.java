package com.example.dicegame;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private TextView textViewDiceResult, textViewScore;
    private int score = 0, wins = 0, losses = 0, totalPlayed = 0;
    private Random random = new Random();
    private String playerName;
    private GameDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        textViewDiceResult = findViewById(R.id.textViewDiceResult);
        textViewScore = findViewById(R.id.textViewScore);
        Button buttonOdd = findViewById(R.id.buttonOdd);
        Button buttonEven = findViewById(R.id.buttonEven);
        Button buttonSmall = findViewById(R.id.buttonSmall);
        Button buttonBig = findViewById(R.id.buttonBig);
        Button buttonEnd = findViewById(R.id.buttonEnd);

        // Initialize database helper
        dbHelper = new GameDbHelper(this);

        // Get the player name from Intent
        playerName = getIntent().getStringExtra("PLAYER_NAME");

        buttonOdd.setOnClickListener(v -> checkResult("Odd"));
        buttonEven.setOnClickListener(v -> checkResult("Even"));
        buttonSmall.setOnClickListener(v -> checkResult("Small"));
        buttonBig.setOnClickListener(v -> checkResult("Big"));
        buttonEnd.setOnClickListener(v -> endGame());
    }

    private void checkResult(String choice) {
        int dice1 = random.nextInt(6) + 1;
        int dice2 = random.nextInt(6) + 1;
        int sum = dice1 + dice2;
        totalPlayed++;

        // Determine if the sum is small or big, and odd or even
        String size = sum <= 6 ? "Small" : "Big";
        String parity = sum % 2 == 0 ? "Even" : "Odd";
        String resultSummary = size + " " + parity;

        textViewDiceResult.setText("Dice Result: " + sum + "\n" + resultSummary);

        boolean isOdd = sum % 2 != 0;
        boolean isSmall = sum <= 6;

        if ((choice.equals("Odd") && isOdd) ||
                (choice.equals("Even") && !isOdd) ||
                (choice.equals("Small") && isSmall) ||
                (choice.equals("Big") && !isSmall)) {
            score++;
            wins++;
            textViewScore.setText("Score: " + score);
        } else {
            losses++;
        }
    }

    private void endGame() {
        // Save statistics to the database
        dbHelper.addPlayerStat(playerName, wins, losses, totalPlayed);

        // Inflate the popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_statistics, null);

        // Create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Load fade-in animation
        Animation fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        popupView.startAnimation(fadeInAnim);

        // Show the popup window
        popupWindow.showAtLocation(findViewById(R.id.mainLayout), Gravity.TOP, 0, 0);

        // Set statistics in the popup
        TextView textViewPopupStats = popupView.findViewById(R.id.textViewPopupStats);
        String stats = "Wins: " + wins + "\nLosses: " + losses + "\nTotal Played: " + totalPlayed;
        textViewPopupStats.setText(stats);

        Button buttonShowStatistic = popupView.findViewById(R.id.buttonShowStatistic);
        buttonShowStatistic.setOnClickListener(v -> {
            // Load zoom-in animation
            Animation zoomInAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
            popupView.startAnimation(zoomInAnim);

            // Wait for animation to complete before dismissing
            zoomInAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Animation started
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Dismiss popup after animation ends
                    popupWindow.dismiss();
                    showStatistics();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Animation repeats
                }
            });
        });
    }

    private void showStatistics() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
