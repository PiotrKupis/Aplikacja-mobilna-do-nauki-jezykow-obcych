package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
    }

    public void signin(View view) {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection con = connectionClass.CONN();
        //EditText login = findViewById(R.id.login);
        EditText loginText = (EditText) findViewById(R.id.loginField);

        //EditText password = findViewById(R.id.password);
        EditText passwordText = (EditText) findViewById(R.id.passwordField);

//        loginText.setError("");
//        passwordText.setError("");

        if (loginText.getText().length() == 0) {
            loginText.setError("Wpisz login");
        }
        if (passwordText.getText().length() == 0) {
            passwordText.setError("Wpisz haslo");
        }

        try {
            if (con != null) {
                String valueoflogin = String.valueOf(loginText.getText());
                String valueofpassword = String.valueOf(passwordText.getText());
                Statement commList = con.createStatement();
                ResultSet listsRS = commList.executeQuery(
                        "select * from [User] WHERE login='" + valueoflogin + "'");
                if (listsRS.next()) {
                    if (!listsRS.getString("password").equals(valueofpassword)) {
                        passwordText.setError("Haslo niepoprawne");
                    } else {
                        CurrentUser currentUser = new CurrentUser(getApplicationContext());
                        currentUser.setId(listsRS.getInt("id_user"));
                        currentUser.setlogin(valueoflogin);
                        currentUser.setPassword(valueofpassword);
                        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(myIntent);
                    }
                } else {
                    loginText.setError("Taki uzytkownik nie istnieje!");
                }

                con.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}