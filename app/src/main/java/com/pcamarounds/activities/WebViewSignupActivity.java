package com.pcamarounds.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.databinding.ActivityWebViewSignupBinding;
import com.pcamarounds.utils.Utility;

import java.util.Locale;

public class WebViewSignupActivity extends AppCompatActivity {
    private static final String TAG = "WebViewSignupActivity";
    private Context context;
    private ActivityWebViewSignupBinding binding;
    private String url = "https://www.google.com/";
    private String flaglogin = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_web_view_signup);
        context = this;
        initView();
    }

    private void initView() {
        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        getIntentData();
        setUpToolbar();
        WebSettings settings = binding.webview.getSettings();
        settings.setJavaScriptEnabled(true);
        binding.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
            binding.webview.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    Log.e(TAG, "onPageFinished: " + url);
                }
            });
        binding.webview.loadUrl(url);

    }
    private void getIntentData() {
        if (getIntent() != null) {
            flaglogin = getIntent().getStringExtra(AppConstants.FLAGLOGIN);
            // Log.e(TAG, "getIntentData: " + flaglogin);
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.sign_up1));
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
