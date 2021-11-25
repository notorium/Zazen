package com.example.zazen.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.zazen.R;

public class WebActivity2 extends AppCompatActivity {
    private WebView webView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web2);

        webView = findViewById(R.id.webView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        View decor = getWindow().getDecorView();
//        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            // TODO Auto-generated method stub

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType(mimetype);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        webView.loadUrl("https://zazethcare.cloud/tutorial.php");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean result = true;
        switch (id) {
            case android.R.id.home:
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Webページを閉じて戻りますか？")
                        .setPositiveButton("はい", (dialog, which) -> {
                            this.finish();
                        })
                        .setNegativeButton("いいえ", null)
                        .show();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }


    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Webページを閉じて戻りますか？")
                .setPositiveButton("はい", (dialog, which) -> {
                    this.finish();
                })
                .setNegativeButton("いいえ", null)
                .show();
    }
}