package com.example.animalquiz;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_DIFFICULTY = "difficulty";
    public static final String EXTRA_SCORE      = "score";

    private static final int  QUESTIONS_PER_GAME       = 10;
    private static final long CORRECT_DISPLAY_DELAY_MS = 2500;

    private int difficulty;
    private int currentQuestionIndex;
    private int currentScore;
    private int attemptCount;

    private List<Animal> allAnimals;
    private List<Animal> questionList;
    private List<Animal> currentChoices;
    private List<ImageView> choiceViews;

    // State preserved across orientation changes
    private Set<String> eliminatedChoices = new HashSet<>();
    private boolean choicesFrozen  = false;
    private boolean overlayVisible = false;

    private MediaPlayer mediaPlayer;

    private TextView    tvQuestionNumber;
    private TextView    tvScore;
    private TextView    tvAnimalName;
    private ImageButton btnPlay;
    private LinearLayout choiceContainer;
    private FrameLayout  correctOverlay;
    private ImageView    ivCorrectAnimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        difficulty = getIntent().getIntExtra(EXTRA_DIFFICULTY, ScoreManager.DIFFICULTY_EASY);

        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvScore          = findViewById(R.id.tv_score);
        tvAnimalName     = findViewById(R.id.tv_animal_name);
        btnPlay          = findViewById(R.id.btn_play);
        choiceContainer  = findViewById(R.id.choice_container);
        correctOverlay   = findViewById(R.id.correct_overlay);
        ivCorrectAnimal  = findViewById(R.id.iv_correct_animal);

        btnPlay.setOnClickListener(v -> playAnimalSound());

        // Select 10 random questions from the 14 animals
        allAnimals = Animal.getAllAnimals();
        List<Animal> shuffled = new ArrayList<>(allAnimals);
        Collections.shuffle(shuffled);
        questionList = new ArrayList<>(shuffled.subList(0, QUESTIONS_PER_GAME));

        choiceViews = new ArrayList<>();
        currentScore = 0;
        currentQuestionIndex = 0;

        loadQuestion();
    }

    // ─── Question loading ───────────────────────────────────────────────────

    private void loadQuestion() {
        attemptCount     = 0;
        choiceViews      = new ArrayList<>();
        eliminatedChoices.clear();
        choicesFrozen    = false;
        overlayVisible   = false;

        Animal correct = questionList.get(currentQuestionIndex);

        tvQuestionNumber.setText((currentQuestionIndex + 1) + "/" + QUESTIONS_PER_GAME);
        tvScore.setText(getString(R.string.label_score, currentScore));
        tvAnimalName.setText(correct.getName());

        // Build choice list: correct animal + (difficulty-1) random others
        currentChoices = new ArrayList<>();
        currentChoices.add(correct);

        List<Animal> others = new ArrayList<>(allAnimals);
        others.remove(correct);
        Collections.shuffle(others);
        for (int i = 0; i < difficulty - 1; i++) {
            currentChoices.add(others.get(i));
        }
        Collections.shuffle(currentChoices);

        setupChoices();

        // Auto-play animal sound when the question first appears
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                playAnimalSound();
            }
        }, 300);
    }

    private void setupChoices() {
        choiceContainer.removeAllViews();
        choiceViews.clear();

        // Layout: 2 cols for easy(2) or hard(4), 3 cols for normal(3)
        int cols  = (difficulty == ScoreManager.DIFFICULTY_NORMAL) ? 3 : 2;
        int total = currentChoices.size();
        int rows  = (total + cols - 1) / cols;

        for (int row = 0; row < rows; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                if (index < total) {
                    final Animal animal = currentChoices.get(index);
                    final ImageView iv  = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                    params.setMargins(8, 8, 8, 8);
                    iv.setLayoutParams(params);
                    iv.setImageResource(animal.getImageResId());
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    iv.setBackgroundResource(R.drawable.choice_item_bg);
                    if (eliminatedChoices.contains(animal.getName())) {
                        ColorMatrix cm = new ColorMatrix();
                        cm.setSaturation(0);
                        iv.setColorFilter(new ColorMatrixColorFilter(cm));
                        iv.setAlpha(0.4f);
                        iv.setClickable(false);
                    } else if (choicesFrozen) {
                        iv.setClickable(false);
                    } else {
                        iv.setOnClickListener(v -> onChoiceClicked(animal, iv));
                    }
                    choiceViews.add(iv);
                    rowLayout.addView(iv);
                } else {
                    // Filler spacer so the last row keeps equal cell widths
                    View spacer = new View(this);
                    spacer.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                    rowLayout.addView(spacer);
                }
            }
            choiceContainer.addView(rowLayout);
        }
    }

    // ─── Answer handling ────────────────────────────────────────────────────

    private void onChoiceClicked(Animal animal, ImageView iv) {
        Animal correct = questionList.get(currentQuestionIndex);
        attemptCount++;

        if (animal.getName().equals(correct.getName())) {
            handleCorrect();
        } else {
            handleWrong(iv);
        }
    }

    private void handleCorrect() {
        // Freeze all choices immediately
        choicesFrozen = true;
        for (ImageView iv : choiceViews) {
            iv.setClickable(false);
        }
        btnPlay.setEnabled(false);

        // Score: 10 / 7 / 4 / 1 for 1st-4th attempt
        int points;
        if      (attemptCount == 1) points = 10;
        else if (attemptCount == 2) points = 7;
        else if (attemptCount == 3) points = 4;
        else                        points = 1;
        currentScore += points;
        tvScore.setText(getString(R.string.label_score, currentScore));

        // Play correct-answer sound, then show overlay
        playSound(R.raw.sound_correct, () -> {
            Animal correct = questionList.get(currentQuestionIndex);
            ivCorrectAnimal.setImageResource(correct.getImageResId());
            overlayVisible = true;
            correctOverlay.setVisibility(View.VISIBLE);

            // Play animal sound over the overlay
            playSound(correct.getSoundResId(), null);

            // After the display delay, advance to the next question
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isFinishing() || isDestroyed()) return;
                overlayVisible = false;
                correctOverlay.setVisibility(View.GONE);
                currentQuestionIndex++;
                if (currentQuestionIndex >= QUESTIONS_PER_GAME) {
                    Intent intent = new Intent(GameActivity.this, ClearActivity.class);
                    intent.putExtra(EXTRA_SCORE, currentScore);
                    intent.putExtra(EXTRA_DIFFICULTY, difficulty);
                    startActivity(intent);
                    finish();
                } else {
                    btnPlay.setEnabled(true);
                    loadQuestion();
                }
            }, CORRECT_DISPLAY_DELAY_MS);
        });
    }

    private void handleWrong(ImageView iv) {
        playSound(R.raw.sound_wrong, null);

        // Track elimination so it can be restored after rotation
        Animal tapped = currentChoices.get(choiceViews.indexOf(iv));
        eliminatedChoices.add(tapped.getName());

        // Grey out the tapped choice so it can't be re-tapped
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        iv.setColorFilter(new ColorMatrixColorFilter(cm));
        iv.setAlpha(0.4f);
        iv.setClickable(false);
    }

    // ─── Sound playback ─────────────────────────────────────────────────────

    private void playAnimalSound() {
        Animal correct = questionList.get(currentQuestionIndex);
        playSound(correct.getSoundResId(), null);
    }

    private void playSound(int resId, Runnable onComplete) {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            } catch (IllegalStateException ignored) { }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = MediaPlayer.create(this, resId);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> {
                    if (onComplete != null) {
                        new Handler(Looper.getMainLooper()).post(onComplete);
                    }
                });
                mediaPlayer.start();
            } else if (onComplete != null) {
                // Resource could not be loaded (placeholder file) — continue anyway
                new Handler(Looper.getMainLooper()).post(onComplete);
            }
        } catch (Exception e) {
            if (onComplete != null) {
                new Handler(Looper.getMainLooper()).post(onComplete);
            }
        }
    }

    // ─── Orientation handling ────────────────────────────────────────────────

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Re-inflate the layout for the new orientation and rebind all views
        setContentView(R.layout.activity_game);
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvScore          = findViewById(R.id.tv_score);
        tvAnimalName     = findViewById(R.id.tv_animal_name);
        btnPlay          = findViewById(R.id.btn_play);
        choiceContainer  = findViewById(R.id.choice_container);
        correctOverlay   = findViewById(R.id.correct_overlay);
        ivCorrectAnimal  = findViewById(R.id.iv_correct_animal);

        btnPlay.setOnClickListener(v -> playAnimalSound());

        // Restore header state
        Animal correct = questionList.get(currentQuestionIndex);
        tvQuestionNumber.setText((currentQuestionIndex + 1) + "/" + QUESTIONS_PER_GAME);
        tvScore.setText(getString(R.string.label_score, currentScore));
        tvAnimalName.setText(correct.getName());

        // Rebuild choice grid (respects eliminatedChoices and choicesFrozen)
        setupChoices();

        // Restore button and overlay state
        if (choicesFrozen) {
            btnPlay.setEnabled(false);
        }
        if (overlayVisible) {
            ivCorrectAnimal.setImageResource(correct.getImageResId());
            correctOverlay.setVisibility(View.VISIBLE);
        }
    }

    // ─── Lifecycle ──────────────────────────────────────────────────────────

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
            } catch (IllegalStateException ignored) { }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
