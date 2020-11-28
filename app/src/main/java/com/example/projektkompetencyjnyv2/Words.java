package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Words extends AppCompatActivity {

    private static final String TAG = "Words";
    private String listName;
    private String ownerLogin;
    private ConnectionClass connectionClass;
    private Connection con;

    private RecyclerView wordsRecView;
    private ArrayList<String> words;
    private ArrayList<String> meanings;
    private ArrayList<String> sentences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        TextView listIdTxt = findViewById(R.id.listIdTxt);

        Intent intent = getIntent();
        listName = intent.getStringExtra(MainActivity.EXTRA_TEXT);
        ownerLogin = intent.getStringExtra(MainActivity.EXTRA_TEXT2);
        listIdTxt.setText(listName + "\n" + ownerLogin);

        words = new ArrayList<>();
        meanings = new ArrayList<>();
        sentences = new ArrayList<>();

        //inicjalizacja połaczenia się z bazą
        connectionClass = new ConnectionClass();
        con = connectionClass.CONN();

        //pobieranie z bazy
        initWordLists();
        //tworzenie listy
        initRecyclerView();
    }


    private void initWordLists() {

        int ownerId;
        ResultSet ownerRS, wordsRS;
        Statement commOwner, commWords;

        try {
            if (con != null) {

                //pobranie id właściciela by znaleźć konkretna listę
                Log.d(TAG, "initWordLists: pobieranie id właściciela");
                commOwner = con.createStatement();
                Log.d(TAG, "initWordLists: m" + ownerLogin + "m");
                ownerRS = commOwner.executeQuery(
                        "select u.id_user as id\n" +
                                "from [User] as u\n" +
                                "where u.login='" + ownerLogin + "'");

                if (ownerRS.next()) {
                    ownerId = ownerRS.getInt("id");

                    //pobranie słów z wybranej listy
                    Log.d(TAG, "initWordLists: pobieranie słów");
                    commWords = con.createStatement();
                    wordsRS = commWords.executeQuery(
                            "select w.word as word, w.meaning as meaning, w.example_sentence as sentence\n" +
                                    "from Word_list as wl\n" +
                                    "inner join Word as w on wl.id_word_list=w.id_list\n" +
                                    "where wl.owner_id=" + ownerId + " and wl.name='" + listName + "'");

                    while (wordsRS.next()) {

                        Log.d(TAG, "initWordLists: dodawanie słów");
                        words.add(wordsRS.getString("word"));
                        meanings.add(wordsRS.getString("meaning"));
                        sentences.add(wordsRS.getString("sentence"));

                    }
                }
            } else {
                Toast.makeText(this, "Błąd połączenia z bazą", Toast.LENGTH_LONG).show();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        wordsRecView = findViewById(R.id.wordsRecView);
        WordsAdapter adapter = new WordsAdapter(this, words, meanings, sentences);
        wordsRecView.setAdapter(adapter);
        wordsRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    //TODO dodać sprawdzenie czy lsita nalezy do użytkownika jeśli nie, nie pokazuj tego przycisku
    public void moveToAddingNewWord(View view) {
        Log.d(TAG, "moveToAddingNewWord: kliknięto dodawanie słowa");
        Intent intent = new Intent(this, AddNewWord.class);
        intent.putExtra("listName", listName);
        intent.putExtra("owner", ownerLogin);
        startActivity(intent);
    }
}