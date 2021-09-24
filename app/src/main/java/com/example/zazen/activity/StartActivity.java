package com.example.zazen.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.zazen.R;
import com.example.zazen.async.HttpRequest_GET;

public class StartActivity extends AppCompatActivity {
    private Button loginButton;
    private TextView username,userid,accountIcon;
    static SharedPreferences loginStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loginButton = findViewById(R.id.loginMenuButton);
        username=findViewById(R.id.username);
        userid=findViewById(R.id.userid);
        HttpRequest_GET httpRequestPost = new HttpRequest_GET(this);
        httpRequestPost.execute("http://fukuiohr2.sakura.ne.jp/2021/Zazen/postdata.php");

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