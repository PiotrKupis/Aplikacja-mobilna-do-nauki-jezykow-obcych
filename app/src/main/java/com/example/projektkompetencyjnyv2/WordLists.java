package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WordLists extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "WordLists";

    private int userId;
    private CurrentUser currentUser;
    private ConnectionClass connectionClass;
    private Connection con;

    private RecyclerView listsRecView;
    private ArrayList<String> listNames;
    private ArrayList<Integer> difficultyLevels;
    private ArrayList<Integer> wordQuantities;
    private ArrayList<Integer> learnedQuantities;
    private ArrayList<String> owners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_lists);

        currentUser = new CurrentUser(getApplicationContext());
        userId = currentUser.getId();

        listNames = new ArrayList<>();
        difficultyLevels = new ArrayList<>();
        wordQuantities = new ArrayList<>();
        learnedQuantities = new ArrayList<>();
        owners = new ArrayList<>();

        //inicjalizacja połaczenia się z bazą
        connectionClass = new ConnectionClass();
        con = connectionClass.CONN();

        initWordLists();
        initRecyclerView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_word_lists);

        currentUser = new CurrentUser(getApplicationContext());
        userId = currentUser.getId();

        listNames = new ArrayList<>();
        difficultyLevels = new ArrayList<>();
        wordQuantities = new ArrayList<>();
        learnedQuantities = new ArrayList<>();
        owners = new ArrayList<>();

        //inicjalizacja połaczenia się z bazą
        connectionClass = new ConnectionClass();
        con = connectionClass.CONN();

        initWordLists();
        initRecyclerView();
    }

    private void initWordLists() {

        Log.d(TAG, "initWordLists: inicializacja wpisów");
        int listId, ownerId;
        ResultSet listsRS = null, ownerRS = null, wordsRS = null, learnedRS = null;
        PreparedStatement listsStmt, ownerStmt, wordsStmt, learnedStmt;

        try {
            if (con != null) {

                //pobranie informacji o listach związanych z użytkownikiem
                listsStmt = con.prepareStatement(
                        "select name, difficulty_level, owner_id, id_wordList\n" +
                                "from Word_list as wl inner join [User_WordList] as uwl \n" +
                                "\ton wl.id_word_list=uwl.id_wordList \n" +
                                "where uwl.id_user=?");

                listsStmt.setInt(1, userId);
                listsRS = listsStmt.executeQuery();


                //prepared statements, początek:
                ownerStmt = con.prepareStatement("select login from [User] where id_user=?");

                wordsStmt = con.prepareStatement("" +
                        "select count(id_progress) as wordsQuantity\n" +
                        "from progress \n" +
                        "where id_list=? \n" +
                        "group by id_list");

                learnedStmt = con.prepareStatement("" +
                        "select count(id_progress) as learnedQuantity\n" +
                        "from progress \n" +
                        "where id_list=? and learned=1\n" +
                        "group by id_list");

                //prepared statements, koniec:


                while (listsRS.next()) {
                    Log.d(TAG, "initWordLists: pobieranie informacji o liście");

                    listNames.add(listsRS.getString("name"));
                    difficultyLevels.add(listsRS.getInt("difficulty_level"));
                    listId = listsRS.getInt("id_wordList");
                    ownerId = listsRS.getInt("owner_id");


                    //pobieranie loginu właściciela listy
                    ownerStmt.setInt(1, ownerId);
                    ownerRS = ownerStmt.executeQuery();

                    if (ownerRS.next()) {
                        owners.add(ownerRS.getString("login"));
                        Log.d(TAG, "initWordLists: login właściciela listy: " + ownerRS.getString("login"));
                    } else
                        owners.add("-----");


                    //pobieranie liczby wszystkich słów oraz nauczonych listy
                    Log.d(TAG, "initWordLists: pobieranie liczby słów w danej liście");
                    wordsStmt.setInt(1, listId);
                    wordsRS = wordsStmt.executeQuery();

                    if (wordsRS.next()) {
                        wordQuantities.add(wordsRS.getInt("wordsQuantity"));

                        Log.d(TAG, "initWordLists: pobieranie liczby nauczonych słów w danej liście");
                        learnedStmt.setInt(1, listId);
                        learnedRS = learnedStmt.executeQuery();

                        if (learnedRS.next())
                            learnedQuantities.add(learnedRS.getInt("learnedQuantity"));
                        else
                            learnedQuantities.add(0);
                    } else {
                        wordQuantities.add(0);
                        learnedQuantities.add(0);
                    }
                }
                listsRS.close();
                listsStmt.close();

                if (ownerRS != null)
                    ownerRS.close();
                ownerStmt.close();

                if (wordsRS != null)
                    wordsRS.close();
                wordsStmt.close();

                if (learnedRS != null)
                    learnedRS.close();
                learnedStmt.close();
            } else {
                Toast.makeText(this, "Błąd połączenia z bazą", Toast.LENGTH_LONG).show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void initRecyclerView() {
        Log.d(TAG, "initRecyvlerView: inicjalizacja recyclerview");

        listsRecView = findViewById(R.id.listsRecView);
        WordListsAdapter adapter = new WordListsAdapter(this, listNames, difficultyLevels, wordQuantities, learnedQuantities, owners);
        listsRecView.setAdapter(adapter);
        listsRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void showListMenu(View view) {
        PopupMenu listPopupMenu = new PopupMenu(this, view);
        listPopupMenu.setOnMenuItemClickListener(this);
        listPopupMenu.inflate(R.menu.lists_popup_menu);
        listPopupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.searchList:
                intent = new Intent(this, AddFromPublicLists.class);
                startActivity(intent);
                return true;
            case R.id.createList:
                intent = new Intent(this, CreateNewList.class);
                intent.putExtra(MainActivity.EXTRA_NUMBER, userId);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}