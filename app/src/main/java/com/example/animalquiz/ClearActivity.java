package com.example.animalquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ClearActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);

        int score      = getIntent().getIntExtra(GameActivity.EXTRA_SCORE, 0);
        int difficulty = getIntent().getIntExtra(
                GameActivity.EXTRA_DIFFICULTY, ScoreManager.DIFFICULTY_EASY);

        TextView tvScore           = findViewById(R.id.tv_score);
        TextView tvHighScoreMsg    = findViewById(R.id.tv_high_score_message);

        tvScore.setText(getString(R.string.label_total_score, score));

        boolean isNewHighScore = ScoreManager.updateHighScore(this, difficulty, score);
        if (isNewHighScore) {
            tvHighScoreMsg.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btn_play_again).setOnClickListener(v -> {
            Intent intent = new Intent(this, TitleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
