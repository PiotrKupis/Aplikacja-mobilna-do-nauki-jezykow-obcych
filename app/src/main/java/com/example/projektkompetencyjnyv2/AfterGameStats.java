package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AfterGameStats extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_game_stats);
    }


    public void backToCategories(View view) {
        Intent myIntent = new Intent(getBaseContext(), CoursesFragment.class);
        startActivity(myIntent);
    }
}
