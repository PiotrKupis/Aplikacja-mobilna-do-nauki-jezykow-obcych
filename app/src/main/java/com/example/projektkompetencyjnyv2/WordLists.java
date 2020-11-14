package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class WordLists extends AppCompatActivity {

    private static final String TAG = "WordLists";

    private int userId;
    private ConnectionClass connectionClass;
    private Connection con;

    private RecyclerView listsRecView;
    private ArrayList<String> listNames;
    private ArrayList<Integer> difficultyLevels;
    private ArrayList<Integer> wordQuantities;
    private ArrayList<Integer> learnedQuantities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_lists);

        //pobieranie id usera
        Intent intent=getIntent();
        userId=intent.getIntExtra(MainActivity.EXTRA_NUMBER,0);
        userId=1;//póki co id jest stałe (brak logowania)

        //inicjalizacja list danych
        listNames=new ArrayList<>();
        difficultyLevels=new ArrayList<>();
        wordQuantities=new ArrayList<>();
        learnedQuantities=new ArrayList<>();

        //inicjalizacja połaczenia się z bazą
        connectionClass=new ConnectionClass();
        con=connectionClass.CONN();

        //pobieranie z bazy
        initWordLists();
        //tworzenie listy
        initRecyvlerView();


        /*
        //trzeba bedzie wrzucić dostęp do bazy w innych wątkach
            new Thread() {
                public void run() {


                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    //pobieranie z bazy
                                    initWordLists();
                                    //tworzenie listy
                                    initRecyvlerView();
                                }
                            });


                }
            }.start();
            */

    }

    private void initWordLists(){
        Log.d(TAG, "initWordLists: init records");

        int listId;
        ResultSet listsRS,wordsRS,learnedRS;
        Statement commList,commWord,commLearned;

        try {
            if(con!=null){
                Toast.makeText(this, "Udało się połączyć z bazą", Toast.LENGTH_LONG).show();

                commList=con.createStatement();
                listsRS=commList.executeQuery(
                        "select name,difficulty_level,owner_id,id_wordList\n" +
                        "from Word_list as wl inner join [User_WordList] as uwl \n" +
                        "\ton wl.id_word_list=uwl.id_wordList \n" +
                        "where uwl.id_user="+userId);

                while (listsRS.next()){
                    Log.d(TAG, "initWordLists: nowy element listy");

                    listNames.add(listsRS.getString("name"));
                    difficultyLevels.add(listsRS.getInt("difficulty_level"));
                    listId=listsRS.getInt("id_wordList");


                    Log.d(TAG, "initWordLists: liczba słów");
                    commWord=con.createStatement();
                    wordsRS=commWord.executeQuery(
                            "select count(id_progress) as wordsQuantity\n" +
                            "from progress \n" +
                            "where id_list="+listId+"\n" +
                            "group by id_list");

                    if(wordsRS.next()){
                        wordQuantities.add(wordsRS.getInt("wordsQuantity"));

                        Log.d(TAG, "initWordLists: liczba nauczonych");
                        commLearned=con.createStatement();
                        learnedRS=commLearned.executeQuery(
                            "select count(id_progress) as learnedQuantity\n" +
                            "from progress \n" +
                            "where id_list="+listId+" and learned=1\n" +
                            "group by id_list");
                        if(learnedRS.next())
                            learnedQuantities.add(learnedRS.getInt("learnedQuantity"));
                        else
                            learnedQuantities.add(0);
                    }
                    else{
                        wordQuantities.add(0);
                        learnedQuantities.add(0);
                    }
                }
            }
            else{
                Toast.makeText(this, "Błąd połączenia z bazą", Toast.LENGTH_LONG).show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void initRecyvlerView(){
        Log.d(TAG, "initRecyvlerView: init recyclerview");

        listsRecView=findViewById(R.id.listsRecView);
        WordListsAdapter adapter=new WordListsAdapter(this,listNames,difficultyLevels,wordQuantities,learnedQuantities);
        listsRecView.setAdapter(adapter);
        listsRecView.setLayoutManager(new LinearLayoutManager(this));
    }
}