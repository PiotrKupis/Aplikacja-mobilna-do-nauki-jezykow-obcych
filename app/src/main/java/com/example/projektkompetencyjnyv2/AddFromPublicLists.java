package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddFromPublicLists extends AppCompatActivity {

    private static final String TAG = "AddFromPublicLists";

    private ConnectionClass connectionClass;
    private Connection con;
    private int userId;
    private CurrentUser currentUser;
    private Spinner languageSpinner;

    private RecyclerView publicListsRecView;
    private ArrayList<String> listNames;
    private ArrayList<Integer> difficultyLevels;
    private ArrayList<Integer> wordQuantities;
    private ArrayList<String> owners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_from_public_lists);

        listNames = new ArrayList<>();
        difficultyLevels = new ArrayList<>();
        wordQuantities = new ArrayList<>();
        owners = new ArrayList<>();

        connectionClass=new ConnectionClass();
        con=connectionClass.CONN();

        currentUser = new CurrentUser(getApplicationContext());
        userId=currentUser.getId();

        initSpinner();

        initWordLists();
        initRecyclerView();
    }

    public void initSpinner(){

        List<String> languages;
        ResultSet languageRS;
        PreparedStatement languageStmt;

        languageSpinner=findViewById(R.id.selectLanguageSpinner);
        languages=new ArrayList<>();

        //pobieranie dostępnych języków z bazy
        try {
            languageStmt = con.prepareStatement("select language from language");
            languageRS = languageStmt.executeQuery();

            while (languageRS.next()){
                languages.add(languageRS.getString("language"));
            }

            languageRS.close();
            languageStmt.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        ArrayAdapter<String> languageAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
    }

    private void initWordLists() {

        Log.d(TAG, "AddFromPublicLists: inicializacja wpisów");
        int listId, ownerId;
        ResultSet listsRS=null,ownerRS=null,wordsRS=null;
        PreparedStatement listsStmt,ownerStmt,wordsStmt;

        try {
            if (con != null) {

                //pobranie informacji o listach związanych z użytkownikiem
                listsStmt = con.prepareStatement(
                        "select name, difficulty_level, owner_id, id_wordList\n" +
                                "from Word_list as wl inner join [User_WordList] as uwl\n" +
                                "on wl.id_word_list=uwl.id_wordList");
                //TODO dodac wybieranie według jezyka oraz nie wyswietlac list ktore sa uzyt lub juz je ma
                listsRS = listsStmt.executeQuery();

                //prepared statements, początek:
                ownerStmt=con.prepareStatement("" +
                        "select login from [User] where id_user=?");

                wordsStmt=con.prepareStatement("" +
                        "select count(id_word) as wordsQuantity\n" +
                        "from Word_list as wl\n" +
                        "\tinner join Word as w on wl.id_word_list=w.id_list " +
                        "where id_list=?" +
                        "group by id_word_list");

                //prepared statements, koniec:


                while (listsRS.next()){
                    Log.d(TAG, "initWordLists: pobieranie informacji o liście");

                    listNames.add(listsRS.getString("name"));
                    difficultyLevels.add(listsRS.getInt("difficulty_level"));
                    listId = listsRS.getInt("id_wordList");
                    ownerId = listsRS.getInt("owner_id");

                    //pobieranie loginu właściciela listy
                    ownerStmt.setInt(1,ownerId);
                    ownerRS=ownerStmt.executeQuery();

                    if(ownerRS.next()) {
                        owners.add(ownerRS.getString("login"));
                        Log.d(TAG, "initWordLists: login właściciela listy: " + ownerRS.getString("login"));
                    } else
                        owners.add("-----");


                    //pobieranie liczby wszystkich słów oraz nauczonych listy
                    Log.d(TAG, "initWordLists: pobieranie liczby słów w danej liście");
                    wordsStmt.setInt(1,listId);
                    wordsRS=wordsStmt.executeQuery();

                    if (wordsRS.next())
                        wordQuantities.add(wordsRS.getInt("wordsQuantity"));
                    else
                        wordQuantities.add(0);
                }
                listsRS.close();
                listsStmt.close();

                if(ownerRS!=null)
                    ownerRS.close();
                ownerStmt.close();

                if(wordsRS!=null)
                    wordsRS.close();
                wordsStmt.close();
            } else {
                Toast.makeText(this, "Błąd połączenia z bazą", Toast.LENGTH_LONG).show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void initRecyclerView() {
        Log.d(TAG, "initRecyvlerView: inicjalizacja recyclerview");

        publicListsRecView = findViewById(R.id.publicListsRecView);
        PublicWordListsAdapter adapter = new PublicWordListsAdapter(this, listNames, difficultyLevels, wordQuantities, owners);
        publicListsRecView.setAdapter(adapter);
        publicListsRecView.setLayoutManager(new LinearLayoutManager(this));
    }
}