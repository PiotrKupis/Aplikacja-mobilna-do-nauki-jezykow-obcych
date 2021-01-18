package com.example.projektkompetencyjnyv2;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class that shows ranking of learned words
 */
public class RankingFragment extends Fragment {

    private View rootView;
    private ConnectionClass connectionClass;
    private Connection con;
    private TableLayout tableLayout;
    private Spinner periodSpinner;
    private String selectedRankingPeriod;

    private ArrayList<String> users;
    private ArrayList<Integer> learnedWordsNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ranking, container, false);

        connectionClass = new ConnectionClass();
        con = connectionClass.CONN();
        initSpinner();
        initUserTable();

        return rootView;
    }

    /**
     * Method that sets visual parameters of TextView
     * @param textView
     */
    public void setTextViewParameters(TextView textView) {
        textView.setTextSize(17);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.parseColor("#0c0cb5"));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC);
    }

    /**
     * Method that add a new row of user's entry to the main table
     * @param rankValue
     * @param userValue
     * @param learnedWordsQuantityValue
     */
    public void addNewRow(String rankValue, String userValue, String learnedWordsQuantityValue) {

        TableRow tableRow = new TableRow(getContext());

        TextView rank = new TextView(getContext());
        setTextViewParameters(rank);
        rank.setText(rankValue);

        TextView user = new TextView(getContext());
        setTextViewParameters(user);
        user.setText(userValue);

        TextView numberOfLearnedWords = new TextView(getContext());
        setTextViewParameters(numberOfLearnedWords);
        numberOfLearnedWords.setText(learnedWordsQuantityValue);

        tableRow.addView(rank);
        tableRow.addView(user);
        tableRow.addView(numberOfLearnedWords);

        tableLayout.addView(tableRow);
    }

    /**
     * Method that initializes the TableLayout and inserts entries of users
     */
    public void initUserTable() {

        int i;
        String userLogin;
        ResultSet usersRS;
        PreparedStatement monthlyRankingStmt, totalRankingStmt, listOfUsersStmt;

        users = new ArrayList<>();
        learnedWordsNumber = new ArrayList<>();
        tableLayout = rootView.findViewById(R.id.rankingTable);

        try {
            monthlyRankingStmt = con.prepareStatement("" +
                    "select login, count(id_progress) as number_of_learned\n" +
                    "from progress as p inner join [User] as u\n" +
                    "\ton p.id_user=u.id_user\n" +
                    "where learned=1 and year(learned_date)=year(getdate()) and month(learned_date)=month(getdate())\n" +
                    "group by login\n" +
                    "order by number_of_learned desc");

            totalRankingStmt = con.prepareStatement("" +
                    "select login,count(id_progress) as number_of_learned\n" +
                    "from progress as p inner join [User] as u\n" +
                    "\ton p.id_user=u.id_user\n" +
                    "where learned=1\n" +
                    "group by login\n" +
                    "order by number_of_learned desc");

            listOfUsersStmt = con.prepareStatement("select login from [User]");

            //getting list of users
            if (selectedRankingPeriod.equals("Miesięczny"))
                usersRS = monthlyRankingStmt.executeQuery();
            else
                usersRS = totalRankingStmt.executeQuery();

            while (usersRS.next()) {
                users.add(usersRS.getString("login"));
                learnedWordsNumber.add(usersRS.getInt("number_of_learned"));
            }

            //adding titles of the columns
            addNewRow("Pozycja", "Nazwa użytkownika", "Nauczone\nsłowa");

            //adding records to the TableLayout
            for (i = 0; i < users.size(); ++i) {
                addNewRow(String.valueOf(i + 1), users.get(i), String.valueOf(learnedWordsNumber.get(i)));
            }

            //adding additional users if number of added users is less that 20
            if (i < 20) {
                usersRS.close();
                usersRS = listOfUsersStmt.executeQuery();

                while (usersRS.next() && i < 20) {

                    userLogin = usersRS.getString("login");
                    if (!users.contains(userLogin)) {
                        users.add(userLogin);
                        addNewRow(String.valueOf(i + 1), userLogin, String.valueOf(0));
                        ++i;
                    }
                }
            }

            usersRS.close();
            monthlyRankingStmt.close();
            totalRankingStmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Method that initializes Spinner that allows to choose period of the ranking
     */
    public void initSpinner() {

        ArrayAdapter<CharSequence> rankingPeriodAdapter;

        periodSpinner = rootView.findViewById(R.id.rankingPeriodSpinner);
        selectedRankingPeriod = "Miesięczny";

        rankingPeriodAdapter = ArrayAdapter.createFromResource(getContext(), R.array.rankingPeriods, android.R.layout.simple_spinner_item);
        rankingPeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(rankingPeriodAdapter);

        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedRankingPeriod = periodSpinner.getSelectedItem().toString();
                users.clear();
                learnedWordsNumber.clear();
                tableLayout.removeAllViews();
                initUserTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
