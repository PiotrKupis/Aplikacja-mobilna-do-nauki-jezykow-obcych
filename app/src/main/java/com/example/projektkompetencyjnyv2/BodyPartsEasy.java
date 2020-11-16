package com.example.projektkompetencyjnyv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class BodyPartsEasy extends AppCompatActivity {
    private int userId;
    private ConnectionClass connectionClass;
    private Connection con;

    private ArrayList<String> wordsEnglish;
    private ArrayList<String> wordsPolish;

    private void setConnection() {
        Intent intent = getIntent();
        userId = intent.getIntExtra(MainActivity.EXTRA_NUMBER,0);
        userId = 1;//póki co id jest stałe (brak logowania)

        //inicjalizacja połaczenia się z bazą
        connectionClass = new ConnectionClass();
        con = connectionClass.CONN();
    }

    private int counter = 0;
    private int pointsCounter = 0;
    private int roundCounter = 1;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy);

        setConnection();

        getWords();

        TextView txt = (TextView) findViewById(R.id.word);
        txt.setText(wordsPolish.get(0));
    }

    private void getWords() {
        wordsPolish = new ArrayList<>();
        wordsEnglish = new ArrayList<>();
        ResultSet words;
        Statement commList;
        try {
            if(con != null) {
                commList = con.createStatement();
                words = commList.executeQuery(
                        "select * from Word"
                );
                while(words.next()) {
                    wordsEnglish.add(words.getString("word"));
                    wordsPolish.add(words.getString("meaning"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void OKBtnClick(View view) throws SQLException, ClassNotFoundException {
        TextView txt = (TextView) findViewById(R.id.word);
        EditText answer = (EditText) findViewById(R.id.answer);
        TextView roundCounterView = (TextView) findViewById(R.id.roundCounter);
        TextView pointsCounterView = (TextView) findViewById(R.id.pointsCouter);
        TextView isAnswerCorrect = (TextView) findViewById(R.id.isAnswerCorrect);
        //TODO: pobieranie słów z bazy danych
        String guessStr = answer.getText().toString();
        String answerStr;
        answer.getText().clear();
        answerStr = wordsEnglish.get(counter);

        if(answerStr.equals(guessStr)) {
            isAnswerCorrect.setText("Odpowiedź poprawna!");
            pointsCounter++;
            pointsCounterView.setText(pointsCounter + "pkt.");
        } else {
            isAnswerCorrect.setText("Błąd! Poprawna odpowiedź: " + wordsEnglish.get(counter));
        }
        roundCounterView.setText(roundCounter + "/10");

        if (roundCounter == 11) {
            endGameStats(view);
            counter = 0;
            roundCounter = 0;
            pointsCounter = 0;
            return;
        }
        
        roundCounter++;
        counter++;
        txt.setText(wordsPolish.get(counter));
    }

    public void endGameStats(View view) {
        Intent myIntent = new Intent(getBaseContext(), AfterGameStats.class);
        myIntent.putExtra("key", pointsCounter);
        startActivity(myIntent);
    }
}
