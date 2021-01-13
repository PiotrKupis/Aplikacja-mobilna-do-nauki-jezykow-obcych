package com.example.projektkompetencyjnyv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class RankingFragment extends Fragment {

    private View rootView;
    private ArrayList<String> users;
    private TableLayout tableLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ranking, container, false);

        users=new ArrayList<>();

        users.add("user1");
        users.add("user2");
        users.add("user3");

        tableLayout=(TableLayout)rootView.findViewById(R.id.rankingTable);

        for(int i=0;i<users.size();++i){
            TableRow tableRow=new TableRow(getContext());

            TextView user=new TextView(getContext());
            user.setText(users.get(i));

            tableRow.addView(user);
            tableLayout.addView(tableRow);


        }



        return rootView;
    }


}
