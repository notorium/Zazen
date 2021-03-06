package com.example.zazen.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

        login();

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
            if (loginStatus.getBoolean("LoginFlg", false)) {
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("??????????????????????????????")
                        .setPositiveButton("??????", (dialog, which) -> {
                            logout();
                        })
                        .setNegativeButton("?????????", null)
                        .show();
            } else {
                loginOpenFlg = true;
                startButton.setEnabled(false);
                ruleButton.setEnabled(false);
                loginScreen.setVisibility(View.VISIBLE);
            }
        });
    }

    public void login() {
        if (loginStatus.getBoolean("LoginFlg", false)) {
            loginMenuButton.setText("???????????????");
            loginMenuButton.setBackgroundColor(getResources().getColor(R.color.red));
        } else {
            loginMenuButton.setText("????????????");
            loginMenuButton.setBackgroundColor(getResources().getColor(R.color.green));
            login_editor.putString("UserId", "").apply();
            login_editor.putString("UserName", "?????????").apply();
            login_editor.putString("Password", "").apply();
            login_editor.putBoolean("LoginFlg", false).apply();

        }
        userid.setText("ID:" + loginStatus.getString("UserId", ""));
        username.setText(loginStatus.getString("UserName", "?????????"));
    }

    public void logout() {
        loginMenuButton.setText("????????????");
        loginMenuButton.setBackgroundColor(getResources().getColor(R.color.green));
        login_editor.putString("UserId", "").apply();
        login_editor.putString("UserName", "?????????").apply();
        login_editor.putString("Password", "").apply();
        login_editor.putBoolean("LoginFlg", false).apply();

        userid.setText(loginStatus.getString("UserId", ""));
        username.setText(loginStatus.getString("UserName", "?????????"));
        LoginFragment.loginButton.setEnabled(true);
        LoginFragment.passwordInput.setEnabled(true);
        LoginFragment.useridInput.setEnabled(true);
        LoginFragment.passwordInput.setText("");
        LoginFragment.useridInput.setText("");
        LoginFragment.errorText.setText("");
    }

    public void config(View v) {
        Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
        startActivity(intent);
    }

    public void web(View v){
        Intent intent = new Intent(getApplicationContext(), WebActivity.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        if (loginOpenFlg) {
            loginOpenFlg = false;
            startButton.setEnabled(true);
            ruleButton.setEnabled(true);
            loginScreen.setVisibility(View.GONE);
            if (loginStatus.getBoolean("LoginFlg", false)) {
                login();
            }
        } else {
            new AlertDialog.Builder(this)
                    //.setTitle("???????????????")
                    .setCancelable(false)
                    .setMessage("?????????????????????????????????")
                    .setPositiveButton("??????", (dialog, which) -> {
                        this.finish();
                        this.moveTaskToBack(true);
                    })
                    .setNegativeButton("?????????", null)
                    .show();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}