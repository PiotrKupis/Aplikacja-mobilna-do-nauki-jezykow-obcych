package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class WordLists extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "WordLists";

    private int userId;
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

        //pobieranie id usera
        Intent intent=getIntent();
        userId = intent.getIntExtra(MainActivity.EXTRA_NUMBER,0);
        userId = 1;//póki co id jest stałe (brak logowania)

        //inicjalizacja list danych
        listNames=new ArrayList<>();
        difficultyLevels=new ArrayList<>();
        wordQuantities=new ArrayList<>();
        learnedQuantities=new ArrayList<>();
        owners=new ArrayList<>();

        //inicjalizacja połaczenia się z bazą
        connectionClass=new ConnectionClass();
        con=connectionClass.CONN();

        //pobieranie z bazy
        initWordLists();
        //tworzenie listy
        initRecyclerView();

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

    private void initWordLists() {
        Log.d(TAG, "initWordLists: init records");

        int listId,ownerId;
        ResultSet listsRS, wordsRS, learnedRS,ownerRS;
        Statement commList, commWord, commLearned,commOwner;

        try {
            if(con!=null){

                commList = con.createStatement();
                listsRS = commList.executeQuery(
                        "select name,difficulty_level,owner_id,id_wordList\n" +
                        "from Word_list as wl inner join [User_WordList] as uwl \n" +
                        "\ton wl.id_word_list=uwl.id_wordList \n" +
                        "where uwl.id_user="+userId);

                while (listsRS.next()){
                    Log.d(TAG, "initWordLists: nowy element listy");

                    listNames.add(listsRS.getString("name"));
                    difficultyLevels.add(listsRS.getInt("difficulty_level"));
                    listId=listsRS.getInt("id_wordList");
                    ownerId=listsRS.getInt("owner_id");

                    Log.d(TAG, "initWordLists: pobieranie nazwy właściciela");
                    commOwner=con.createStatement();
                    ownerRS=commOwner.executeQuery(
                            "select login from [User] where id_user="+ownerId);
                    if(ownerRS.next()) {
                        owners.add(ownerRS.getString("login"));
                        Log.d(TAG, "initWordLists: "+ownerRS.getString("login"));
                    }
                    else
                        owners.add("-----");

                    Log.d(TAG, "initWordLists: liczba słów");
                    commWord=con.createStatement();
                    wordsRS=commWord.executeQuery(
                            "select count(id_progress) as wordsQuantity\n" +
                            "from progress \n" +
                            "where id_list=" + listId + "\n" +
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

    public void initRecyclerView(){
        Log.d(TAG, "initRecyvlerView: init recyclerview");

        listsRecView = findViewById(R.id.listsRecView);
        WordListsAdapter adapter = new WordListsAdapter(this,listNames,difficultyLevels,wordQuantities,learnedQuantities,owners);
        listsRecView.setAdapter(adapter);
        listsRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void showListMenu(View view){
        PopupMenu listPopupMenu=new PopupMenu(this, view);
        listPopupMenu.setOnMenuItemClickListener(this);
        listPopupMenu.inflate(R.menu.lists_popup_menu);
        listPopupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch(item.getItemId()){
            case R.id.searchList:
                Toast.makeText(this, "szukaj", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.createList:

                Intent intent = new Intent(this, CreateNewList.class);
                intent.putExtra(MainActivity.EXTRA_NUMBER ,userId);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}