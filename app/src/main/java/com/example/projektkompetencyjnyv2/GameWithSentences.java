package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GameWithSentences extends AppCompatActivity {
    private int layoutCounter = 0;

    int clickedButtonsCounter = 0;
    LinearLayout[] allLayouts;
    int[] rowSizes;
    private int listId;
    private Connection con;

    private int sentencesCounter;
    private int roundsCounter;
    private int scoreCounter;
    TextView sentenceTextPolish;
    private ArrayList<String> sentencePolish;
    private ArrayList<String> sentenceEnglish;

    TextView roundScoreSentences;
    TextView roundCounterSentences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_with_sentence);

        Bundle extras = getIntent().getExtras();
        int userId;
        if (extras != null) {
            userId = extras.getInt("userID");
            System.out.println("ID użytkownika" + userId);
            listId = extras.getInt("listID");
        } else {
            userId = 1;
            listId = 19;
        }

        setConnection();
        getSentences();

        TextView sentenceTextEnglish = findViewById(R.id.sentenceTxtEnglish);
        sentenceTextPolish = findViewById(R.id.sentenceTxtPolish);

        initGame();
        generateButtons(sentenceTextEnglish);
    }

    private void initGame() {
        allLayouts = new LinearLayout[3];
        allLayouts[0] = findViewById(R.id.wordsLinearLayoutFirstLine);
        allLayouts[1] = findViewById(R.id.wordsLinearLayoutSecondLine);
        allLayouts[2] = findViewById(R.id.wordsLinearLayoutThirdLine);
        roundCounterSentences = findViewById(R.id.roundCounterSentences);
        roundScoreSentences = findViewById(R.id.pointsCounterSentences);

        roundsCounter = 0;
        scoreCounter = 0;
        roundCounterSentences.setText("0/" + sentencesCounter);

        rowSizes = new int[3];
    }

    private void generateButtons(TextView sentenceTextEnglish) {
        String[] englishWords = sentenceEnglish.get(roundsCounter).split(" ");
        Collections.shuffle(Arrays.asList(englishWords));
        int englishWordsCounter = englishWords.length;
        sentenceTextPolish.setText(sentencePolish.get(roundsCounter));

        rowSizes[0] = Math.min(englishWordsCounter, 4);
        rowSizes[1] = Math.min(englishWordsCounter, 8);
        rowSizes[2] = englishWordsCounter;

        for (int t = 0; t < 12; t += 4) {
            for (int i = t; i < rowSizes[t / 4]; i++) {
                final Button button = new Button(this);
                button.setLayoutParams(new LinearLayout.LayoutParams(265, 150));
                button.setId(i);
                button.setAllCaps(false);
                button.setText(englishWords[i]);
                int finalI = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        button.setBackgroundColor(0);
                        button.setText("");
                        button.setEnabled(false);
                        if (clickedButtonsCounter == 0) sentenceTextEnglish.setText("");
                        sentenceTextEnglish.append(englishWords[finalI] + " ");
                        clickedButtonsCounter++;
                        if (clickedButtonsCounter == englishWordsCounter) {
                            TextView answerCheck = findViewById(R.id.answerCheck);
                            if (sentenceTextEnglish.getText().toString().trim().equals(sentenceEnglish.get(roundsCounter))) {
                                answerCheck.setText("Odpowiedź poprawna!");
                                setScore(1);
                                nextSentence(sentenceTextEnglish);
                            } else {
                                setScore(0);
                                nextSentence(sentenceTextEnglish);
                                answerCheck.setText("Błąd");
                                System.out.println("\n" + sentenceTextEnglish.getText());
                                System.out.println(sentenceEnglish.get(0));
                            }
                        }
                    }
                });
                allLayouts[layoutCounter].addView(button);
            }
            layoutCounter++;
        }
    }

    public void endGameStats() {
        Intent myIntent = new Intent(getBaseContext(), AfterGameStats.class);
        myIntent.putExtra("key", scoreCounter);
        startActivity(myIntent);
    }

    private void nextSentence(TextView sentenceTextEnglish) {
        if (roundsCounter == sentencesCounter) {
            endGameStats();
            return;
        }

        rowSizes[0] = 0;
        rowSizes[1] = 0;
        rowSizes[2] = 0;

        allLayouts[0].removeAllViews();
        allLayouts[1].removeAllViews();
        allLayouts[2].removeAllViews();
        layoutCounter = 0;
        clickedButtonsCounter = 0;
        generateButtons(sentenceTextEnglish);
    }

    private void setScore(int point) {
        roundsCounter++;
        scoreCounter += point;
        roundScoreSentences.setText(scoreCounter + " pkt.");
        roundCounterSentences.setText(roundsCounter + "/" + sentencesCounter);
    }

    private void getSentences() {
        sentencePolish = new ArrayList<>();
        sentenceEnglish = new ArrayList<>();
        ResultSet sentences;
        Statement commList;
        try {
            if (con != null) {
                commList = con.createStatement();
                sentences = commList.executeQuery(
                        "SELECT * FROM word WHERE example_sentence_pl != '-' " +
                                "and id_list = " + listId
                );
                while (sentences.next()) {
                    sentencesCounter++;
                    System.out.println(sentences.getString("example_sentence"));
                    sentenceEnglish.add(sentences.getString("example_sentence"));
                    sentencePolish.add(sentences.getString("example_sentence_pl"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setConnection() {
        ConnectionClass connectionClass = new ConnectionClass();
        con = connectionClass.CONN();
    }
}