package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class main_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        CurrentUser currentUser = new CurrentUser(getApplicationContext());

        if (currentUser.getId() != 0) {

            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(myIntent);
        } else {
            setContentView(R.layout.activity_main_screen);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        CurrentUser currentUser = new CurrentUser(getApplicationContext());

        if (currentUser.getId() != 0) {
            Intent myIntent = new Intent(getBaseContext(), WordLists.class);
            startActivity(myIntent);
        } else {
            setContentView(R.layout.activity_main_screen);
        }
    }

    public void register(View view) {
        Intent myIntent = new Intent(getBaseContext(), Register.class);
        startActivity(myIntent);
    }

    public void login(View view) {
        Intent myIntent = new Intent(getBaseContext(), LoginUser.class);
        startActivity(myIntent);
    }
}