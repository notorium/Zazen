package com.example.zazen.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.zazen.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }

    public void config(View v) {
        Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
        startActivity(intent);

    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                //.setTitle("開始前確認")
                .setCancelable(false)
                .setMessage("アプリを終了しますか？")
                .setPositiveButton("はい", (dialog, which) -> {
                    this.finish();
                    this.moveTaskToBack(true);
                })
                .setNegativeButton("いいえ", null)
                .show();

    }

    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}