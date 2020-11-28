package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user2);
    }
    public void signin(View view)
    {
        ConnectionClass connectionClass=new ConnectionClass();
        Connection con=connectionClass.CONN();
        EditText login = findViewById(R.id.login);
        TextInputLayout logintextInputLayout = ( TextInputLayout) findViewById(R.id.loginField);

        EditText password = findViewById(R.id.password);
        TextInputLayout passwordtextInputLayout = ( TextInputLayout) findViewById(R.id.passwordField);

        logintextInputLayout.setError("");
        passwordtextInputLayout.setError("");
        if(login.getText().length() == 0)
        {
            logintextInputLayout.setError("Wpisz login");
        }
        if(password.getText().length() == 0)
        {
            passwordtextInputLayout.setError("Wpisz haslo");
        }
        try {
            if (con != null) {
                String valueoflogin = String.valueOf(login.getText());
                String valueofpassword = String.valueOf(password.getText());
                Statement commList = con.createStatement();
                ResultSet listsRS = commList.executeQuery(
                        "select * from [User] WHERE login='" +valueoflogin + "'");
                if (listsRS.next()) {
                    if(!listsRS.getString("password").equals(valueofpassword))
                    {
                        passwordtextInputLayout.setError("Haslo niepoprawne");
                    }else
                    {
                        CurrentUser currentUser = new CurrentUser(getApplicationContext());
                        currentUser.setId(listsRS.getInt("id_user"));
                        currentUser.setlogin(valueoflogin);
                        currentUser.setPassword(valueofpassword);
                        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(myIntent);
                    }
                }else
                {
                    logintextInputLayout.setError("Taki uzytkownik nie istnieje!");
                }

                con.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}