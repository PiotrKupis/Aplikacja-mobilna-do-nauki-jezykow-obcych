package com.example.projektkompetencyjnyv2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.wordsViewHolder> {

    private static final String TAG = "WordsAdapter";
    private ArrayList<String> words;
    private ArrayList<String> meanings;
    private ArrayList<String> sentences;

    public WordsAdapter(Context mContext, ArrayList<String> words, ArrayList<String> meanings, ArrayList<String> sentences) {
        this.words = words;
        this.meanings = meanings;
        this.sentences = sentences;
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

    public class wordsViewHolder extends RecyclerView.ViewHolder {

        TextView wordTxt;
        TextView meaningTxt;
        TextView sentenceTxt;
        RelativeLayout wordLayout;

        public wordsViewHolder(@NonNull View itemView) {

            super(itemView);
            wordTxt = itemView.findViewById(R.id.wordTxt);
            meaningTxt = itemView.findViewById(R.id.meaningTxt);
            sentenceTxt = itemView.findViewById(R.id.sentenceTxt);
            wordLayout = itemView.findViewById(R.id.wordsLayout);
        }
    }
}



