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

public class WordListsAdapter extends RecyclerView.Adapter<WordListsAdapter.ViewHolder>{

    private static final String TAG = "WordListsAdapter";
    private ArrayList<String> listNames;
    private ArrayList<Integer> difficultyLevels;
    private ArrayList<Integer> wordQuantities;
    private ArrayList<Integer> learnedQuantities;
    private ArrayList<String> owners;
    private Context mContext;

    public WordListsAdapter(Context mContext,ArrayList<String> listNames, ArrayList<Integer> difficultyLevels,ArrayList<Integer> wordQuantities,ArrayList<Integer> learnedQuantities,ArrayList<String> owners) {
        this.listNames = listNames;
        this.difficultyLevels = difficultyLevels;
        this.mContext = mContext;
        this.wordQuantities=wordQuantities;
        this.learnedQuantities=learnedQuantities;
        this.owners=owners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.word_list_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.listNameBtn.setText(listNames.get(position));
        holder.difficultyRB.setRating(difficultyLevels.get(position));
        holder.progressBar.setProgress(learnedQuantities.get(position));
        holder.progressBar.setMax(wordQuantities.get(position));
        String progressTxt=learnedQuantities.get(position)+"/"+wordQuantities.get(position);
        holder.progressTxt.setText(progressTxt);
        Log.d(TAG, owners.get(position));
        holder.ownerTxt.setText("Utworzone przez: "+owners.get(position));

        holder.listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: "+listNames.get(position));
                Toast.makeText(mContext, listNames.get(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, Words.class);
                intent.putExtra("listName",listNames.get(position));
                intent.putExtra("owner",owners.get(position));
                mContext.startActivity(intent);
            }
        });

        holder.listNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "cliecked button", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, Words.class);
                intent.putExtra("listName",listNames.get(position));
                intent.putExtra("owner",owners.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        Button listNameBtn;
        RatingBar difficultyRB;
        ProgressBar progressBar;
        TextView progressTxt;
        RelativeLayout listLayout;
        TextView ownerTxt;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            listNameBtn=itemView.findViewById(R.id.listNameBtn);
            difficultyRB=itemView.findViewById(R.id.difficultyRB);
            progressBar=itemView.findViewById(R.id.progressBar);
            progressTxt=itemView.findViewById(R.id.progressTxt);
            listLayout=itemView.findViewById(R.id.listLayout);
            ownerTxt=itemView.findViewById(R.id.ownerTxt);
        }
    }
}



