package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PlantsDifficulty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
    }

    public void easyDifficultyBtnOnClick(View view) {
        Intent myIntent = new Intent(getBaseContext(), PlantsEasy.class);
        startActivity(myIntent);
    }

    public void mediumDifficultyBtnOnClick(View view) {
        Intent myIntent = new Intent(getBaseContext(), PlanstMedium.class);
        startActivity(myIntent);
    }
}
