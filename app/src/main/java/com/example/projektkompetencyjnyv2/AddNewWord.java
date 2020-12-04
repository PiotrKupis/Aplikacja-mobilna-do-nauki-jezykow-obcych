package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddNewWord extends AppCompatActivity {

    private static final String TAG = "AddNewWord";
    private String listName;
    private String ownerLogin;

    private int listId;
    private int ownerId;

    private EditText newWordEdtTxt;
    private EditText newMeaningEdtTxt;
    private EditText newSentenceEdtTxt;
    private EditText newSentenceMeaningEdtTxt;
    private TextView newWordMessage;

    private ConnectionClass connectionClass;
    private Connection con;
    private ResultSet rs;
    private PreparedStatement stmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_word);

        Intent intent=getIntent();
        listName = intent.getStringExtra("listName");
        ownerLogin = intent.getStringExtra("owner");

        initVariables();
    }

    public void initVariables(){

        newWordEdtTxt=findViewById(R.id.newWordEdtTxt);
        newMeaningEdtTxt=findViewById(R.id.newMeaningEdtTxt);
        newSentenceEdtTxt=findViewById(R.id.newSentenceEdtTxt);
        newSentenceMeaningEdtTxt=findViewById(R.id.newSentenceMeaningEdtTxt);
        newWordMessage=findViewById(R.id.newWordMessage);

        connectionClass=new ConnectionClass();
        con=connectionClass.CONN();

        try {
            //pobranie id listy oraz id właściciela listy
            Log.d(TAG, "initVariables: pobieranie id listy oraz właściciela");
            
            stmt = con.prepareStatement("" +
                    "select wl.id_word_list as id_list, wl.owner_Id as owner_id\n" +
                    "from Word_list as wl inner join [User] as u \n" +
                    "\ton wl.owner_id=u.id_user\n" +
                    "where wl.name=? and u.login=?");

            stmt.setString(1, listName);
            stmt.setString(2, ownerLogin);
            rs = stmt.executeQuery();

            if (rs.next()) {
                listId=rs.getInt("id_list");
                ownerId=rs.getInt("owner_id");
            }
            rs.close();
            stmt.close();
        }catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public void addNewWord(View view){

        String word,meaning,exampleSentence,exampleSentenceMeaning;
        int wordId;
        PreparedStatement stmtAddProgress;

        word=newWordEdtTxt.getText().toString();
        meaning=newMeaningEdtTxt.getText().toString();
        exampleSentence=newSentenceEdtTxt.getText().toString();
        exampleSentenceMeaning=newSentenceMeaningEdtTxt.getText().toString();

        //TODO dodać sprawdzenie czy podane słowo nie jest już w liscie
        if(word.length()==0){
            newWordMessage.setText("Pole słowa nie może być puste");
            newWordMessage.setTextColor(getResources().getColor(R.color.red));
            newWordMessage.setVisibility(View.VISIBLE);
        }
        else if(meaning.length()==0){
            newWordMessage.setText("Pole znaczenia nie może być puste");
            newWordMessage.setTextColor(getResources().getColor(R.color.red));
            newWordMessage.setVisibility(View.VISIBLE);
        }
        else{
            try {
                if(exampleSentence.length()==0){
                    exampleSentence="-";
                    exampleSentenceMeaning="-";
                }

                Log.d(TAG, "addNewWord: sprawdzenie czy podane słowo już nie znajduje się w liście");
                stmt = con.prepareStatement("\n" +
                        "select id_word " +
                        "from word " +
                        "where word=? and id_list=?");

                stmt.setString(1,word);
                stmt.setInt(2,listId);
                rs=stmt.executeQuery();

                if(rs.next()){
                    rs.close();
                    stmt.close();

                    newWordMessage.setText("Podane słowo już znajduje się w wybranej liście");
                    newWordMessage.setTextColor(getResources().getColor(R.color.red));
                    newWordMessage.setVisibility(View.VISIBLE);
                }
                else if(exampleSentence.length()>0 && exampleSentenceMeaning.length()==0){
                    newWordMessage.setText("Nie podano znaczenia przykładowego zdania");
                    newWordMessage.setTextColor(getResources().getColor(R.color.red));
                    newWordMessage.setVisibility(View.VISIBLE);
                }
                else{
                    //dodawanie słowa
                    Log.d(TAG, "addNewWord: dodawanie słowa do listy");
                    stmt = con.prepareStatement("" +
                            "insert into [word] " +
                            "values (?,?,?,?,?)");

                    stmt.setString(1,word);
                    stmt.setString(2,meaning);
                    stmt.setString(3,exampleSentence);
                    stmt.setInt(4,listId);
                    stmt.setString(5,exampleSentenceMeaning);
                    stmt.executeUpdate();
                    stmt.close();

                    //pobieranie id nowego słowa
                    Log.d(TAG, "addNewWord: pobieranie id nowego słowa");
                    stmt = con.prepareStatement("\n" +
                            "select id_word " +
                            "from word " +
                            "where word=? and meaning=? and example_sentence=? and id_list=?");

                    stmt.setString(1,word);
                    stmt.setString(2,meaning);
                    stmt.setString(3,exampleSentence);
                    stmt.setInt(4,listId);
                    rs=stmt.executeQuery();

                    if(rs.next()){
                        wordId=rs.getInt("id_word");

                        //dodawnie wpisu o postepie w nauce danego słowa
                        Log.d(TAG, "addNewWord: dodawnie postepu nowego słowa");
                        stmtAddProgress = con.prepareStatement("" +
                                "insert into [progress] values (?,?,?,?,?,?)");

                        stmtAddProgress.setInt(1,0);
                        stmtAddProgress.setInt(2,wordId);
                        stmtAddProgress.setInt(3,listId);
                        stmtAddProgress.setInt(4,ownerId);
                        stmtAddProgress.setInt(5,0);
                        stmtAddProgress.setInt(6,0);
                        stmtAddProgress.executeUpdate();
                        stmtAddProgress.close();

                        newWordMessage.setText("Pomyślnie dodano słowo do listy");
                        newWordMessage.setTextColor(getResources().getColor(R.color.green));
                        newWordMessage.setVisibility(View.VISIBLE);
                    }
                    rs.close();
                    stmt.close();
                }
            }catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }
}