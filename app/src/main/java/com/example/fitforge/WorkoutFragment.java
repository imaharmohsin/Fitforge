package com.example.fitforge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkoutFragment extends Fragment {

    CardView cardChest, cardBack, cardLegs,
            cardShoulders, cardArms, cardCardio;
    EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardChest     = view.findViewById(R.id.cardChest);
        cardBack      = view.findViewById(R.id.cardBack);
        cardLegs      = view.findViewById(R.id.cardLegs);
        cardShoulders = view.findViewById(R.id.cardShoulders);
        cardArms      = view.findViewById(R.id.cardArms);
        cardCardio    = view.findViewById(R.id.cardCardio);
        etSearch      = view.findViewById(R.id.etSearch);


        cardChest.setOnClickListener(v -> openDetail(
                "Chest", "12 EXERCISES", R.drawable.chest_image,
                getChestExercises()
        ));


        cardBack.setOnClickListener(v -> openDetail(
                "Back", "12 EXERCISES", R.drawable.back_image,
                getBackExercises()
        ));


        cardLegs.setOnClickListener(v -> openDetail(
                "Legs", "12 EXERCISES", R.drawable.leg_image,
                getLegExercises()
        ));


        cardShoulders.setOnClickListener(v -> openDetail(
                "Shoulders", "12 EXERCISES", R.drawable.shoulder_image,
                getShoulderExercises()
        ));


        cardArms.setOnClickListener(v -> openDetail(
                "Arms", "12 EXERCISES", R.drawable.arms_image,
                getArmExercises()
        ));


        cardCardio.setOnClickListener(v -> openDetail(
                "Cardio", "12 EXERCISES", R.drawable.cardio_image,
                getCardioExercises()
        ));


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCards(s.toString().toLowerCase().trim());
            }
        });
    }


    private void openDetail(String name, String count, int imageRes,
                            ArrayList<Exercise> exercises) {
        Intent intent = new Intent(requireActivity(), WorkoutDetail.class);
        intent.putExtra("CATEGORY_NAME",  name);
        intent.putExtra("EXERCISE_COUNT", count);
        intent.putExtra("IMAGE_RES",      imageRes);
        intent.putExtra("EXERCISES",      exercises);
        startActivity(intent);
    }



    private ArrayList<Exercise> getChestExercises() {
        ArrayList<Exercise> list = new ArrayList<>();
        list.add(new Exercise("Bench Press",              "4 sets × 10 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Push Ups",                 "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Incline Dumbbell Press",   "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Cable Flyes",             "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Dips",                    "4 sets × 10 reps", "Hard",   "#EF5350"));
        list.add(new Exercise("Chest Flyes",             "4 sets × 12 reps", "Easy",   "#66BB6A"));

        list.add(new Exercise("Decline Bench Press",      "4 sets × 10 reps", "Hard",   "#EF5350"));
        list.add(new Exercise("Incline Push Ups",         "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Decline Push Ups",         "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Pec Deck Machine",         "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Dumbbell Pullover",        "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Single Arm Cable Fly",     "3 sets × 12 reps", "Medium", "#FFA726"));
        return list;
    }

    private ArrayList<Exercise> getBackExercises() {
        ArrayList<Exercise> list = new ArrayList<>();
        list.add(new Exercise("Pull Ups",           "4 sets × 8 reps",  "Hard",   "#EF5350"));
        list.add(new Exercise("Bent Over Row",      "4 sets × 10 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Lat Pull down",       "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Seated Cable Row",   "3 sets × 12 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Deadlift",           "4 sets × 6 reps",  "Hard",   "#EF5350"));
        list.add(new Exercise("Face Pulls",         "3 sets × 15 reps", "Easy",   "#66BB6A"));

        list.add(new Exercise("T Bar Row",          "4 sets × 10 reps", "Hard",   "#EF5350"));
        list.add(new Exercise("Single Arm Dumbbell Row", "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Straight Arm Pull down",   "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Chest Supported Row",     "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Wide Grip Lat Pull down",  "4 sets × 10 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Machine Row",             "3 sets × 12 reps", "Easy",   "#66BB6A"));
        return list;
    }

    private ArrayList<Exercise> getLegExercises() {
        ArrayList<Exercise> list = new ArrayList<>();
        list.add(new Exercise("Squats",                "4 sets × 10 reps", "Hard",   "#EF5350"));
        list.add(new Exercise("Leg Press",             "4 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Lunges",                "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Leg Curl",              "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Calf Raises",           "4 sets × 20 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Romanian Deadlift",     "3 sets × 10 reps", "Hard",   "#EF5350"));

        list.add(new Exercise("Leg Extension",         "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Bulgarian Split Squat", "3 sets × 10 reps", "Hard",   "#EF5350"));
        list.add(new Exercise("Hack Squat",            "4 sets × 10 reps", "Hard",   "#EF5350"));
        list.add(new Exercise("Walking Lunges",        "3 sets × 20 steps","Medium", "#FFA726"));
        list.add(new Exercise("Seated Calf Raise",     "4 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Goblet Squat",          "3 sets × 12 reps", "Medium", "#FFA726"));
        return list;
    }

    private ArrayList<Exercise> getShoulderExercises() {
        ArrayList<Exercise> list = new ArrayList<>();
        list.add(new Exercise("Overhead Press",         "4 sets × 10 reps", "Hard",   "#EF5350"));
        list.add(new Exercise("Lateral Raises",         "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Front Raises",           "3 sets × 12 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Arnold Press",           "4 sets × 10 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Rear Delt Flyes",        "3 sets × 15 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Shrugs",                 "4 sets × 12 reps", "Easy",   "#66BB6A"));

        list.add(new Exercise("Seated Dumbbell Press",  "4 sets × 10 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Upright Row",            "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Cable Lateral Raise",    "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Reverse Pec Deck",       "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Machine Shoulder Press", "4 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Cable Front Raise",      "3 sets × 12 reps", "Easy",   "#66BB6A"));
        return list;
    }

    private ArrayList<Exercise> getArmExercises() {
        ArrayList<Exercise> list = new ArrayList<>();
        list.add(new Exercise("Bicep Curls",            "4 sets × 12 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Tricep Pushdown",        "4 sets × 12 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Hammer Curls",           "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Skull Crushers",         "3 sets × 10 reps", "Hard",   "#EF5350"));
        list.add(new Exercise("Concentration Curl",     "3 sets × 15 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Overhead Tricep",        "3 sets × 12 reps", "Medium", "#FFA726"));

        list.add(new Exercise("Preacher Curl",          "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Cable Curl",             "3 sets × 15 reps", "Easy",   "#66BB6A"));
        list.add(new Exercise("Reverse Curl",           "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Close Grip Bench Press", "4 sets × 10 reps", "Hard",   "#EF5350"));
        list.add(new Exercise("Tricep Dips",            "3 sets × 12 reps", "Medium", "#FFA726"));
        list.add(new Exercise("Rope Pushdown",          "3 sets × 15 reps", "Easy",   "#66BB6A"));
        return list;
    }

    private ArrayList<Exercise> getCardioExercises() {
        ArrayList<Exercise> list = new ArrayList<>();
        list.add(new Exercise("Treadmill Run",      "1 set × 20 mins", "Medium", "#FFA726"));
        list.add(new Exercise("Jump Rope",          "5 sets × 3 mins", "Hard",   "#EF5350"));
        list.add(new Exercise("Burpees",            "4 sets × 15 reps","Hard",   "#EF5350"));
        list.add(new Exercise("Cycling",            "1 set × 30 mins", "Easy",   "#66BB6A"));
        list.add(new Exercise("Mountain Climbers",  "4 sets × 20 reps","Medium", "#FFA726"));
        list.add(new Exercise("Box Jumps",          "4 sets × 10 reps","Hard",   "#EF5350"));

        list.add(new Exercise("High Knees",         "4 sets × 30 secs","Medium", "#FFA726"));
        list.add(new Exercise("Jumping Jacks",      "4 sets × 50 reps","Easy",   "#66BB6A"));
        list.add(new Exercise("Stair Climber",      "1 set × 20 mins", "Medium", "#FFA726"));
        list.add(new Exercise("Rowing Machine",     "1 set × 15 mins", "Medium", "#FFA726"));
        list.add(new Exercise("Sprint Intervals",   "6 sets × 30 secs","Hard",   "#EF5350"));
        list.add(new Exercise("Battle Ropes",       "4 sets × 45 secs","Hard",   "#EF5350"));
        return list;
    }


    private void filterCards(String query) {
        cardChest.setVisibility("chest".contains(query)         ? View.VISIBLE : View.GONE);
        cardBack.setVisibility("back".contains(query)           ? View.VISIBLE : View.GONE);
        cardLegs.setVisibility("legs".contains(query)           ? View.VISIBLE : View.GONE);
        cardShoulders.setVisibility("shoulders".contains(query) ? View.VISIBLE : View.GONE);
        cardArms.setVisibility("arms".contains(query)           ? View.VISIBLE : View.GONE);
        cardCardio.setVisibility("cardio".contains(query)       ? View.VISIBLE : View.GONE);
    }
}