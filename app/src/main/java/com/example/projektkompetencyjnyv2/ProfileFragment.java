package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        CurrentUser currentUser = new CurrentUser(getActivity().getApplicationContext());

        TextView text = (TextView) view.findViewById(R.id.nameText);
        text.setText("Login: " + currentUser.getlogin());
        TextView TextID = (TextView) view.findViewById(R.id.TextID);
        TextID.setText("ID: " + currentUser.getId());

        ConnectionClass connectionClass = new ConnectionClass();
        Connection con = connectionClass.CONN();
        try {
            if (con != null) {

                Statement commList = con.createStatement();
                ResultSet listsRS = commList.executeQuery(
                        "SELECT learned_words_quantity FROM [User] WHERE id_user =" + currentUser.getId());
                if (listsRS.next()) {
                    TextView textlearnword = (TextView) view.findViewById(R.id.TextAmountLearnWord);
                    textlearnword.setText("Liczba nauczonych słów " + listsRS.getInt("learned_words_quantity"));
                }
            }
        } catch (SQLException throwables) {
            Log.d(null, "Problem z połaczeniem z bazą");
        }
        Button button = (Button) view.findViewById(R.id.buttonlogout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser currentUser = new CurrentUser(getActivity().getApplicationContext());
                currentUser.cleardata();
                Intent intent = new Intent(getActivity(), main_screen.class);
                startActivity(intent);
            }
        });
        return view;
    }
}

