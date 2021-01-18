package com.example.projektkompetencyjnyv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Class that shows user's lists and allows him to add, delete or learn selected list
 */
public class WordListsFragment extends Fragment {

    private static final String TAG = "WordLists";
    private View rootView;
    private int userId;
    private Connection con;
    private ArrayList<String> listNames;
    private ArrayList<Integer> difficultyLevels;
    private ArrayList<Integer> wordQuantities;
    private ArrayList<Integer> learnedQuantities;
    private ArrayList<String> owners;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: created word lists");
        rootView = inflater.inflate(R.layout.activity_word_lists, container, false);

        CurrentUser currentUser = new CurrentUser(getActivity().getApplicationContext());
        userId = currentUser.getId();

        listNames = new ArrayList<>();
        difficultyLevels = new ArrayList<>();
        wordQuantities = new ArrayList<>();
        learnedQuantities = new ArrayList<>();
        owners = new ArrayList<>();

        ConnectionClass connectionClass = new ConnectionClass();
        con = connectionClass.CONN();

        initWordLists();
        initRecyclerView();

        FloatingActionButton actionButton = rootView.findViewById(R.id.listsFloatingButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu listPopupMenu = new PopupMenu(getActivity(), v);
                listPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.searchList:
                                intent = new Intent(getActivity(), AddFromPublicLists.class);
                                startActivity(intent);
                                return true;
                            case R.id.createList:
                                intent = new Intent(getActivity(), CreateNewList.class);
                                startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                listPopupMenu.inflate(R.menu.lists_popup_menu);
                listPopupMenu.show();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {

        Log.d(TAG, "onResume: resumed word lists");
        super.onResume();
    }

    /**
     * Method that initializes user's lists
     */
    private void initWordLists() {

        Log.d(TAG, "initWordLists: initialization of user's lists");

        int listId, ownerId, difficultyLevel;
        String listName;
        final Object lock;
        ResultSet listsRS;
        PreparedStatement listsStmt;
        Thread thread = null;

        try {
            if (con != null) {

                //creating a lock for threads
                lock = new Object();

                //getting information about user's lists
                listsStmt = con.prepareStatement(
                        "select name, difficulty_level, owner_id, id_wordList\n" +
                                "from Word_list as wl inner join [User_WordList] as uwl \n" +
                                "\ton wl.id_word_list=uwl.id_wordList \n" +
                                "where uwl.id_user=?");
                listsStmt.setInt(1, userId);
                listsRS = listsStmt.executeQuery();

                while (listsRS.next()) {

                    listName = listsRS.getString("name");
                    difficultyLevel = listsRS.getInt("difficulty_level");
                    listId = listsRS.getInt("id_wordList");
                    ownerId = listsRS.getInt("owner_id");

                    //thread that gets information about the list
                    thread = new Thread(new GetListInformation(lock, listId, ownerId, difficultyLevel, listName, listNames, difficultyLevels, wordQuantities, learnedQuantities, owners));
                    thread.start();
                }
                thread.join();

                listsRS.close();
                listsStmt.close();
            } else {
                Toast.makeText(getActivity(), "Błąd połączenia z bazą", Toast.LENGTH_LONG).show();
            }
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Method that initializes recyclerview
     */
    public void initRecyclerView() {
        Log.d(TAG, "initRecyvlerView: initialization of a recyclerview");

        RecyclerView listsRecView = rootView.findViewById(R.id.listsRecView);
        listsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WordListsAdapter adapter = new WordListsAdapter(getActivity(), listNames, difficultyLevels, wordQuantities, learnedQuantities, owners);
        listsRecView.setAdapter(adapter);
    }
}