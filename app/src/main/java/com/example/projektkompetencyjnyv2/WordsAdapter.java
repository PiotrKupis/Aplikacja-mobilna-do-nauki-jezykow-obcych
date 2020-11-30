package com.example.projektkompetencyjnyv2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.wordsViewHolder> {

    private static final String TAG = "WordsAdapter";
    private ArrayList<String> words;
    private ArrayList<String> meanings;
    private ArrayList<String> sentences;
    private CurrentUser currentUser;

    public WordsAdapter(Context mContext, ArrayList<String> words, ArrayList<String> meanings, ArrayList<String> sentences) {
        this.words = words;
        this.meanings = meanings;
        this.sentences = sentences;
        currentUser = new CurrentUser(mContext.getApplicationContext());
    }

    @NonNull
    @Override
    public wordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.words_item, parent, false);
        wordsViewHolder holder = new wordsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull wordsViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.wordTxt.setText(words.get(position));
        holder.meaningTxt.setText(meanings.get(position));
        holder.sentenceTxt.setText(sentences.get(position));

        holder.wordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + words.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class wordsViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, PopupMenu.OnMenuItemClickListener{

        TextView wordTxt;
        TextView meaningTxt;
        TextView sentenceTxt;
        RelativeLayout wordLayout;

        public wordsViewHolder(@NonNull View itemView) {

            super(itemView);
            wordTxt = itemView.findViewById(R.id.wordTxt);
            meaningTxt = itemView.findViewById(R.id.meaningTxt);
            sentenceTxt = itemView.findViewById(R.id.sentenceTxtPolish);
            wordLayout = itemView.findViewById(R.id.wordsLayout);

            wordLayout.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {

            if(currentUser.getId()==currentUser.getCurrentListOwnerId())
                showPopupMenu(v);

            return false;
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.words_functions_popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (item.getItemId() == R.id.deleteWord) {
                Log.d(TAG, "onMenuItemClick: usuwanie słowa z listy: " + getAdapterPosition());
                removeWordsItem(getAdapterPosition());
                return true;
            }
            return false;
        }

        public void removeWordsItem(int position) {

            int wordId;
            ConnectionClass connectionClass;
            Connection con;
            ResultSet rs;
            PreparedStatement stmt;

            connectionClass = new ConnectionClass();
            con = connectionClass.CONN();

            try {
                //pobranie id słowa
                stmt = con.prepareStatement("" +
                        "select w.id_word as id_word\n" +
                        "from Word as w inner join Word_list as wl on w.id_list=wl.id_word_list\n" +
                        "where wl.owner_id=? and wl.name=? and word=? and meaning=?");

                stmt.setInt(1,currentUser.getCurrentListOwnerId());
                stmt.setString(2, currentUser.getCurrentListName());
                stmt.setString(3, words.get(position));
                stmt.setString(4, meanings.get(position));
                rs = stmt.executeQuery();

                if (rs.next()) {
                    wordId=rs.getInt("id_word");
                    rs.close();
                    stmt.close();

                    stmt = con.prepareStatement("" +
                            "delete\n" +
                            "from Word\n" +
                            "where id_word=?");
                    stmt.setInt(1, wordId);
                    stmt.executeUpdate();

                    words.remove(position);
                    meanings.remove(position);
                    sentences.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, words.size());

                    rs.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }
}



