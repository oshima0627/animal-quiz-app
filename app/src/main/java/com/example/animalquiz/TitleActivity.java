package com.example.animalquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TitleActivity extends AppCompatActivity {

    private TextView tvHighScoreEasy;
    private TextView tvHighScoreNormal;
    private TextView tvHighScoreHard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        tvHighScoreEasy   = findViewById(R.id.tv_high_score_easy);
        tvHighScoreNormal = findViewById(R.id.tv_high_score_normal);
        tvHighScoreHard   = findViewById(R.id.tv_high_score_hard);

        findViewById(R.id.btn_easy).setOnClickListener(
                v -> startGame(ScoreManager.DIFFICULTY_EASY));
        findViewById(R.id.btn_normal).setOnClickListener(
                v -> startGame(ScoreManager.DIFFICULTY_NORMAL));
        findViewById(R.id.btn_hard).setOnClickListener(
                v -> startGame(ScoreManager.DIFFICULTY_HARD));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHighScores();
    }

    private void updateHighScores() {
        int easy   = ScoreManager.getHighScore(this, ScoreManager.DIFFICULTY_EASY);
        int normal = ScoreManager.getHighScore(this, ScoreManager.DIFFICULTY_NORMAL);
        int hard   = ScoreManager.getHighScore(this, ScoreManager.DIFFICULTY_HARD);

        tvHighScoreEasy.setText(easy   >= 0
                ? getString(R.string.highscore_format, easy)   : getString(R.string.highscore_none));
        tvHighScoreNormal.setText(normal >= 0
                ? getString(R.string.highscore_format, normal) : getString(R.string.highscore_none));
        tvHighScoreHard.setText(hard   >= 0
                ? getString(R.string.highscore_format, hard)   : getString(R.string.highscore_none));
    }

    private void startGame(int difficulty) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_DIFFICULTY, difficulty);
        startActivity(intent);
    }
}
