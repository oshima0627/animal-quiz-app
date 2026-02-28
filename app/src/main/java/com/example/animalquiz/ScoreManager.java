package com.example.animalquiz;

import android.content.Context;
import android.content.SharedPreferences;

public class ScoreManager {

    public static final int DIFFICULTY_EASY   = 2;
    public static final int DIFFICULTY_NORMAL = 3;
    public static final int DIFFICULTY_HARD   = 4;

    private static final String PREF_NAME    = "animal_quiz_prefs";
    private static final String KEY_EASY     = "highscore_easy";
    private static final String KEY_NORMAL   = "highscore_normal";
    private static final String KEY_HARD     = "highscore_hard";

    private static String getKey(int difficulty) {
        switch (difficulty) {
            case DIFFICULTY_EASY:   return KEY_EASY;
            case DIFFICULTY_NORMAL: return KEY_NORMAL;
            case DIFFICULTY_HARD:   return KEY_HARD;
            default:                return KEY_EASY;
        }
    }

    /** Returns the stored high score, or -1 if none recorded yet. */
    public static int getHighScore(Context context, int difficulty) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(getKey(difficulty), -1);
    }

    /**
     * Updates the high score if the given score is higher.
     *
     * @return true if a new high score was recorded.
     */
    public static boolean updateHighScore(Context context, int difficulty, int score) {
        int current = getHighScore(context, difficulty);
        if (score > current) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                   .edit()
                   .putInt(getKey(difficulty), score)
                   .apply();
            return true;
        }
        return false;
    }
}
