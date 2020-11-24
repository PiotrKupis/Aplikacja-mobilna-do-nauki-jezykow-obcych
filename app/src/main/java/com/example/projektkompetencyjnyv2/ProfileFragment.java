package com.example.projektkompetencyjnyv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container,false);

        CurrentUser currentUser = new CurrentUser(getActivity().getApplicationContext());

        TextView text =(TextView)  view.findViewById(R.id.nameText);
        text.setText("Login: " +currentUser.getlogin() );
        TextView TextID =(TextView)  view.findViewById(R.id.TextID);
        TextID.setText("ID: " +currentUser.getId() );
        Button button = view.findViewById(R.id.buttonlogout);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentUser.cleardata();
            }
        });
        return view;
    }
    /*
    public void setprofile()
    {
        CurrentUser currentUser = new CurrentUser(getActivity().getApplicationContext());
        TextView text =(TextView)  findViewById(R.id.nameText);
    }
     */
}
