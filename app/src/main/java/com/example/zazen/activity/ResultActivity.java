package com.example.zazen.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.zazen.R;
import com.example.zazen.async.HttpRequest_POST_Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private String accelerationData, rotationData;
    boolean gyroFlg = ConfigActivity.config_value.getBoolean("GyroChecked", false);

    private EditText commentText;
    private SeekBar assessment_seekBar;
    private View loginScreen;
    private Button resultButton;
    private TextView breathCount;

    private int selfAssessment = 1;
    private boolean loginOpenFlg = false;
    private String[] modeStr = {"練習", "瞑想", "フリー"};
    private String[] shareModeStr = {"腹式呼吸の練習", "瞑想", "座禅"};
    public static boolean postFlg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        assessment_seekBar = findViewById(R.id.time_seekBar);
        commentText = findViewById(R.id.editText);
        loginScreen = findViewById(R.id.loginScreen);
        resultButton = findViewById(R.id.postResultButton);
        breathCount = findViewById(R.id.breathCount);

        accelerationData = gyroFlg ? MainActivity.accelerationData.toString() : "";
        rotationData = gyroFlg ? MainActivity.rotationData.toString() : "";

        resultButton.setText(StartActivity.loginStatus.getBoolean("LoginFlg", false) ? "結果を送信" : "ログイン");

        assessment_seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //ツマミのドラッグ時の処理
                        System.out.println(assessment_seekBar.getProgress());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //ツマミに触ったときの処理
                        System.out.println(assessment_seekBar.getProgress());
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //ツマミを離した時の処理
                        selfAssessment = assessment_seekBar.getProgress() + 1;
                        System.out.println(assessment_seekBar.getProgress());
                    }
                }
        );
        breathCount.setText(getIntent().getStringExtra("Breath") + "回");
    }

    public void postResult(View v) {
        if (StartActivity.loginStatus.getBoolean("LoginFlg", false)) {
            v.setEnabled(false);
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("送信前確認")
                    .setMessage("座禅の記録を送信しますか？")
                    .setPositiveButton("はい", (dialog, which) -> {
                        String postStr = "{\"user_id\":\"" + StartActivity.loginStatus.getString("UserId", "") +
                                "\",\"date\":\"" + getIntent().getStringExtra("StartTime") +
                                "\",\"mode\":\"" + modeStr[ConfigActivity.config_value.getInt("ModeNumber", 2)] +
                                "\",\"time\":\"" + getIntent().getStringExtra("SetTime") +
                                "\",\"comment\":\"" + commentText.getText().toString() +
                                "\",\"selfassessment\":\"" + selfAssessment +
                                "\",\"flg\":\"" + (gyroFlg ? "1" : "0") +
                                "\",\"rotationdata\":\"" + rotationData +
                                "\",\"weather_id\":\"" + "0" +
                                "\"}";
                        HttpRequest_POST_Data httpRequestPost = new HttpRequest_POST_Data(this, postStr);
                        httpRequestPost.execute("https://zazethcare.cloud/Application/postdata.php");
                    })
                    .setNegativeButton("いいえ", (dialog, which) -> {
                        v.setEnabled(true);
                    })
                    .show();
        } else {
            loginScreen.setVisibility(View.VISIBLE);
            loginOpenFlg = true;
        }
    }

    public void shareResult(View v) {
        String text = StartActivity.loginStatus.getString("UserName", "ゲスト") + "さんは\n" +
                getIntent().getStringExtra("StartTime") + "から" +
                getIntent().getStringExtra("Minute") + "分間" +
                shareModeStr[ConfigActivity.config_value.getInt("ModeNumber", 2)] +
                "をしました！" +
                "\n\nサイトの利用はこちらから→https://zazethcare.cloud/index.php";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, text);
        startActivity(shareIntent);
    }

//    public void tweet(View view) {
//        String strTweet = "";
//        String strHashTag = "#ZAZETHCARE";
//        String strMessage = "";
//        try {
//            strTweet = "http://twitter.com/intent/tweet?text="
//                    + URLEncoder.encode(strMessage, "UTF-8")
//                    + "+"
//                    + URLEncoder.encode(strHashTag, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strTweet));
//        startActivity(intent);
//    }

    public void help(View v) {
        switch (getResources().getResourceEntryName(v.getId())) {
            case "help1":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("感想")
                        .setMessage("・座禅の感想を入力することができます。\n" +
                                "・文字数の入力制限は140字以内となります。\n")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
            case "help2":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("自己評価")
                        .setMessage("・座禅の出来を5段階で自己評価します。\n" +
                                "・数字が大きいほど評価が高くなります。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
            case "help3":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("呼吸の回数")
                        .setMessage("・ジャイロ計測ありの場合に、簡易的に検知できた呼吸の回数を表示します。" +
                                "\n・表示される呼吸の回数は完全でない場合があります。"+
                                "\n・ノイズなどが含まれた場合、回数が大幅にずれることがあります。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
            case "help4":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("結果を共有")
                        .setMessage("・座禅の記録を任意のSNSにシェアすることができます。" +
                                "\n・共有される内容は、ユーザ名と記録時間です。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
            case "help5":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("データ送信")
                        .setMessage("・直前に行った座禅の記録をデータベースに送信します。\n" +
                                "・送信すると、Webページにて記録の詳細が確認できます。\n" +
                                "・記録を送信するにはログインが必須です。\n" +
                                "・ログインをしていない場合はここからログインすることができます。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
        }
    }

    public void onBackPressed() {
        backHome();
    }

    public void onHomeButton(View v) {
        backHome();
    }

    public void backHome() {
        if (loginOpenFlg) {
            loginScreen.setVisibility(View.GONE);
            loginOpenFlg = false;
            if (StartActivity.loginStatus.getBoolean("LoginFlg", false)) {
                resultButton.setText("結果を送信");
            }
        } else {
            if (postFlg) {
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("データ送信未完了")
                        .setMessage("記録を送信をせずにホーム画面へ戻りますか？\n※記録は破棄されます。")
                        .setPositiveButton("はい", (dialog, which) -> {
                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            startActivity(intent);
                            this.finish();
                        })
                        .setNegativeButton("いいえ", null)
                        .show();
            }
        }
    }

}