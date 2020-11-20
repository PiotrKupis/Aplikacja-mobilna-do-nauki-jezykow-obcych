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

        Intent intent=getIntent();
        userId = intent.getIntExtra(MainActivity.EXTRA_NUMBER,0);

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
        Statement commLanguage;

        privacySpinner=findViewById(R.id.privacySpinner);
        privacyAdapter=ArrayAdapter.createFromResource(this,R.array.listPrivacy, android.R.layout.simple_spinner_item);
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        privacySpinner.setAdapter(privacyAdapter);

        languageSpinner=findViewById(R.id.languageSpinner);
        languages=new ArrayList<>();

        try {
            commLanguage = con.createStatement();
            languageRS = commLanguage.executeQuery("select language from language");

            while (languageRS.next()){
                languages.add(languageRS.getString("language"));
            }
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
        Statement commListName,commLanguageId,commListId;
        PreparedStatement commCreateList;

        try {
            name=listNameEdtTxt.getText().toString();

            //sprawdzenie czy użytkownik nie stworzył już wcześniej listy o tej samej nazwie
            commListName = con.createStatement();
            listNameRS = commListName.executeQuery("\n" +
                    "select *\n" +
                    "from Word_list\n" +
                    "where owner_id="+userId+" and name='"+name+"'");

            if(!listNameRS.next() && name.length()!=0){

                privacy=privacySpinner.getSelectedItem().toString();
                language=languageSpinner.getSelectedItem().toString();;
                difficultyLevel=(int)listDifficultyRB.getRating();

                //pobranie id wybranego języka
                commLanguageId = con.createStatement();
                languageIdRs = commLanguageId.executeQuery("select id_language\n" +
                        "from language\n" +
                        "where language='"+language+"'");

                if(languageIdRs.next()){
                    languageId=languageIdRs.getInt("id_language");

                    //ustawienie prywatności listy
                    if(privacy.equals("prywatna"))
                        isPublic=0;
                    else
                        isPublic=1;

                    //dodanie listy do bazy
                    commCreateList = con.prepareStatement("" +
                            "insert into Word_list " +
                            "values ('"+name+"',"+difficultyLevel+","+userId+","+languageId+","+isPublic+")");
                    commCreateList.executeUpdate();

                    //pobranie id utworzonej listy
                    commListId = con.createStatement();
                    listIdRS = commListId.executeQuery("" +
                            "select id_word_list\n" +
                            "from Word_list\n" +
                            "where owner_id="+userId+" and name='"+name+"'");

                    if(listIdRS.next()){
                        listId=listIdRS.getInt("id_word_list");

                        commCreateList = con.prepareStatement("" +
                                "insert into [User_WordList] values ("+listId+","+userId+")");
                        commCreateList.executeUpdate();

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