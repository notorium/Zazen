package com.example.zazen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //View変数
    private TextView timerText, countdownText;
    private View startScreen, poseScreen, tapScreen;

    //初回スタート判定
    private boolean activityStart = false;

    private SimpleDateFormat dataFormat =
            new SimpleDateFormat("mm:ss.SS", Locale.JAPAN);

    private long countNumber = 10000;
    private CountDown countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.timerText);
        countdownText = findViewById(R.id.countdownText);

        startScreen = findViewById(R.id.startScreen);
        poseScreen = findViewById(R.id.poseScreen);
        tapScreen = findViewById(R.id.tapScreen);

        timerText.setText(dataFormat.format(countNumber));
        countdownText.setText("");
    }

    //画面タップ後に座禅スタート
    public void screenTap(View v) {
        if (!activityStart) {
            //3秒のカウントダウン
            countDown();
        } else {
            poseScreen.setVisibility(View.VISIBLE);
            tapScreen.setEnabled(false);
            countDown.cancel();
            activityStart = false;
        }
    }

    //タップから3秒後にスタート
    public void countDown() {
        countDown = new CountDown(countNumber, 10);
        startScreen.setVisibility(View.GONE);
        tapScreen.setEnabled(false);
        final Handler handler = new Handler();
        handler.postDelayed(() -> countdownText.setText("3"), 1000);
        handler.postDelayed(() -> countdownText.setText("2"), 2000);
        handler.postDelayed(() -> countdownText.setText("1"), 3000);
        handler.postDelayed(() -> {
            timerText.setEnabled(true);
            countdownText.setText("Start!!");
        }, 4000);
        handler.postDelayed(() -> {
            countdownText.setText("");
            countDown.start();
            tapScreen.setEnabled(true);
            activityStart = true;
        }, 5000);
    }

    public void pose(View v) {
        //ポーズ
        switch (getResources().getResourceEntryName(v.getId())) {
            case "resume":
                //再開
                poseScreen.setVisibility(View.GONE);
                countDown();
                break;
            case "restart":
                //リスタート
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("同じ設定のままやり直しますか？")
                        .setPositiveButton("はい", (dialog, which) -> {
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
                //中断
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("座禅を中断しますか？")
                        .setPositiveButton("はい", (dialog, which) -> {
                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            startActivity(intent);
                            this.finish();
                        })
                        .setNegativeButton("いいえ", null)
                        .show();
                break;
            default:
                break;
        }
    }

    public void onUserLeaveHint() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("座禅が中断されました")
                .setPositiveButton("閉じる", (dialog, which) ->{
                    countDown.cancel();
                    activityStart = false;
                    this.finish();
                })
                .show();
    }

    public void onBackPressed() {
        if (activityStart) {
            poseScreen.setVisibility(View.VISIBLE);
            tapScreen.setEnabled(false);
            countDown.cancel();
            activityStart = false;
        }
    }

    class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

//        public void run() {
//            // 10 msec order
//            int period = 10;
//
//            /*while (!stopRun) {
//                // sleep: period msec
//                try {
//                    Thread.sleep(period);
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                    stopRun = true;
//                }*/
//            final Handler handler = new Handler();
//            handler.post((Runnable) () -> {
//                long endTime = System.currentTimeMillis();
//                // カウント時間 = 経過時間 - 開始時間
//                long diffTime = (endTime - startTime);
//                timerText.setText(dataFormat.format(diffTime));
//
//            });
//            //}
//        }

        @Override
        public void onFinish() {
            //タイマー終了
            activityStart = false;
            startScreen.setVisibility(View.GONE);
            tapScreen.setEnabled(false);
            timerText.setText(dataFormat.format(0));
            countdownText.setText("終了!!");
        }

        // インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {
            // 残り時間を分、秒、ミリ秒に分割
            //long mm = millisUntilFinished / 1000 / 60;
            //long ss = millisUntilFinished / 1000 % 60;
            //long ms = millisUntilFinished - ss * 1000 - mm * 1000 * 60;
            //timerText.setText(String.format("%1$02d:%2$02d.%3$03d", mm, ss, ms));
            countNumber = millisUntilFinished;
            timerText.setText(dataFormat.format(millisUntilFinished));
        }
    }


}


