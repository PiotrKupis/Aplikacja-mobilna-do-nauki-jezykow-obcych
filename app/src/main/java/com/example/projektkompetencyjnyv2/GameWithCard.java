package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameWithCard extends AppCompatActivity {
    double time = 0;
    boolean fail = false;
    int paircollect= 0 ;
    int selectedbutton = 0;
    Button[] sellectedbuttonarray = new Button[2];
    String[] arraytodisplay = new String[10];
    List<String> wordsPolish = new ArrayList<>();
    List<String> wordsEnglish = new ArrayList<>();
    Button[] button= new Button[12];
    int listId;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_with_card);


        Bundle extras = getIntent().getExtras();
        int userId;
        if (extras != null) {
            userId = extras.getInt("userID");
            System.out.println("ID użytkownika" + userId);
            listId = extras.getInt("listID");
        } else {
            userId = 1;
            listId = 19;
        }

        time = 0;
        getWords();
        generatedata();
        inputData();
        Thread t1 = new Thread(this::stoperStart);
        t1.start();

    }

    private void getWords() {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection con = connectionClass.CONN();

        ResultSet words;
        Statement commList;
        int iterator = 0;
        int i = 0;
        try {
            if (con != null) {
                commList = con.createStatement();
                words = commList.executeQuery(
                        "select * from Word where id_list = " + listId
                );
                while (words.next()) {
                    arraytodisplay[i]= words.getString("word");
                    i++;
                    arraytodisplay[i] = words.getString("meaning");
                    i++;
                    wordsEnglish.add(words.getString("word"));
                    wordsPolish.add(words.getString("meaning"));
                    iterator += 1;

                    if(iterator>5 || i >9) {
                        break;
                    }

                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void stoperStart()
    {

        synchronized (this) {
            while (paircollect < 5) {
                try {
                    Thread.sleep((long) 1000);
                } catch (InterruptedException e) {
                }
                time++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView)findViewById(R.id.time);
                        textView.setText(time + "s");
                    }
                });

            }
        }
    }

    private void inputData()
    {
        for (int i = 1; i <= arraytodisplay.length; i++) {
            int nextbutton = getResources().getIdentifier("button"+i, "id", getPackageName());
            button[i] = (Button) findViewById(nextbutton);
            if(button[i]!= null)
            {
                button[i].setText(arraytodisplay[i-1]);
            }

        }
    }
    private void generatedata()
    {
        Random rand = new Random(0);
        for(int j=0;j<2;j++) {
            for (int i = 0; i < arraytodisplay.length; i++) {
                int randomIndexToSwap = rand.nextInt(arraytodisplay.length - 1);
                String temp = arraytodisplay[randomIndexToSwap];
                arraytodisplay[randomIndexToSwap] = arraytodisplay[i];
                arraytodisplay[i] = temp;
            }
        }
        System.out.println(Arrays.toString(arraytodisplay));
    }

    public void ValidateButton(View view) {
        if(fail)
        {
            clearbutton(R.color.purple_200);
            fail = false;
        }
        sellectedbuttonarray[selectedbutton] = findViewById(view.getId());
        sellectedbuttonarray[selectedbutton].setBackgroundTintList(getResources().getColorStateList(R.color.green));
        selectedbutton++;

        if(selectedbutton == 2)
        {
            if(validatedata())
            {
                invisiblebutton();
            }else
                clearbutton(R.color.red);
        }


    }

    private boolean validatedata()
    {
        // sciagni text  z przycisku znajdz go w liscie i porwoaj
        System.out.println(sellectedbuttonarray[0].getText());
        System.out.println(sellectedbuttonarray[1].getText());
        int indexpolish=-1;
        int indexenglish=-5;
        for(int i = 0; i<wordsPolish.size();i++)
        {
            if(wordsPolish.get(i).contentEquals(sellectedbuttonarray[0].getText())|| wordsPolish.get(i).contentEquals(sellectedbuttonarray[1].getText()))
            {
                indexpolish = i;
                break;
            }
        }
        for(int i = 0; i<wordsEnglish.size();i++)
        {
            if(wordsEnglish.get(i).contentEquals(sellectedbuttonarray[0].getText())|| wordsEnglish.get(i).contentEquals(sellectedbuttonarray[1].getText()))
            {
                indexenglish = i;
                break;
            }
        }

        if(indexenglish == indexpolish)
        {
            paircollect++;
            if(paircollect ==5)
            {
                Intent myIntent = new Intent(getBaseContext(), ResultGameWithCard.class);
                finish();
                myIntent.putExtra("time", time);
                startActivity(myIntent);
            }
            return true;
        }
        fail = true;
        return false;
    }

    private void invisiblebutton()
    {
        for (int i = 0;i<2;i++) {
            sellectedbuttonarray[i].setVisibility(View.INVISIBLE);
            selectedbutton = 0;
        }
    }

    private void clearbutton(int a )
    {
        for (int i = 0;i<2;i++) {
            sellectedbuttonarray[i].setBackgroundTintList(getResources().getColorStateList(a));
            selectedbutton = 0;

        }
    }
}