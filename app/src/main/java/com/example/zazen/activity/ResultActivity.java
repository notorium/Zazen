package com.example.zazen.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.example.zazen.R;
import com.example.zazen.async.HttpRequest_POST_Data;

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

    private int selfAssessment;
    private boolean loginOpenFlg = false;
    private String[] modeStr = {"練習", "瞑想", "フリーカスタム"};

    public static boolean postFlg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        assessment_seekBar = findViewById(R.id.time_seekBar);
        commentText = findViewById(R.id.editText);
        loginScreen = findViewById(R.id.loginScreen);
        resultButton = findViewById(R.id.postResultButton);

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
//        TextView view = findViewById(R.id.textView7);
//        view.setText(timeData);
    }

    public void postResult(View v) {
        if (StartActivity.loginStatus.getBoolean("LoginFlg", false)) {
            v.setEnabled(false);
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("送信前確認")
                    .setMessage("座禅の記録を送信しますか？")
                    .setPositiveButton("はい", (dialog, which) -> {
                        SimpleDateFormat DF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN);
                        String date = DF.format(new Date());
                        String postStr = "{\"user_id\":\"" + StartActivity.loginStatus.getString("UserId", "") +
                                "\",\"date\":\"" + date +
                                "\",\"mode\":\"" + modeStr[ConfigActivity.config_value.getInt("ModeNumber", 2)] +
                                "\",\"time\":\"" + getIntent().getStringExtra("SetTime") +
                                "\",\"comment\":\"" + commentText.getText().toString() +
                                "\",\"selfassessment\":\"" + selfAssessment +
                                "\",\"flg\":\"" + (gyroFlg ? "1" : "0") +
                                "\",\"accelerationdata\":\"" + accelerationData +
                                "\",\"rotationdata\":\"" + rotationData +
                                "\",\"weather_id\":\"" + "1" +
                                "\"}";
                        HttpRequest_POST_Data httpRequestPost = new HttpRequest_POST_Data(this, postStr);
                        httpRequestPost.execute("http://fukuiohr2.sakura.ne.jp/2021/Zazen/postdata.php");
//        HttpRequest_GET_Img httpRequestGetImg = new HttpRequest_GET_Img(this, "test_");
//        httpRequestGetImg.execute("http://fukuiohr2.sakura.ne.jp/2021/Zazen/postdata.php");
                    })
                    .setNegativeButton("いいえ", null)
                    .show();
        } else {
            loginScreen.setVisibility(View.VISIBLE);
            loginOpenFlg = true;
        }
    }

    public void shareResult(View v) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(shareIntent);
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