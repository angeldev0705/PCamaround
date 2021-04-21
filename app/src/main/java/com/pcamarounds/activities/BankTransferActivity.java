package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.pcamarounds.R;
import com.pcamarounds.databinding.ActivityBankTransferBinding;
import com.pcamarounds.utils.Utility;

public class BankTransferActivity extends AppCompatActivity {
    private static final String TAG = "BankTransferActivity";
    private Context context;
    private ActivityBankTransferBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_bank_transfer);
        context = this;
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        setUpToolbar();
    }
    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.bank_transfer));
        binding.myToolbar.myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        binding.myToolbar.myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
