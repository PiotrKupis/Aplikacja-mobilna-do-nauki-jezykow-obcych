package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultGameWithCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_game_with_card);
        Bundle extras = getIntent().getExtras();
        double time = extras.getDouble("time");
        TextView myAwesomeTextView = (TextView)findViewById(R.id.Resultgamewithcard);
        myAwesomeTextView.setText("Twój czas: +"+ time+ "s");
    }

    public void gotolist(View view) {
        Intent myIntent = new Intent(getBaseContext(), main_screen.class);
        finish();
        startActivity(myIntent);

    }
}