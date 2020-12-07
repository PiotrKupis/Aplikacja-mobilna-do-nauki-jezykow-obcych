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

public class GameWithSentence extends AppCompatActivity {
    //    private ArrayList<LinearLayout> allLayouts;
    private int layoutCounter = 0;

    int clickedButtonsCounter = 0;

    private int userId;
    private int listId;
    private ConnectionClass connectionClass;
    private Connection con;

    private ArrayList<String> sentencePolish;
    private ArrayList<String> sentenceEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_with_sentence);

        Bundle extras = getIntent().getExtras();
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

        TextView sentenceTextEnglish = (TextView) findViewById(R.id.sentenceTxtEnglish);
        TextView sentenceTextPolish = (TextView) findViewById(R.id.sentenceTxtPolish);
        sentenceTextPolish.setText(sentencePolish.get(0));

        generateButtons(sentenceTextEnglish);

//        String[] englishWords = sentenceEnglish.get(0).split(" ");
//        int englishWordsCounter = englishWords.length;
//
//        int firstRow = Math.min(englishWordsCounter, 4);
//        int secondRow = Math.min(englishWordsCounter, 8);

//        for (int i = 0; i < firstRow; i++) {
//            final Button button = new Button(this);
//            button.setLayoutParams(new LinearLayout.LayoutParams(265, 150));
//            button.setId(i);
//            button.setText(englishWords[i]);
//            int finalI = i;
//            button.setOnClickListener(view -> sentenceTextEnglish.append(englishWords[finalI] + " "));
//            layoutFirstLine.addView(button);
//        }
//        for (int i = 4; i < secondRow; i++) {
//            final Button button = new Button(this);
//            button.setLayoutParams(new LinearLayout.LayoutParams(265, 150));
//            button.setId(i);
//            button.setText(englishWords[i]);
//            int finalI = i;
//            button.setOnClickListener(view -> sentenceTextEnglish.append(englishWords[finalI] + " "));
//            layoutSecondLine.addView(button);
//        }
//        for (int i = 8; i < englishWordsCounter; i++) {
//            final Button button = new Button(this);
//            button.setLayoutParams(new LinearLayout.LayoutParams(265, 150));
//            button.setId(i);
//            button.setText(englishWords[i]);
//            int FinalI = i;
//            button.setOnClickListener(view -> sentenceTextEnglish.append(englishWords[FinalI] + " "));
//            layoutSecondLine.addView(button);
//
//        }
    }

    private void generateButtons(TextView sentenceTextEnglish) {
        String[] englishWords = sentenceEnglish.get(0).split(" ");
        int englishWordsCounter = englishWords.length;

        LinearLayout[] allLayouts = new LinearLayout[3];
        allLayouts[0] = findViewById(R.id.wordsLinearLayoutFirstLine);
        allLayouts[1] = findViewById(R.id.wordsLinearLayoutSecondLine);
        allLayouts[2] = findViewById(R.id.wordsLinearLayoutThirdLine);

        int[] rowSizes = new int[3];
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
                        sentenceTextEnglish.append(englishWords[finalI] + " ");
                        clickedButtonsCounter++;
                        if (clickedButtonsCounter == englishWordsCounter) {
                            TextView answerCheck = findViewById(R.id.answerCheck);
                            if (sentenceTextEnglish.getText().toString().trim().equals(sentenceEnglish.get(0))) {
                                answerCheck.setText("Odpowiedź poprawna!");
                            } else {
                                answerCheck.setText("Błąd");
                                System.out.println("\n" + sentenceTextEnglish.getText());
                                System.out.println(sentenceEnglish.get(0));
                            }
                        }
                    }
                });
                //button.setOnClickListener(view -> sentenceTextEnglish.append(englishWords[finalI] + " "));
                allLayouts[layoutCounter].addView(button);
            }
            layoutCounter++;
        }
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
        Intent intent = getIntent();
        userId = intent.getIntExtra(MainActivity.EXTRA_NUMBER, 0);

        userId = 1;//póki co id jest stałe (brak logowania)

        //inicjalizacja połaczenia się z bazą
        connectionClass = new ConnectionClass();
        con = connectionClass.CONN();
    }
}