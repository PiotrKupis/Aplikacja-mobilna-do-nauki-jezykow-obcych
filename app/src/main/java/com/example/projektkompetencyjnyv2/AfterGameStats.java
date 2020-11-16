package com.example.projektkompetencyjnyv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AfterGameStats extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_game_stats);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int score = extras.getInt("key");
            TextView wynik = (TextView) findViewById(R.id.wynik);
            wynik.setText("" + score + " pkt.");
        }
    }


    public void backToCategories(View view) {
        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(myIntent);
    }
}
