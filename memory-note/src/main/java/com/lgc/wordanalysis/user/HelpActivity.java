package com.lgc.wordanalysis.user;

import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.lgc.wordanalysis.R;
import com.lgc.wordanalysis.base.AppConfig;

public class HelpActivity extends AppCompatActivity {
    private final String url = "https://zhuanlan.zhihu.com/p/53787762";
    private boolean isGoBack = false;
    private static long backTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (System.currentTimeMillis() - backTime < 2000) {
            finish();
        }
        setContentView(R.layout.activity_main);
        WebView webView = findViewById(R.id.web_view);
        webView.loadUrl(url);
        findViewById(R.id.browser_open).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://zhuanlan.zhihu.com/p/53787762");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        findViewById(R.id.help_return_btn).setOnClickListener(v -> {
            finish();
        });
        AppConfig.putLookHelp(true);
    }

    @Override
    public void onBackPressed() {
        backTime = System.currentTimeMillis();
        super.onBackPressed();
    }
}
