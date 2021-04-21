package com.pcamarounds.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.pcamarounds.R;
import com.pcamarounds.constants.AppConstants;
import com.pcamarounds.databinding.ActivityTermAndConditionBinding;
import com.pcamarounds.retrofit.Environment;
import com.pcamarounds.utils.AppProgressDialog;
import com.pcamarounds.utils.Utility;

import java.util.Locale;

public class TermAndConditionActivity extends AppCompatActivity {
    private static final String TAG = "TermAndConditionActivit";
    private Context context;
    private ActivityTermAndConditionBinding binding;
    private String key = "";
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.showTransparentStatusBar(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_term_and_condition);
        context = this;
        dialog = new Dialog(context);
        Utility.hideKeyboardNew(this);
        initView();
    }

    private void initView() {
        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setUpToolbar();
        setup_webview(AppConstants.TERMANDPOLICYURL+"terms");
    }

    private void setup_webview(String url) {
        AppProgressDialog.show(dialog);
        WebSettings settings = binding.webview.getSettings();
        settings.setJavaScriptEnabled(true);
        binding.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        binding.webview.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView webView1, int newProgress) {
                if (newProgress == 100) {
                    AppProgressDialog.hide(dialog);
                }
            }
        });
        binding.webview.loadUrl(url);
        binding.webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(TAG, "shouldOverrideUrlLoading: "+ url );
                return true;
            }
        });

    }

    private void setUpToolbar() {
        setSupportActionBar(binding.myToolbar.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.myToolbar.toolbarTitle.setText(context.getResources().getString(R.string.terms_and_conditions));
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
        super.onBackPressed();
        finish();
    }
}
