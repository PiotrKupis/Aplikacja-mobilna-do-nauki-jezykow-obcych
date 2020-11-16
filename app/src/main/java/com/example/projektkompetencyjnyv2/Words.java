package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Words extends AppCompatActivity {

    private String listName;
    private String ownerLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        TextView listIdTxt=findViewById(R.id.listIdTxt);

        Intent intent=getIntent();
        listName = intent.getStringExtra("listName");
        ownerLogin=intent.getStringExtra("owner");
        listIdTxt.setText(listName+"\n"+ownerLogin);
    }
}