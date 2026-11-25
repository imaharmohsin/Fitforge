package com.example.fitforge;

import java.io.Serializable;

public class Exercise implements Serializable {
    public String name;
    public String setsReps;
    public String difficulty;
    public String diffColor;

    public Exercise(String name, String setsReps, String difficulty, String diffColor) {
        this.name       = name;
        this.setsReps   = setsReps;
        this.difficulty = difficulty;
        this.diffColor  = diffColor;
    }
}