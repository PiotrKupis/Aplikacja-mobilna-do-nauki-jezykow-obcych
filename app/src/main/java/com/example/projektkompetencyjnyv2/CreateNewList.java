package com.example.projektkompetencyjnyv2;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CreateNewList extends AppCompatActivity{

    private static final String TAG = "CreateNewList";

    private ConnectionClass connectionClass;
    private Connection con;
    private int userId;
    private CurrentUser currentUser;

    private EditText listNameEdtTxt;
    private RatingBar listDifficultyRB;
    private Spinner languageSpinner;
    private Spinner privacySpinner;
    private TextView newListMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_list);

        connectionClass=new ConnectionClass();
        con=connectionClass.CONN();

        currentUser = new CurrentUser(getApplicationContext());
        userId=currentUser.getId();

        initLayout();
        initVariables();
    }

    public void initVariables(){
        listNameEdtTxt=findViewById(R.id.listNameEdtTxt);
        listDifficultyRB=findViewById(R.id.listDifficultyRB);
        languageSpinner=findViewById(R.id.languageSpinner);
        privacySpinner=findViewById(R.id.privacySpinner);
        newListMessage=findViewById(R.id.newListMessage);
    }

    public void initLayout(){

        Spinner privacySpinner,languageSpinner;
        List<String> languages;
        ArrayAdapter<CharSequence> privacyAdapter;
        ResultSet languageRS;
        PreparedStatement languageStmt;

        privacySpinner=findViewById(R.id.privacySpinner);
        privacyAdapter=ArrayAdapter.createFromResource(this,R.array.listPrivacy, android.R.layout.simple_spinner_item);
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        privacySpinner.setAdapter(privacyAdapter);

        languageSpinner=findViewById(R.id.languageSpinner);
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

    public void createNewList(View view){

        String language,privacy,name;
        int difficultyLevel,languageId,isPublic,listId;
        ResultSet listNameRS,languageIdRs,listIdRS;
        PreparedStatement createListStmt,listNameStmt,languageIdStmt,listIdStmt;

        try {
            name=listNameEdtTxt.getText().toString();

            //sprawdzenie czy użytkownik nie stworzył już wcześniej listy o tej samej nazwie
            listNameStmt=con.prepareStatement("" +
                    "select *\n" +
                    "from Word_list\n" +
                    "where owner_id=? and name=?");
            listNameStmt.setInt(1,userId);
            listNameStmt.setString(2,name);
            listNameRS=listNameStmt.executeQuery();

            if(!listNameRS.next() && name.length()!=0){

                listNameRS.close();
                listNameStmt.close();

                privacy=privacySpinner.getSelectedItem().toString();
                language=languageSpinner.getSelectedItem().toString();;
                difficultyLevel=(int)listDifficultyRB.getRating();

                //pobranie id wybranego języka
                languageIdStmt=con.prepareStatement("" +
                        "select id_language\n" +
                        "from language\n" +
                        "where language=?");
                languageIdStmt.setString(1,language);
                languageIdRs=languageIdStmt.executeQuery();

                if(languageIdRs.next()){
                    languageId=languageIdRs.getInt("id_language");
                    languageIdRs.close();
                    languageIdStmt.close();

                    //ustawienie prywatności listy
                    if(privacy.equals("prywatna"))
                        isPublic=0;
                    else
                        isPublic=1;

                    //dodanie listy do bazy
                    createListStmt = con.prepareStatement("" +
                            "insert into Word_list " +
                            "values (?,?,?,?,?)");
                    createListStmt.setString(1,name);
                    createListStmt.setInt(2,difficultyLevel);
                    createListStmt.setInt(3,userId);
                    createListStmt.setInt(4,languageId);
                    createListStmt.setInt(5,isPublic);
                    createListStmt.executeUpdate();
                    createListStmt.close();

                    //pobranie id utworzonej listy
                    listIdStmt=con.prepareStatement("" +
                            "select id_word_list\n" +
                            "from Word_list\n" +
                            "where owner_id=? and name=?");
                    listIdStmt.setInt(1,userId);
                    listIdStmt.setString(2,name);
                    listIdRS=listIdStmt.executeQuery();

                    if(listIdRS.next()){
                        listId=listIdRS.getInt("id_word_list");

                        listIdRS.close();
                        listIdStmt.close();

                        createListStmt=con.prepareStatement("" +
                                "insert into [User_WordList] " +
                                "values (?,?)");
                        createListStmt.setInt(1,listId);
                        createListStmt.setInt(2,userId);
                        createListStmt.executeUpdate();
                        createListStmt.close();

                        newListMessage.setText("Lista została utworzona");
                        newListMessage.setTextColor(getResources().getColor(R.color.green));
                        newListMessage.setVisibility(View.VISIBLE);
                    }
                }
            }
            else if(name.length()==0){
                newListMessage.setText("Pole nazwy nie może być puste");
                newListMessage.setTextColor(getResources().getColor(R.color.red));
                newListMessage.setVisibility(View.VISIBLE);
            }
            else{
                newListMessage.setText("Utworzyłeś już listę o takiej nazwie!\nZmień nazwę albo usuń wcześniejszą listę");
                newListMessage.setTextColor(getResources().getColor(R.color.red));
                newListMessage.setVisibility(View.VISIBLE);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}