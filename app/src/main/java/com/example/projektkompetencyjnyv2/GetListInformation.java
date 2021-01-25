package com.example.projektkompetencyjnyv2;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class that gets information about a specific list
 */
public class GetListInformation implements Runnable {

    private static final String TAG = "GetListInformation";
    private final Object lock;
    private final int listId;
    private final int ownerId;
    private final int difficultyLevel;
    private final String listName;
    private final ArrayList<String> listNames;
    private final ArrayList<Integer> difficultyLevels;
    private final ArrayList<Integer> wordQuantities;
    private final ArrayList<Integer> learnedQuantities;
    private final ArrayList<String> owners;
    private int userId;

    public GetListInformation(int userId, Object lock, int listId, int ownerId, int difficultyLevel, String listName, ArrayList<String> listNames, ArrayList<Integer> difficultyLevels, ArrayList<Integer> wordQuantities, ArrayList<Integer> learnedQuantities, ArrayList<String> owners) {
        this.lock = lock;
        this.listId = listId;
        this.ownerId = ownerId;
        this.difficultyLevel = difficultyLevel;
        this.listName = listName;
        this.listNames = listNames;
        this.difficultyLevels = difficultyLevels;
        this.wordQuantities = wordQuantities;
        this.learnedQuantities = learnedQuantities;
        this.owners = owners;
        this.userId = userId;

    }

    @Override
    public void run() {

        ResultSet ownerRS, wordsRS, learnedRS = null;
        PreparedStatement ownerStmt, wordsStmt, learnedStmt;
        String owner;
        int wordsQuantity, learnedQuantity;

        try {
            Log.d(TAG, "GetListInformation: list: " + listName);

            ConnectionClass connectionClass = new ConnectionClass();
            Connection con = connectionClass.CONN();

            //prepared statements, begin
            ownerStmt = con.prepareStatement("" +
                    "select login from [User] where id_user=?");

            wordsStmt = con.prepareStatement("" +
                    "select count(id_progress) as wordsQuantity\n" +
                    "from progress \n" +
                    "where id_list=? \n" +
                    "group by id_list");

            learnedStmt = con.prepareStatement("" +
                    "select count(id_progress) as learnedQuantity\n" +
                    "from progress \n" +
                    "where id_list=? and learned=1 and id_user=? \n" +
                    "group by id_list");
            //prepared statements, end


            //getting list owner's login
            ownerStmt.setInt(1, ownerId);
            ownerRS = ownerStmt.executeQuery();

            if (ownerRS.next())
                owner = ownerRS.getString("login");
            else
                owner = "-----";


            //getting learned and total number of words in the list
            wordsStmt.setInt(1, listId);
            wordsRS = wordsStmt.executeQuery();

            if (wordsRS.next()) {
                wordsQuantity = wordsRS.getInt("wordsQuantity");

                learnedStmt.setInt(1, listId);
                learnedStmt.setInt(2, userId);
                learnedRS = learnedStmt.executeQuery();

                if (learnedRS.next())
                    learnedQuantity = learnedRS.getInt("learnedQuantity");
                else
                    learnedQuantity = 0;
            } else {
                wordsQuantity = 0;
                learnedQuantity = 0;
            }

            synchronized (lock) {
                listNames.add(listName);
                difficultyLevels.add(difficultyLevel);
                wordQuantities.add(wordsQuantity);
                learnedQuantities.add(learnedQuantity);
                owners.add(owner);
            }

            if (ownerRS != null)
                ownerRS.close();
            ownerStmt.close();

            if (wordsRS != null)
                wordsRS.close();
            wordsStmt.close();

            if (learnedRS != null)
                learnedRS.close();
            learnedStmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
