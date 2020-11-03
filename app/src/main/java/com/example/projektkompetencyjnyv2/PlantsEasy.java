package com.example.projektkompetencyjnyv2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlantsEasy extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy);

    }
    int counter = 0;

    @SuppressLint("SetTextI18n")
    public void OKBtnClick(View view) {
        TextView txt = (TextView) findViewById(R.id.word);
        EditText answer = (EditText) findViewById(R.id.answer);
        TextView pointsCounter = (TextView) findViewById(R.id.counter);

        String answerStr = answer.getText().toString();
        String txtStr = txt.getText().toString();

        if(answerStr.equals(txtStr)) {
            counter++;
            pointsCounter.setText(counter + " / 10");
        }



//        TODO: Dodać przejście do kolejnego ekranu albo zmianę tekstu na nowy
    }
}
