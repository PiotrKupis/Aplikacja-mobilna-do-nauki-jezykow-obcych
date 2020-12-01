package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class Register extends AppCompatActivity {
    private Connection con;
    private int correctdata;
    private String password;
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Log.d(null, "uruchamiamy rejestacje");
        // connect with data base
        ConnectionClass connectionClass = new ConnectionClass();
        con = connectionClass.CONN();
        Button button = findViewById(R.id.registerConfirmBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String[] files = getApplicationContext().fileList();
                Log.d(null, Arrays.toString(files));

                EditText loginText = (EditText) findViewById(R.id.loginFieldReg);
                //EditText textInputEditTextlogin = findViewById(R.id.login);
                try {
                    if (con != null) {

                        Statement commList = con.createStatement();
                        ResultSet listsRS = commList.executeQuery(
                                "select login from [User] WHERE login='" + loginText.getText() + "'");
                        while (listsRS.next()) {
                            loginText.setError("Taki login juz jest uzywany");
                            Log.d(null, "initWordLists: nowy element listy");
                            correctdata = 1;
                        }

                        if (correctdata == 0) {
                            login = String.valueOf(loginText.getText());
                            loginText.setError("");
                            //TextInputLayout textInputLayoutpassword1 = ( TextInputLayout) findViewById(R.id.textInputLayout6);
                            //TextInputEditText textInputEditTextpassword = findViewById(R.id.password);
                            EditText passwordText = (EditText) findViewById(R.id.passwordFieldReg);
                            EditText passwordConfirmText = findViewById(R.id.passwordConfirmField);
                            Log.d(null, String.valueOf(passwordText.getText()));
                            Log.d(null, String.valueOf(passwordConfirmText.getText()));
                            if (String.valueOf(passwordText.getText()).equals(String.valueOf(passwordConfirmText.getText()))) {
                                password = String.valueOf(passwordText.getText());
                                passwordText.setError("");
                            } else {
                                passwordText.setError("Hasla nie sa takie same");
                                correctdata = 1;
                            }
                        }
                        if (correctdata == 0) {
                            int result = commList.executeUpdate(
                                    "INSERT INTO [User](login,password,learned_words_quantity,profile_photo) values ('" + login + "','" + password + "',0,'')");
                            Log.d(null, "Utworzenie usera");

                            CurrentUser currentUser = new CurrentUser(getApplicationContext());
                            currentUser.setlogin(login);
                            currentUser.setPassword(password);
                            Log.d(null, "SELECT [id_user] FROM [User] WHERE login = '" + login + "'");
                            ResultSet resultid = commList.executeQuery("SELECT id_user FROM [User] WHERE login = '" + login + "'");
                            if (resultid.next()) {
                                currentUser.setId(resultid.getInt("id_user"));
                            }
                            Log.d(null, "rejestacja udana");
                            
                            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(myIntent);
                        }
                        correctdata = 0;
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
}