package com.example.projektkompetencyjnyv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GameWithWords extends AppCompatActivity {
    private int userId;
    private Connection con;
    private int listId;
    private ArrayList<Integer> wordsIds;
    private ArrayList<String> wordsEnglish;
    private ArrayList<String> wordsPolish;
    TextView roundCounterView;

    private void setConnection() {
        Intent intent = getIntent();
        userId = intent.getIntExtra(MainActivity.EXTRA_NUMBER, 0);
        //userId = 1;//póki co id jest stałe (brak logowania)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getInt("userID");
            System.out.println("ID użytkownika: " + userId);
            listId = extras.getInt("listID");
        } else {
            listId = 19;
            userId = 26;
        }
        //inicjalizacja połaczenia się z bazą
        ConnectionClass connectionClass = new ConnectionClass();
        con = connectionClass.CONN();
    }

    private int currentWordCounter = 0;
    private int pointsCounter = 0;
    private int roundCounter = 1;
    private int totalRoundsCounter = 0;

    private TextView pointsCounterView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_with_words);

        setConnection();
        TextView txt = findViewById(R.id.word);

        if (savedInstanceState != null) {
            currentWordCounter = savedInstanceState.getInt("currentWordCounter");
            pointsCounter = savedInstanceState.getInt("pointsCounter");
            roundCounter = savedInstanceState.getInt("roundCounter");
            totalRoundsCounter = savedInstanceState.getInt("totalRoundsCounter");
            currentWordCounter = savedInstanceState.getInt("currentWordCounter");
            userId = savedInstanceState.getInt("userId");
            listId = savedInstanceState.getInt("listId");

            wordsPolish = savedInstanceState.getStringArrayList("wordsPolish");
            wordsEnglish = savedInstanceState.getStringArrayList("wordsEnglish");

            wordsIds = savedInstanceState.getIntegerArrayList("wordsIds");
            progressValues = savedInstanceState.getIntegerArrayList("progressValues");
        } else {
            getWordsAndProgressFromDatabase();
        }

        pointsCounterView = findViewById(R.id.pointsCouter);
        pointsCounterView.setText(pointsCounter + "pkt.");
        roundCounterView = findViewById(R.id.roundCounter);
        roundCounterView.setText(roundCounter + "/" + totalRoundsCounter);
        txt.setText(wordsPolish.get(roundCounter - 1));
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
                    totalRoundsCounter++;
                    wordsIds.add(words.getInt("id_word"));
                    wordsEnglish.add(words.getString("word"));
                    wordsPolish.add(words.getString("meaning"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void getWordsAndProgressFromDatabase() {
        wordsPolish = new ArrayList<>();
        wordsEnglish = new ArrayList<>();
        wordsIds = new ArrayList<>();
        progressValues = new ArrayList<>();
        ResultSet queryResult;
        Statement commList;
        try {
            if (con != null) {
                commList = con.createStatement();
                queryResult = commList.executeQuery(
                        "select word, meaning, progress, w.id_word from Progress as p " +
                                "join Word as w " +
                                "on p.id_word = w.id_word " +
                                "and id_user = " + userId +
                                "and w.id_list = " + listId +
                                "and learned = 0 "
                );
                while (queryResult.next()) {
                    totalRoundsCounter++;
                    wordsEnglish.add(queryResult.getString("word"));
                    wordsPolish.add(queryResult.getString("meaning"));
                    progressValues.add(queryResult.getInt("progress"));
                    wordsIds.add(queryResult.getInt("id_word"));
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    private ArrayList<Integer> progressValues;

    public void getProgress() {
        int id_word = wordsIds.get(currentWordCounter);
        progressValues = new ArrayList<>();
        try {
            if (con != null) {
                Statement commList;
                commList = con.createStatement();
                ResultSet progress = commList.executeQuery(
                        "select * from Progress where id_user = " + userId +
                                " and id_word = " + id_word
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
        TextView txt = findViewById(R.id.word);

        TextView isAnswerCorrect = findViewById(R.id.isAnswerCorrect);

        EditText answer = findViewById(R.id.answer);
        String guessStr = answer.getText().toString();
        String answerStr;

        answer.getText().clear();
        answerStr = wordsEnglish.get(currentWordCounter);

        int progress = progressValues.get(currentWordCounter);

        if (answerStr.equals(guessStr)) {
            isAnswerCorrect.setText("Odpowiedź poprawna!");
            progress++;
            pointsCounter++;
            pointsCounterView.setText(pointsCounter + "pkt.");

        } else {
            if (progress > 0) {
                progress--;
            }
            isAnswerCorrect.setText("Błąd! Poprawna odpowiedź: " + wordsEnglish.get(currentWordCounter));
        }
        try {
            updateProgress(progress);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        roundCounterView.setText(roundCounter + "/" + totalRoundsCounter);

        if (roundCounter == totalRoundsCounter) {
            endGameStats(view);
            currentWordCounter = 0;
            roundCounter = 0;
            pointsCounter = 0;
            return;
        }

        roundCounter++;
        currentWordCounter++;
        txt.setText(wordsPolish.get(currentWordCounter));
    }

    private void updateProgress(int progress) throws SQLException {
        if (con != null) {
            Statement commList;
            commList = con.createStatement();
            if (progress < 3) {
                commList.executeUpdate(
                        "update Progress set progress = "
                                + progress +
                                " where id_word = " + wordsIds.get(currentWordCounter).toString()
                );
            } else {
                commList.executeUpdate(
                        "update Progress set progress = "
                                + progress +
                                " where id_word = " + wordsIds.get(currentWordCounter).toString()
                );
                commList.executeUpdate(
                        "update Progress set learned = 1 " +
                                " where id_word = " + wordsIds.get(currentWordCounter).toString()
                );
            }

        }
    }

    public void endGameStats(View view) {
        Intent myIntent = new Intent(getBaseContext(), AfterGameStats.class);
        myIntent.putExtra("key", pointsCounter);
        startActivity(myIntent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("currentWordCounter", currentWordCounter);
        outState.putInt("pointsCounter", pointsCounter);
        outState.putInt("roundCounter", roundCounter);
        outState.putInt("totalRoundsCounter", totalRoundsCounter);
        outState.putInt("userId", userId);
        outState.putInt("listId", listId);

        outState.putStringArrayList("wordsEnglish", wordsEnglish);
        outState.putStringArrayList("wordsPolish", wordsPolish);

        outState.putIntegerArrayList("wordsIds", wordsIds);
        outState.putIntegerArrayList("progressValues", progressValues);
    }
}
