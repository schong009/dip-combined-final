package com.example.junhaozeng.testdesign.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.junhaozeng.testdesign.Activity.HistoryActivity;
import com.example.junhaozeng.testdesign.R;

public class HealthFragment extends Fragment {

    public static HealthFragment newInstance() {
        HealthFragment fragment = new HealthFragment();
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private ImageView stepImage;
    private ImageView heartImage;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_health, container,
                false);
        stepImage = (ImageView) rootView.findViewById(R.id.stepImage);
        heartImage = (ImageView) rootView.findViewById(R.id.heartImage);

        stepImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HistoryActivity.class);
                getActivity().startActivity(intent);

            }
        });

        heartImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });


        return rootView;
    }



}
