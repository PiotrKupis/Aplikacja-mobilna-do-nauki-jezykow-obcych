package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Words extends AppCompatActivity {

    private static final String TAG = "Words";
    private String listName;
    private String ownerLogin;
    private int ownerId;
    private int userId;
    private ConnectionClass connectionClass;
    private Connection con;
    private CurrentUser currentUser;

    private RecyclerView wordsRecView;
    private ArrayList<String> words;
    private ArrayList<String> meanings;
    private ArrayList<String> sentences;
    private ArrayList<String> sentencesMeanings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = new CurrentUser(getApplicationContext());
        listName = currentUser.getCurrentListName();
        ownerLogin = currentUser.getCurrentListOwner();
        userId = currentUser.getId();

        words = new ArrayList<>();
        meanings = new ArrayList<>();
        sentences = new ArrayList<>();
        sentencesMeanings = new ArrayList<>();

        connectionClass = new ConnectionClass();
        con = connectionClass.CONN();

        initWordLists();

        //jeśli nie należy do użytkownika inny layout bez możliwosci dodawania słów do listy
        if (userId == ownerId) {
            setContentView(R.layout.activity_words_owner);
        } else {
            setContentView(R.layout.activity_words);
        }
        currentUser.setCurrentListOwnerId(ownerId);

        initRecyclerView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        currentUser = new CurrentUser(getApplicationContext());
        listName = currentUser.getCurrentListName();
        ownerLogin = currentUser.getCurrentListOwner();
        userId = currentUser.getId();

        words = new ArrayList<>();
        meanings = new ArrayList<>();
        sentences = new ArrayList<>();
        sentencesMeanings = new ArrayList<>();

        connectionClass = new ConnectionClass();
        con = connectionClass.CONN();

        initWordLists();

        //jeśli nie należy do użytkownika inny layout bez możliwosci dodawania słów do listy
        if (userId == ownerId) {
            setContentView(R.layout.activity_words_owner);
        } else {
            setContentView(R.layout.activity_words);
        }
        currentUser.setCurrentListOwnerId(ownerId);

        initRecyclerView();
    }

    private void initWordLists() {

        ResultSet ownerRS, wordsRS;
        PreparedStatement ownerStmt, wordsStmt;

        try {
            if (con != null) {

                //pobranie id właściciela by znaleźć konkretna listę
                Log.d(TAG, "initWordLists: pobieranie id właściciela listy");
                ownerStmt = con.prepareStatement("" +
                        "select u.id_user as id\n" +
                        "from [User] as u\n" +
                        "where u.login=?");
                ownerStmt.setString(1, ownerLogin);
                ownerRS = ownerStmt.executeQuery();

                if (ownerRS.next()) {
                    ownerId = ownerRS.getInt("id");

                    //pobranie słów z wybranej listy
                    Log.d(TAG, "initWordLists: pobieranie słów z listy");
                    wordsStmt = con.prepareStatement("" +
                            "select w.word as word, w.meaning as meaning, w.example_sentence as sentence, w.example_sentence_pl as sentenceMeaning \n" +
                            "from Word_list as wl\n" +
                            "inner join Word as w on wl.id_word_list=w.id_list\n" +
                            "where wl.owner_id=? and wl.name=?");
                    wordsStmt.setInt(1, ownerId);
                    wordsStmt.setString(2, listName);
                    wordsRS = wordsStmt.executeQuery();

                    while (wordsRS.next()) {
                        words.add(wordsRS.getString("word"));
                        meanings.add(wordsRS.getString("meaning"));
                        sentences.add(wordsRS.getString("sentence"));
                        sentencesMeanings.add(wordsRS.getString("sentenceMeaning"));
                    }
                    ownerRS.close();
                    ownerStmt.close();
                    wordsRS.close();
                    wordsStmt.close();
                }
            } else {
                Toast.makeText(this, "Błąd połączenia z bazą", Toast.LENGTH_LONG).show();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: inicjalizacja recyclerview words");

        wordsRecView = findViewById(R.id.wordsRecView);
        WordsAdapter adapter = new WordsAdapter(this, words, meanings, sentences, sentencesMeanings);
        wordsRecView.setAdapter(adapter);
        wordsRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    //TODO dodać sprawdzenie czy lsita nalezy do użytkownika jeśli nie, nie pokazuj tego przycisku
    public void moveToAddingNewWord(View view) {
        Log.d(TAG, "moveToAddingNewWord: wybrano dodawanie nowego słowa do listy");
        Intent intent = new Intent(this, AddNewWord.class);
        intent.putExtra("listName", listName);
        intent.putExtra("owner", ownerLogin);
        startActivity(intent);
    }
}