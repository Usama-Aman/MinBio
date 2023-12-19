package com.vic.vicwsp.Views.Activities;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vic.vicwsp.R;

public class WebViewActivity extends AppCompatActivity {


    private WebView webView;
    private ImageView ivToolbarBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        String content = getIntent().getStringExtra("content");

        ivToolbarBack = findViewById(R.id.ivToolbarBack);
        ivToolbarBack.setOnClickListener(v -> {
            finish();
        });

        webView = findViewById(R.id.webView1);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadData(content, "text/html", "UTF-8");

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);

    }
}
