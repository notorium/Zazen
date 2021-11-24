package com.example.zazen.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.zazen.R;

public class StartActivity extends AppCompatActivity {
    private Button startButton, webButton, loginMenuButton, nomalModeButton, freeModeButton;
    private ImageButton accountIcon, help;
    private TextView username, userid;
    private View loginMenu, loginScreen, selectionScreen;

    public static SharedPreferences loginStatus;
    public static SharedPreferences.Editor login_editor;

    private boolean accountOpenFlg = true, loginOpenFlg = false, selectionOpenFlg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        webButton = findViewById(R.id.webButton);
        loginScreen = findViewById(R.id.loginScreen);

        selectionScreen = findViewById(R.id.selectionScreen);
        nomalModeButton = findViewById(R.id.nomalModeButton);
        freeModeButton = findViewById(R.id.freeModeButton);
        help = findViewById(R.id.help);

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
                        .setMessage("ログアウトしますか？")
                        .setPositiveButton("はい", (dialog, which) -> {
                            logout();
                        })
                        .setNegativeButton("いいえ", null)
                        .show();
            } else {
                loginOpenFlg = true;
                loginScreen.setVisibility(View.VISIBLE);
            }
        });

        startButton.setOnClickListener(v -> {
            selectionOpenFlg = true;
            selectionScreen.setVisibility(View.VISIBLE);
        });

        selectionScreen.setOnClickListener(v -> {
        });
    }

    public void login() {
        if (loginStatus.getBoolean("LoginFlg", false)) {
            loginMenuButton.setText("ログアウト");
            loginMenuButton.setBackgroundColor(getResources().getColor(R.color.red));
            nomalModeButton.setEnabled(true);
        } else {
            loginMenuButton.setText("ログイン");
            loginMenuButton.setBackgroundColor(getResources().getColor(R.color.green));
            login_editor.putString("UserId", "").apply();
            login_editor.putString("UserName", "ゲスト").apply();
            login_editor.putString("Password", "").apply();
            login_editor.putBoolean("LoginFlg", false).apply();
            nomalModeButton.setEnabled(false);
        }
        userid.setText("ID:" + loginStatus.getString("UserId", ""));
        username.setText(loginStatus.getString("UserName", "ゲスト"));
    }

    public void logout() {
        loginMenuButton.setText("ログイン");
        loginMenuButton.setBackgroundColor(getResources().getColor(R.color.green));
        login_editor.putString("UserId", "").apply();
        login_editor.putString("UserName", "ゲスト").apply();
        login_editor.putString("Password", "").apply();
        login_editor.putBoolean("LoginFlg", false).apply();

        userid.setText(loginStatus.getString("UserId", ""));
        username.setText(loginStatus.getString("UserName", "ゲスト"));
        nomalModeButton.setEnabled(false);
        LoginFragment.loginButton.setEnabled(true);
        LoginFragment.passwordInput.setEnabled(true);
        LoginFragment.useridInput.setEnabled(true);
        LoginFragment.passwordInput.setText("");
        LoginFragment.useridInput.setText("");
        LoginFragment.errorText.setText("");
    }

    public void config(View v) {
        Intent intent = null;
        switch (getResources().getResourceEntryName(v.getId())) {
            case "nomalModeButton":
                intent = new Intent(getApplicationContext(), ConfigActivity.class);
                startActivity(intent);
                break;
            case "freeModeButton":
                intent = new Intent(getApplicationContext(), ConfigActivity2.class);
                startActivity(intent);
                break;
        }
        selectionOpenFlg = false;
        selectionScreen.setVisibility(View.GONE);
    }

    public void web(View v) {
        Intent intent = new Intent(getApplicationContext(), WebActivity.class);
        startActivity(intent);
    }

    public void help(View v) {
        switch (getResources().getResourceEntryName(v.getId())) {
            case "help":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("モード")
                        .setMessage("モードの選択をします。\n" +
                                "記録あり：\n　・座禅の記録を行います。\n" +
                                "　・Web版にて記録を見ることができます。\n" +
                                "　・ジャイロ計測による呼吸の練習もできます。\n" +
                                "　・このモードを選ぶにはログイン必須です。\n" +
                                "記録なし：\n　・記録は残りません。\n" +
                                "　・マインドフルネス瞑想など、瞑想等のみに集中したい方向けです。\n" +
                                "　・時間も体勢も自分の好みに合わせて行うことができます。\n" +
                                "　・このモードはログインは必須ではありません。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
        }
    }

    public void onBackPressed() {
        if (loginOpenFlg) {
            loginOpenFlg = false;
            loginScreen.setVisibility(View.GONE);
            if (loginStatus.getBoolean("LoginFlg", false)) {
                login();
            }
        } else if (selectionOpenFlg) {
            selectionOpenFlg = false;
            selectionScreen.setVisibility(View.GONE);
        } else {
            new AlertDialog.Builder(this)
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