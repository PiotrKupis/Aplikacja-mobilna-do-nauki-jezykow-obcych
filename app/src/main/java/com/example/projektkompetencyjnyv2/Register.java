package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class Register extends AppCompatActivity {
    private ConnectionClass connectionClass;
    private Connection con;
    private int correctdata;
    private String password;
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(null,"uruchamiamy rejestacje");
        // connect with data base
        connectionClass=new ConnectionClass();
        con=connectionClass.CONN();
        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String[] files = getApplicationContext().fileList();
                Log.d(null, Arrays.toString(files));

                TextInputLayout textInputLayout = ( TextInputLayout) findViewById(R.id.textInputLayout5);
                TextInputEditText textInputEditTextlogin = findViewById(R.id.login);
                try {
                    if (con != null) {

                        Statement commList = con.createStatement();
                        ResultSet listsRS = commList.executeQuery(
                                "select login from [User] WHERE login='"+textInputEditTextlogin.getText()+"'");
                        while (listsRS.next()) {
                            textInputLayout.setError("Taki login juz jest uzywany");
                            Log.d(null, "initWordLists: nowy element listy");
                            correctdata = 1;
                        }

                        if(correctdata== 0)
                        {
                            login = String.valueOf(textInputEditTextlogin.getText());
                            textInputLayout.setError("");
                            TextInputLayout textInputLayoutpassword1 = ( TextInputLayout) findViewById(R.id.textInputLayout6);
                            TextInputEditText textInputEditTextpassword = findViewById(R.id.password);
                            TextInputLayout textInputLayoutpassword2 = ( TextInputLayout) findViewById(R.id.textInputLayout4);
                            TextInputEditText textInputEditTextpassword2 = findViewById(R.id.passwordConfirm);
                            Log.d(null, String.valueOf(textInputEditTextpassword.getText()));
                            Log.d(null, String.valueOf(textInputEditTextpassword2.getText()));
                            if(String.valueOf(textInputEditTextpassword.getText()).equals(String.valueOf(textInputEditTextpassword2.getText())))
                            {
                                password = String.valueOf(textInputEditTextpassword.getText());
                                textInputLayoutpassword2.setError("");

                            }else
                            {
                                textInputLayoutpassword2.setError("Hasla nie sa takie same");
                                correctdata = 1;
                            }
                        }
                        if(correctdata ==0) {
                             int result  = commList.executeUpdate(
                            "INSERT INTO [User](login,password,learned_words_quantity,profile_photo) values ('"+login+"','"+password+"',0,'')");
                            Log.d(null, "Utworzenie usera");

                            CurrentUser currentUser = new CurrentUser(getApplicationContext());
                            currentUser.setlogin(login);
                            currentUser.setPassword(password);
                            Log.d(null, "SELECT [id_user] FROM [User] WHERE login = '"+login+"'");
                            ResultSet resultid  = commList.executeQuery("SELECT id_user FROM [User] WHERE login = '"+login+"'");
                            if(resultid.next())
                            {
                                currentUser.setId(resultid.getInt("id_user"));
                            }
                            Log.d(null,"rejestacja udana");
                            setContentView(R.layout.fragment_profile);

                            /*
                              File file2 = new File(getApplicationContext().getFilesDir(),"LoginPassword.txt");
                              if(file2.exists())
                              {
                                  BufferedWriter writer = new BufferedWriter(new FileWriter(file2));
                                  writer.write("");
                                  writer.close();
                              }
                            if (file2.createNewFile() || file2.exists()) {
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file2));
                                writer.write("Login:"+login+"\nPassword:"+password+"\n");
                                writer.close();
                                setContentView(R.layout.fragment_profile);
                            }
                             */
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