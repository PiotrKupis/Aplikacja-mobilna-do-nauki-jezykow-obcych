package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WordLists extends AppCompatActivity {

    private ConnectionClass connectionClass;
    private Statement comm;
    private Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_lists);

        Intent intent=getIntent();
        int userId=intent.getIntExtra(MainActivity.EXTRA_NUMBER,0);

        TextView userIdTxt=findViewById(R.id.userIdTxt);
        userIdTxt.setText("user id: "+userId);


        connectionClass=new ConnectionClass();
        con=connectionClass.CONN();

        //testowy kod łączenia się z bazą
        try {
            if(con!=null){
                Toast.makeText(this, "Udało się połączyć z bazą", Toast.LENGTH_LONG).show();

                comm=con.createStatement();
                ResultSet rs=comm.executeQuery("select word, definition from translate");
                String result="";

                while (rs.next()){
                    result+=rs.getString("word")+" - "+rs.getString("definition")+"\n";
                }
                userIdTxt.setText(result);
            }
            else{
                Toast.makeText(this, "Błąd połączenia z bazą", Toast.LENGTH_LONG).show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
    }
}