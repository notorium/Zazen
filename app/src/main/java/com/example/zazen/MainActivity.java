package com.example.zazen;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //View変数
    private TextView timerText, countdownText;
    private View poseScreen, tapScreen;

    //初回スタート判定
    private boolean activityStart = false;

    private SimpleDateFormat dataFormat =
            new SimpleDateFormat("mm:ss.SS", Locale.JAPAN);

    private long countNumber = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.timerText);
        countdownText = findViewById(R.id.countdownText);
        poseScreen = findViewById(R.id.poseScreen);
        tapScreen = findViewById(R.id.tapScreen);
        timerText.setText(dataFormat.format(countNumber));
        countdownText.setText("");
    }

    //画面タップ後に座禅スタート
    public void screenTap(View v) {
        if (!activityStart) {
            View view = findViewById(R.id.startScreen);
            view.setVisibility(View.GONE);
            tapScreen.setEnabled(false);
            activityStart = true;

            //タップから3秒後にスタート
            CountDown countDown = new CountDown(countNumber, 10);
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
            }, 5000);
        } else {
            poseScreen.setVisibility(View.VISIBLE);
        }
    }

    public void pose(View v) {
        //ポーズ
        switch (getResources().getResourceEntryName(v.getId())) {
            case "resume":
                break;
            case "restart":
                break;
            case "finish":
                break;
            default:
                break;
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

            timerText.setText(dataFormat.format(millisUntilFinished));
        }
    }

}


