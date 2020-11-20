package com.example.projektkompetencyjnyv2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder>{

    private static final String TAG = "WordsAdapter";
    private ArrayList<String> words;
    private ArrayList<String> meanings;
    private ArrayList<String> sentences;
    private Context mContext;

    public WordsAdapter(Context mContext, ArrayList<String> words, ArrayList<String> meanings, ArrayList<String> sentences) {
        this.words = words;
        this.meanings = meanings;
        this.sentences = sentences;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.words_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.wordTxt.setText(words.get(position));
        holder.meaningTxt.setText(meanings.get(position));
        holder.sentenceTxt.setText(sentences.get(position));

        holder.wordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: "+words.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView wordTxt;
        TextView meaningTxt;
        TextView sentenceTxt;
        RelativeLayout wordLayout;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            wordTxt=itemView.findViewById(R.id.wordTxt);
            meaningTxt=itemView.findViewById(R.id.meaningTxt);
            sentenceTxt=itemView.findViewById(R.id.sentenceTxt);
            wordLayout=itemView.findViewById(R.id.wordsLayout);
        }
    }
}



