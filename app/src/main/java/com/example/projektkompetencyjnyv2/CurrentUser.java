package com.example.projektkompetencyjnyv2;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CurrentUser extends Application {
    private  SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public CurrentUser()
    {

    }
    public CurrentUser(Context context)
    {
        pref = context.getSharedPreferences("PROJECT_NAME", Context.MODE_PRIVATE);
     //   SharedPreferences sharedPref = context.getSharedPreferences(
       //         getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    public void cleardata()
    {
        editor.clear();
        editor.apply();
    }
    public String getPassword() {
        return pref.getString("password", null);
    }

    public void setPassword(String password)
    {
        editor.putString("password", password);
        editor.apply();
    }

    public int getId() {
        return  pref.getInt("id", 0);
    }
    public void setId(int id)
    {
        editor.putInt("id", id);
        editor.apply();
    }

    public void setlogin(String login)
    {
        editor.putString("login", login);
        editor.apply();
    }
    public String getlogin() {
        return  pref.getString("login", null);
    }

    public String getCurrentListName() {
        return  pref.getString("currentListName", null);
    }
    public void setCurrentListName(String currentListName)
    {
        editor.putString("currentListName", currentListName);
        editor.apply();
    }

    public String getCurrentListOwner() {
        return  pref.getString("currentListOwner", null);
    }
    public void setCurrentListOwner(String currentListOwner)
    {
        editor.putString("currentListOwner", currentListOwner);
        editor.apply();
    }

    public int getCurrentListOwnerId() {
        return  pref.getInt("currentListOwnerId", 0);
    }
    public void setCurrentListOwnerId(int currentListOwnerId)
    {
        editor.putInt("currentListOwnerId", currentListOwnerId);
        editor.apply();
    }
}
