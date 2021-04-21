package com.pcamarounds.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.pcamarounds.R;
import com.pcamarounds.activities.LoginActivity;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    private ArrayList<Integer> images;
    private LayoutInflater inflater;
    private Context context;

    public SliderAdapter(Context context, ArrayList<Integer> images) {
        this.context = context;
        this.images = images;


        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.slider_cell, view, false);

        ImageView myImage = myImageLayout.findViewById(R.id.ivImage);
        ImageView ivSliderSecondImg = myImageLayout.findViewById(R.id.ivSliderSecondImg);
        ImageView ivSliderImg = myImageLayout.findViewById(R.id.ivSliderImg);
        TextView tvOffering = myImageLayout.findViewById(R.id.tvOffering);
        TextView tvWelcome = myImageLayout.findViewById(R.id.tvWelcome);
        LinearLayout llAccessExplore = myImageLayout.findViewById(R.id.llAccessExplore);
        TextView btnSkip = myImageLayout.findViewById(R.id.btnSkip);
        com.google.android.material.button.MaterialButton btnNext = myImageLayout.findViewById(R.id.btnNext);
        com.google.android.material.button.MaterialButton btnToAccess = myImageLayout.findViewById(R.id.btnToAccess);
        com.google.android.material.button.MaterialButton btnToExplore = myImageLayout.findViewById(R.id.btnToExplore);


        if (position == 0) {
            myImage.setImageDrawable(context.getDrawable(R.drawable.sliderpro_image1));
            ivSliderImg.setVisibility(View.VISIBLE);
            ivSliderSecondImg.setVisibility(View.GONE);
            tvWelcome.setText(context.getResources().getString(R.string.welcome_to_camarounds));
            tvOffering.setText(context.getResources().getString(R.string.offering_your_services_has_never_been_so_neasy));
            //myImage.setVisibility(View.VISIBLE);
            btnSkip.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            llAccessExplore.setVisibility(View.GONE);
        } else if (position == 1) {
            myImage.setImageDrawable(context.getDrawable(R.drawable.sliderpro_image2));
            ivSliderImg.setVisibility(View.GONE);
            ivSliderSecondImg.setVisibility(View.VISIBLE);
            tvWelcome.setText(context.getResources().getString(R.string.get_requests));
            tvOffering.setText(context.getResources().getString(R.string.hundreds));
           // myImage.setVisibility(View.GONE);
            btnSkip.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
            llAccessExplore.setVisibility(View.VISIBLE);
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        btnToAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        btnToExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

}
