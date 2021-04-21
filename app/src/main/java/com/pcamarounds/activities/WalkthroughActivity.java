package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.pcamarounds.R;
import com.pcamarounds.adapters.SliderAdapter;
import com.pcamarounds.databinding.ActivityWalkthroughBinding;
import com.pcamarounds.utils.Utility;

import java.util.ArrayList;
import java.util.Locale;

public class WalkthroughActivity extends AppCompatActivity {
    private static final String TAG = "WalkthroughActivity";
    private Context context;
    private ActivityWalkthroughBinding binding;
    private static Integer[] img = {1,2};
    private ArrayList<Integer> ImgArray = new ArrayList<Integer>();
    private int currentPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_walkthrough);
        context = this;
        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        initView();
    }

    private void initView() {
         sliderSetup();
    }

    private void sliderSetup() {
        for (int i = 0; i < img.length; i++)
            ImgArray.add(img[i]);

        binding.pager.setAdapter(new SliderAdapter(context, ImgArray));
        binding.tabDots.setupWithViewPager(binding.pager, true);

        for(int i=0; i < binding.tabDots.getTabCount(); i++) {
            View tab = ((ViewGroup) binding.tabDots.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 6, 0);
            tab.requestLayout();
        }
    }

}
