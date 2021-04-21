package com.pcamarounds.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.databinding.ActivityImageSliderBinding;
import com.pcamarounds.models.ItemImagesModel;
import com.pcamarounds.utils.Utility;


import java.util.ArrayList;
import java.util.List;

public class ImageSliderActivity extends AppCompatActivity {
    private static final String TAG = "ImageSliderActivity";
    private Context context;
    private ActivityImageSliderBinding binding;
    private List<ItemImagesModel> mImagesModelList = new ArrayList<>();
    private String user_id = "";
    private int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_slider);
        context = this;
        initView();
    }

    private void initView() {
        user_id = getIntent().getStringExtra(AppConstants.USER_ID);
        pos = getIntent().getIntExtra(AppConstants.POSITION,0);
        mImagesModelList = ((List<ItemImagesModel>) getIntent().getExtras().getSerializable(AppConstants.LIST));
        ImageSliderAdapter adapter = new ImageSliderAdapter(context, mImagesModelList,user_id);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setCurrentItem(pos, true);
        binding.ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    static class ImageSliderAdapter extends PagerAdapter {
        private static final String TAG = "DashboardSlideradapter";
        private List<ItemImagesModel> images;
        private LayoutInflater inflater;
        private Context context;
        private String user_id = "";
        String imageUrl = "";
        public ImageSliderAdapter(Context context, List<ItemImagesModel> images,String udi){
            this.context = context;
            this.images = images;
            this.user_id = udi;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position){
            View myImageLayout  = inflater.inflate(R.layout.image_slider_cell, view, false);
            view.addView(myImageLayout, 0);

            com.github.chrisbanes.photoview.PhotoView photoView = view.findViewById(R.id.image);
            ProgressBar loading = view.findViewById(R.id.loading);

            if (images.get(position).getmType() != null && !images.get(position).getmType().equals(""))
            {
                imageUrl =  "file://" + images.get(position).getImage();
            }else {
                imageUrl = AppConstants.PROFILE_WORK +images.get(position).getImage();
            }

            loading.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUrl).error(R.color.colorGray).into(photoView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    loading.setVisibility(View.GONE);

                }
                @Override
                public void onError(Exception e) {
                    loading.setIndeterminate(false);
                    loading.setBackgroundColor(Color.LTGRAY);
                }
            });
            return myImageLayout;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view.equals(o);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
