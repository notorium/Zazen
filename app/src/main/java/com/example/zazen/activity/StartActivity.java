package com.example.zazen.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.zazen.R;

public class StartActivity extends AppCompatActivity {
    private Button startButton, ruleButton, loginMenuButton;
    private ImageButton accountIcon;
    private TextView username, userid;
    private View loginMenu, loginScreen;

    public static SharedPreferences loginStatus;
    public static SharedPreferences.Editor login_editor;

    private boolean accountOpenFlg = true, loginOpenFlg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ruleButton = findViewById(R.id.ruleButton);
        loginScreen = findViewById(R.id.loginScreen);

        loginMenu = findViewById(R.id.loginMenu);
        loginMenuButton = findViewById(R.id.loginMenuButton);
        username = findViewById(R.id.username);
        userid = findViewById(R.id.userid);
        accountIcon = findViewById(R.id.accountIcon);
        startButton = findViewById(R.id.startButton);

        loginStatus = getSharedPreferences("Login", MODE_PRIVATE);
        login_editor = loginStatus.edit();

        if (!loginStatus.getBoolean("LoginFlg", false)) {
            login_editor.putString("UserId", "").apply();
            login_editor.putString("UserName", "ゲストユーザー").apply();
            login_editor.putString("Password", "").apply();
            userid.setText(loginStatus.getString("UserID", ""));
            username.setText(loginStatus.getString("UserName", "ゲストユーザー"));
        } else {

        }

        accountIcon.setOnClickListener(v -> {
            if (accountOpenFlg) {
                loginMenu.setVisibility(View.VISIBLE);
                accountOpenFlg = false;
            } else {
                loginMenu.setVisibility(View.GONE);
                accountOpenFlg = true;
            }
        });

        loginMenuButton.setOnClickListener(v -> {
            loginOpenFlg = true;
            startButton.setEnabled(false);
            ruleButton.setEnabled(false);
            loginScreen.setVisibility(View.VISIBLE);
        });

    }

    public void login() {

    }

    public void config(View v) {
        Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        if (loginOpenFlg) {
            loginOpenFlg = false;
            startButton.setEnabled(true);
            ruleButton.setEnabled(true);
            loginScreen.setVisibility(View.GONE);
        } else {
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
    }

    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}