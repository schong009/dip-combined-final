package com.example.junhaozeng.testdesign.Fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.example.junhaozeng.testdesign.R;

public class PetFragment extends Fragment {
    public static PetFragment newInstance() {
        PetFragment fragment = new PetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ImageView img;
    private ImageView img2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.petanimation, container,
                false);
        img = (ImageView) rootView.findViewById(R.id.idle);
        img2 = (ImageView) rootView.findViewById(R.id.walk);
        img2.setVisibility(View.INVISIBLE);

        final Button button =(Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                img.setVisibility(View.INVISIBLE);
                img2.setVisibility(View.VISIBLE);
                img2 = (ImageView) rootView.findViewById(R.id.walk);

                img.post(new Runnable() {

                    @Override
                    public void run() {
                        ((AnimationDrawable) img2.getBackground()).start();
                    }

                });
            }
        });

        img.post(new Runnable() {

            @Override
            public void run() {
                ((AnimationDrawable) img.getBackground()).start();
            }

        });

        return rootView;
    }
}


