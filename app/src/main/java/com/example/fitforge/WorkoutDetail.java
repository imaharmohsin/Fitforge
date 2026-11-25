package com.example.fitforge;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class WorkoutDetail extends AppCompatActivity {

    int[] nameIds = {
            R.id.tvName1, R.id.tvName2, R.id.tvName3, R.id.tvName4,
            R.id.tvName5, R.id.tvName6, R.id.tvName7, R.id.tvName8,
            R.id.tvName9, R.id.tvName10, R.id.tvName11, R.id.tvName12
    };
    int[] setsIds = {
            R.id.tvSets1, R.id.tvSets2, R.id.tvSets3, R.id.tvSets4,
            R.id.tvSets5, R.id.tvSets6, R.id.tvSets7, R.id.tvSets8,
            R.id.tvSets9, R.id.tvSets10, R.id.tvSets11, R.id.tvSets12
    };
    int[] diffIds = {
            R.id.tvDiff1, R.id.tvDiff2, R.id.tvDiff3, R.id.tvDiff4,
            R.id.tvDiff5, R.id.tvDiff6, R.id.tvDiff7, R.id.tvDiff8,
            R.id.tvDiff9, R.id.tvDiff10, R.id.tvDiff11, R.id.tvDiff12
    };
    int[] cardIds = {
            R.id.cardBenchPress, R.id.cardPushUps, R.id.cardInclinePress,
            R.id.cardCableFlyes, R.id.cardDips, R.id.cardChestFlyes,
            R.id.cardPecDeck, R.id.cardDeclinePress, R.id.cardWidePushUps,
            R.id.cardPullover, R.id.cardCloseGrip, R.id.cardLandminePress
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        ImageView   imgHero         = findViewById(R.id.imgHero);
        TextView    tvCategoryName  = findViewById(R.id.tvCategoryName);
        TextView    tvExerciseCount = findViewById(R.id.tvExerciseCount);
        ImageButton btnBack         = findViewById(R.id.btnBack);

        String categoryName  = getIntent().getStringExtra("CATEGORY_NAME");
        String exerciseCount = getIntent().getStringExtra("EXERCISE_COUNT");
        int    imageRes      = getIntent().getIntExtra("IMAGE_RES", R.drawable.chest_image);
        ArrayList<Exercise> exercises =
                (ArrayList<Exercise>) getIntent().getSerializableExtra("EXERCISES");

        imgHero.setImageResource(imageRes);
        tvCategoryName.setText(categoryName);
        tvExerciseCount.setText(exerciseCount);

        if (exercises != null) {
            for (int i = 0; i < 12; i++) {
                if (i < exercises.size()) {

                    findViewById(cardIds[i]).setVisibility(View.VISIBLE);

                    Exercise ex = exercises.get(i);

                    TextView tvName = findViewById(nameIds[i]);
                    tvName.setText(ex.name);

                    TextView tvSets = findViewById(setsIds[i]);
                    tvSets.setText(ex.setsReps);

                    TextView tvDiff = findViewById(diffIds[i]);
                    tvDiff.setText(ex.difficulty);
                    tvDiff.setTextColor(Color.parseColor(ex.diffColor));
                } else {

                    findViewById(cardIds[i]).setVisibility(View.GONE);
                }
            }
        }

        btnBack.setOnClickListener(v -> finish());
    }
}