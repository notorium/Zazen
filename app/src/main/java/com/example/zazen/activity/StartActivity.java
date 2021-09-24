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
import com.example.zazen.async.HttpRequest_GET;

public class StartActivity extends AppCompatActivity {
    private Button loginButton, loginMenuButton;
    private ImageButton accountIcon;
    private TextView username, userid, errorText;
    private EditText useridInput, passwordInput;
    private View loginMenu, loginScreen;

    static SharedPreferences loginStatus;
    static SharedPreferences.Editor editor;

    private boolean accountFlg = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loginMenu = findViewById(R.id.loginMenu);
        loginMenuButton = findViewById(R.id.loginMenuButton);
        username = findViewById(R.id.username);
        userid = findViewById(R.id.userid);
        accountIcon = findViewById(R.id.accountIcon);

        loginScreen = findViewById(R.id.loginScreen);
        loginButton = findViewById(R.id.loginMenuButton);
        errorText = findViewById(R.id.errorText);
        useridInput = findViewById(R.id.userid_editText);
        passwordInput = findViewById(R.id.password_editText);

        accountIcon.setOnClickListener(v -> {
            if (accountFlg) {
                loginMenu.setVisibility(View.VISIBLE);
                accountFlg = false;
            } else {
                loginMenu.setVisibility(View.GONE);
                accountFlg = true;
            }
        });

        loginMenuButton.setOnClickListener(v -> {
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