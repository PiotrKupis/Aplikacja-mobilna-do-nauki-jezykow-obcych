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
    private int listId;
    private ArrayList<Integer> wordsIds;
    private ArrayList<String> wordsEnglish;
    private ArrayList<String> wordsPolish;

    private void setConnection() {
        Intent intent = getIntent();
        userId = intent.getIntExtra(MainActivity.EXTRA_NUMBER, 0);
        //userId = 1;//póki co id jest stałe (brak logowania)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getInt("userID");
            System.out.println("ID użytkownika" + userId);
            listId = extras.getInt("listID");
        } else {
            userId = 1;
        }
        //inicjalizacja połaczenia się z bazą
        connectionClass = new ConnectionClass();
        con = connectionClass.CONN();
    }

    private int counter = 0;
    private int pointsCounter = 0;
    private int roundCounter = 1;
    private int id_word;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy);

        setConnection();
        getWords();
        getProgress();

        TextView txt = (TextView) findViewById(R.id.word);
        txt.setText(wordsPolish.get(0));
    }

    private void getWords() {
        wordsPolish = new ArrayList<>();
        wordsEnglish = new ArrayList<>();
        wordsIds = new ArrayList<>();
        ResultSet words;
        Statement commList;
        try {
            if (con != null) {
                commList = con.createStatement();
                words = commList.executeQuery(
                        "select * from Word where id_list = " + listId
                );
                while (words.next()) {
                    wordsIds.add(words.getInt("id_word"));
                    wordsEnglish.add(words.getString("word"));
                    wordsPolish.add(words.getString("meaning"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private ArrayList<Integer> progressValues;

    public void getProgress() {
        //id_word = wordsIds.get(counter);
        progressValues = new ArrayList<>();
        try {
            if (con != null) {
                Statement commList;
                commList = con.createStatement();
                ResultSet progress = commList.executeQuery(
                        "select * from Progress where id_user = " + userId
                        //" and id_word = " + id_word
                );
                while (progress.next()) {

                    progressValues.add(progress.getInt("progress"));
                }
                System.out.println(progress);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void OKBtnClick(View view) {
        TextView txt = (TextView) findViewById(R.id.word);
        EditText answer = (EditText) findViewById(R.id.answer);
        TextView roundCounterView = (TextView) findViewById(R.id.roundCounter);
        TextView pointsCounterView = (TextView) findViewById(R.id.pointsCouter);
        TextView isAnswerCorrect = (TextView) findViewById(R.id.isAnswerCorrect);
        String guessStr = answer.getText().toString();
        String answerStr;
        answer.getText().clear();
        answerStr = wordsEnglish.get(counter);

        int progress = progressValues.get(counter);

        if (answerStr.equals(guessStr)) {
            isAnswerCorrect.setText("Odpowiedź poprawna!");
            progress++;
            pointsCounter++;
            pointsCounterView.setText(pointsCounter + "pkt.");

        } else {
            if (progress > 0) {
                progress--;
            }
            isAnswerCorrect.setText("Błąd! Poprawna odpowiedź: " + wordsEnglish.get(counter));
        }
        roundCounterView.setText(roundCounter + "/10");
        try {
            if (con != null) {
                Statement commList;
                commList = con.createStatement();

                commList.executeUpdate(
                        "update Progress set progress = "
                                + progress +
                                " where id_word = " + wordsIds.get(counter).toString()
                );

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
