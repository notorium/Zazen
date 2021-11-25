package com.example.zazen.activity;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.zazen.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    //View変数
    private TextView timerText, insteadtimerText, countdownText, attentionText;
    private Button resultButton;
    private View startScreen, poseScreen, tapScreen, mode1Dialog;

    //座禅開始判定
    private boolean activityStart = false;

    //座禅終了判定
    private boolean activityFinish = false;

    //ポーズ判定
    private boolean activityPose = true;

    //音声データ
    private SoundPool soundPool;
    private int se1, se2, se3;

    //データフォーマット
//    private SimpleDateFormat dataFormat =
//            new SimpleDateFormat("mm:ss.SS", Locale.JAPAN);

    //タイマー変数
    private CountDown countDown;
    private CountUp countUp;
    private long[] countNumberList = {180000, 300000, 600000, 1200000, 1800000, 59940000};
    private long countNumber = 0;
    private long firstTime = 0;
    private long oldSec = 0;
    private Boolean countUpDownFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        timerText = findViewById(R.id.timerText);
        countdownText = findViewById(R.id.countdownText);
        attentionText = findViewById(R.id.attentionText);
        insteadtimerText = findViewById(R.id.insteadtimerText);
        resultButton = findViewById(R.id.resultButton);

        startScreen = findViewById(R.id.startScreen);
        poseScreen = findViewById(R.id.poseScreen);
        tapScreen = findViewById(R.id.tapScreen);

        attentionText.setText("記録、計測は行われません。\n楽な体勢で行ってください。");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        } else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(5)
                    .build();
        }

        se1 = soundPool.load(this, R.raw.se1, 1);
        se2 = soundPool.load(this, R.raw.se2, 1);
        se3 = soundPool.load(this, R.raw.se3, 1);

        if (!ConfigActivity2.config_value2.getBoolean("TimeChecked", true)) {
            timerText.setVisibility(View.GONE);
            insteadtimerText.setVisibility(View.VISIBLE);
        }

        if (ConfigActivity2.config_value2.getInt("SelectNum", 1) == 1) {
            countUpDownFlag = ConfigActivity2.config_value2.getInt("SeekValue", 0) != 5;
            countNumber = countNumberList[ConfigActivity2.config_value2.getInt("SeekValue", 0)];
        } else {
            countUpDownFlag = true;
            countNumber = ConfigActivity2.config_value2.getInt("TextValue", 10) * 60000;
        }
        firstTime = countNumber;

        if (countUpDownFlag) {
            long milli = countNumber % 1000 / 10;
            long second = (countNumber / 1000) % 60;
            long minute = (countNumber / (1000 * 60));
            timerText.setText(String.format("%02d:%02d.%02d", minute, second, milli));
        }


        countdownText.setText("");

        if (countUpDownFlag) {
            countDown = new CountDown(countNumber, 10);
        } else {
            countUp = new CountUp(countNumber, 10, firstTime);
        }
    }

    //画面タップ後に座禅スタート
    public void screenTap(View v) {
        if (!activityStart && !activityFinish) {
            //3秒のカウントダウン
            countDown();
        } else if (activityStart && !activityFinish) {
            poseScreen.setVisibility(View.VISIBLE);
            tapScreen.setEnabled(false);

//            if (countUpDownFlag) {
//                countDown.cancel();
//            } else {
//                countTimer.cancel();
//            }

//            activityStart = false;
            activityPose = true;
        }
    }


    //タップから3秒後にスタート
    public void countDown() {
        startScreen.setVisibility(View.GONE);
        tapScreen.setEnabled(false);
        activityPose = false;

        final Handler countDownHandler = new Handler();
        countDownHandler.postDelayed(() -> countdownText.setText("3"), 1000);
        countDownHandler.postDelayed(() -> countdownText.setText("2"), 2000);
        countDownHandler.postDelayed(() -> countdownText.setText("1"), 3000);
        countDownHandler.postDelayed(() -> {
            timerText.setEnabled(true);
            insteadtimerText.setEnabled(true);
            countdownText.setText("Start!!");
        }, 4000);

        countDownHandler.postDelayed(() -> {
            countdownText.setText("");
            if (!activityFinish) {
                if (countUpDownFlag) {
                    countDown.start();
                } else {
                    countUp.start();
                }
                tapScreen.setEnabled(true);
            }
            activityStart = true;
        }, 5000);
    }

    //ポーズ画面
    public void pose(View v) {
        switch (getResources().getResourceEntryName(v.getId())) {
            case "resume":
                //再開
                poseScreen.setVisibility(View.GONE);
                tapScreen.setEnabled(true);
                break;

            case "restart":
                //座禅やり直し
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("同じ設定のままやり直しますか？")
                        .setPositiveButton("はい", (dialog, which) -> {
                            onStop();
                            if (countUpDownFlag) {
                                countDown.cancel();
                            } else {
                                countUp.cancel();
                            }
                            Intent intent = getIntent();
                            overridePendingTransition(0, 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();

                            overridePendingTransition(0, 0);
                            startActivity(intent);
                        })
                        .setNegativeButton("いいえ", null)
                        .show();
                break;

            case "finish":
                //座禅中断
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("座禅を終了しますか？")
                        .setPositiveButton("はい", (dialog, which) -> {
                            Intent intent = new Intent(getApplicationContext(), ResultActivity2.class);
                            SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN);
                            long second = ((countUpDownFlag ?firstTime - countNumber : countNumber) / 1000) % 60;
                            long minute = ((countUpDownFlag ?firstTime - countNumber : countNumber) / (1000 * 60)) % 60;
                            long hour = ((countUpDownFlag ?firstTime - countNumber : countNumber) / (1000 * 60 * 60)) % 24;
                            intent.putExtra("SetTime", String.format("%02d:%02d:%02d.%d", hour, minute, second, countNumber % 1000));
//                                intent.putExtra("StartTime", DF.format(startTime));
                            intent.putExtra("Minute", (second == 0 ? "" : "約") + (hour * 60 + minute));
                            startActivity(intent);
                            if (countUpDownFlag) {
                                countDown.cancel();
                            } else {
                                countUp.cancel();
                            }
                            onStop();
                            activityFinish = true;
                            this.finish();
                        })
                        .setNegativeButton("いいえ", null)
                        .show();
        }
    }

    //結果画面へ遷移
    public void result(View v) {
        Intent intent = new Intent(getApplicationContext(), ResultActivity2.class);
        SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN);
        long second = (firstTime / 1000) % 60;
        long minute = (firstTime / (1000 * 60)) % 60;
        long hour = (firstTime / (1000 * 60 * 60)) % 24;
        intent.putExtra("SetTime", String.format("%02d:%02d:%02d.%d", hour, minute, second, firstTime % 1000));
//        intent.putExtra("StartTime", DF.format(startTime));
        intent.putExtra("Minute", Long.toString(hour * 60 + minute));
        soundPool.stop(se3);
        startActivity(intent);
        this.finish();
    }

    //ホームボタン、タスクボタンタップ検知
    public void onUserLeaveHint() {
        //座禅強制終了
        if (!activityFinish) {
            if (countUpDownFlag) {
                countDown.cancel();
            } else {
                countUp.cancel();
            }
            activityStart = false;
            activityFinish = true;
            onStop();
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("座禅が中断されました")
                    .setPositiveButton("閉じる", (dialog, which) -> {
                        this.finish();
                    })
                    .show();
        }
    }

    //戻るボタン処理
    public void onBackPressed() {
        if (activityStart && !activityFinish) {
            //画面タップ有効時、ポーズ画面へ遷移
            poseScreen.setVisibility(View.VISIBLE);
            tapScreen.setEnabled(false);
        }
    }

    //カウントダウンタイマー
    class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            //タイマー終了
            activityStart = false;
            activityFinish = true;
            soundPool.play(se3, 1f, 1f, 0, 3, 1f);
            startScreen.setVisibility(View.GONE);
            tapScreen.setVisibility(View.GONE);
            poseScreen.setVisibility(View.GONE);
            timerText.setText(String.format("%02d:%02d.%02d", 0, 0, 0));
            countdownText.setText("終了!!");
            resultButton.setVisibility(View.VISIBLE);
        }

        // インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {
            // 残り時間を分、秒、ミリ秒に分割
            countNumber = millisUntilFinished;
            long milli = countNumber % 1000 / 10;
            long second = (countNumber / 1000) % 60;
            long minute = (countNumber / (1000 * 60));
            timerText.setText(String.format("%02d:%02d.%02d", minute, second, milli));
        }
    }

    //カウントアップタイマー
    class CountUp extends CountDownTimer {
        private final long firstTime;

        public CountUp(long millisInFuture, long countDownInterval, long firstTime) {
            super(millisInFuture, countDownInterval);
            this.firstTime = firstTime;
        }

        @Override
        public void onFinish() {
            //タイマー終了
            activityStart = false;
            activityFinish = true;
            soundPool.play(se3, 1f, 1f, 0, 3, 1f);
            startScreen.setVisibility(View.GONE);
            tapScreen.setVisibility(View.GONE);
            poseScreen.setVisibility(View.GONE);
            long milli = firstTime % 1000 / 10;
            long second = (firstTime / 1000) % 60;
            long minute = (firstTime / (1000 * 60));
            timerText.setText(String.format("%02d:%02d.%02d", minute, second, milli));
            countdownText.setText("終了!!");
            resultButton.setVisibility(View.VISIBLE);
        }

        // インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {
            countNumber = firstTime - millisUntilFinished;
            if (countNumber >= firstTime) {
                onFinish();
            }
            long milli = countNumber % 1000 / 10;
            long second = (countNumber / 1000) % 60;
            long minute = (countNumber / (1000 * 60));
//            if (second != oldSec) {
//                oldSec = second;
//                if (oldSec % 10 == 0) {
//                    soundPool.play(se2, 1f, 1f, 0, 0, 1f);
//                }
//                soundPool.play(se1, 1f, 1f, 0, 0, 1f);
//            }
            timerText.setText(String.format("%02d:%02d.%02d", minute, second, milli));
        }
    }
}


