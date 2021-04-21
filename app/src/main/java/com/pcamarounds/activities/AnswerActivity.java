package com.pcamarounds.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.databinding.ActivityAnswerBinding;
import com.pcamarounds.utils.Utility;


public class AnswerActivity extends AppCompatActivity {
    private static final String TAG = "AnswerActivity";
    private Context context;
    private ActivityAnswerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer);
        context = this;
        initView();
    }

    private void initView() {
        String que = getIntent().getStringExtra(AppConstants.QUE);
        String ans = getIntent().getStringExtra(AppConstants.ANS);
        binding.tvQue.setText(que);
        binding.tvAns.setText(ans);
        setUpToolbar();

        binding.tvQue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ContactUsActivity.class);
                startActivity(intent);
            }
        });
        binding.tvAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ContactUsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.help_center));
        binding.myToolbar.myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        binding.myToolbar.myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}