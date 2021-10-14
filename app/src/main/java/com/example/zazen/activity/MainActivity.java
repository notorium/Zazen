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

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //View変数
    private TextView timerText, countdownText;
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
    private SimpleDateFormat dataFormat =
            new SimpleDateFormat("mm:ss.SS", Locale.JAPAN);

    //記録データ
    static StringBuilder accelerationData;
    static StringBuilder rotationData;
    Date startTime;
    float x1 = 0, y1 = 0;
    List<Float> avglist = new ArrayList<>();
    List<Long> timelist = new ArrayList<>();
    List<Float> rotx = new ArrayList<>();
    List<Float> roty = new ArrayList<>();
    int datacnt = 0;

    //タイマー変数
    private CountDown countDown;
    private CountUp countUp;
    private long[] countNumberList = {180000, 300000, 600000, 1200000, 1800000, 3600000};
    private long countNumber = countNumberList[ConfigActivity.config_value.getInt("SeekValue", 0)];
    private long firstTime = countNumber;
    private long oldSec = 0;
    private Boolean countUpDownFlag = ConfigActivity.config_value.getInt("SeekValue", 0) != 5;

    //センサー変数
    private SensorManager sensorManager;
    private float x = 0, y = 0, z = 0;
    private StringBuilder data;

    //THRESHOLD ある値以上を検出するための閾値
    protected final static double THRESHOLD = 1.0;
    protected final static double THRESHOLD_MIN = 1;

    //low pass filter alpha ローパスフィルタのアルファ値
    protected final static float alpha = (float) 0.8;

    //端末が実際に取得した加速度値。重力加速度も含まれる
    private float[] currentOrientationValues = {0.0f, 0.0f, 0.0f};
    //ローパス、ハイパスフィルタ後の加速度値
    private float[] currentAccelerationValues = {0.0f, 0.0f, 0.0f};

    //diff 差分
    private float dx = 0.0f;
    private float dy = 0.0f;
    private float dz = 0.0f;

    //previous data 1つ前の値
    private float old_x = 0.0f;
    private float old_y = 0.0f;
    private float old_z = 0.0f;

    //ベクトル量
    private double vectorSize = 0;

    //カウンタ
    long counter = 0;

    //一回目のゆれを省くカウントフラグ
    boolean counted = false;

    // X軸加速方向
    boolean vecx = true;
    // Y軸加速方向
    boolean vecy = true;
    // Z軸加速方向
    boolean vecz = true;

    //ノイズ対策
    boolean noiseflg = true;
    //ベクトル量(最大値)
    private double vectorSize_max = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        timerText = findViewById(R.id.timerText);
        countdownText = findViewById(R.id.countdownText);

        resultButton = findViewById(R.id.resultButton);

        startScreen = findViewById(R.id.startScreen);
        poseScreen = findViewById(R.id.poseScreen);
        tapScreen = findViewById(R.id.tapScreen);

        mode1Dialog = this.getLayoutInflater().inflate(R.layout.mode1, null);

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


        accelerationData = new StringBuilder("");
        rotationData = new StringBuilder("");

        //countNumber = countNumberList[ConfigActivity.config_value.getInt("SeekValue", 0)];
        timerText.setText(dataFormat.format(countNumber));
        countdownText.setText("");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        System.out.println(ConfigActivity.config_value.getInt("ModeNumber", 2));
        if (ConfigActivity.config_value.getInt("ModeNumber", 2) == 0) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("練習モード")
                    .setView(mode1Dialog)
                    .setPositiveButton("閉じる", null)
                    .show();
        }

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
            if (!ConfigActivity.config_value.getBoolean("GyroChecked", false)) onStop();
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

        startTime = new Date();
        final Handler countDownHandler = new Handler();
        countDownHandler.postDelayed(() -> countdownText.setText("3"), 1000);
        countDownHandler.postDelayed(() -> countdownText.setText("2"), 2000);
        countDownHandler.postDelayed(() -> countdownText.setText("1"), 3000);
        countDownHandler.postDelayed(() -> {
            timerText.setEnabled(true);
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
                if (countUpDownFlag || countNumber < 6000) {
                    new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage("座禅を中断しますか？\n※記録は残りません")
                            .setPositiveButton("はい", (dialog, which) -> {
                                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
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

                } else {
                    new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage("座禅を終了しますか？")
                            .setPositiveButton("はい", (dialog, which) -> {
                                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                                SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN);
                                long second = (countNumber / 1000) % 60;
                                long minute = (countNumber / (1000 * 60)) % 60;
                                long hour = (countNumber / (1000 * 60 * 60)) % 24;
                                intent.putExtra("SetTime", String.format("%02d:%02d:%02d:%d", hour, minute, second, countNumber % 1000));
                                intent.putExtra("StartTime", DF.format(startTime));
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
                break;

            default:
                break;
        }
    }

    //結果画面へ遷移
    public void result(View v) {
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN);
        long second = (firstTime / 1000) % 60;
        long minute = (firstTime / (1000 * 60)) % 60;
        long hour = (firstTime / (1000 * 60 * 60)) % 24;
        intent.putExtra("SetTime", String.format("%02d:%02d:%02d:%d", hour, minute, second, firstTime % 1000));
        intent.putExtra("StartTime", DF.format(startTime));
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

    //ジャイロストップ
    @Override
    public void onStop() {
        super.onStop();
        // Listenerの登録解除
        sensorManager.unregisterListener(this);
    }

    //ジャイロ開始
    @Override
    public void onResume() {
        super.onResume();
        // Listenerの登録
        List<android.hardware.Sensor> sensors = sensorManager.getSensorList(android.hardware.Sensor.TYPE_ROTATION_VECTOR);
        List<android.hardware.Sensor> sensors2 = sensorManager.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            android.hardware.Sensor s = sensors.get(0);
            android.hardware.Sensor s2 = sensors2.get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, s2, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
// TODO Auto-generated method stub
    }

    //センサイベント発生
    public void onSensorChanged(SensorEvent event) {
// TODO Auto-generated method stub
        float[] vector;
        float[] gyro;

        /*case Sensor.TYPE_ROTATION_VECTOR:
                vector = event.values.clone();
                int vx = (int) Math.abs(Math.floor(vector[0] / 7 * 500));
                int vy = (int) Math.abs(Math.floor(vector[1] / 7 * 500));
                int vz = (int) Math.abs(Math.floor(vector[2] * 100));
                sum += Math.abs(vx - x) > 0 ? 1 : 0;
                sum += Math.abs(vy - y) > 0 ? 1 : 0;
                //sum += Math.abs(vz - z) > 0 ? 1 : 0;
                x = vx;
                y = vy;
                z = vz;
                String str = "ジャイロセンサー値:"
                        + "\nX軸中心:" + String.format("%3d", vx)
                        + "\nY軸中心:" + String.format("%3d", vy)
                        + "\nZ軸中心:" + String.format("%3d", vz)
                        + "\n累計:" + String.format("%d", sum);
                values.setText(str);*/
        if (event.sensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER) {
            gyro = event.values.clone();
            // 取得 Acquiring data

            // ローパスフィルタで重力値を抽出
            currentOrientationValues[0] = event.values[0] * 0.2f + currentOrientationValues[0] * (1.0f - 0.2f);
            currentOrientationValues[1] = event.values[1] * 0.2f + currentOrientationValues[1] * (1.0f - 0.2f);
            currentOrientationValues[2] = event.values[2] * 0.2f + currentOrientationValues[2] * (1.0f - 0.2f);

            // 重力の値を省く
            currentAccelerationValues[0] = event.values[0] - currentOrientationValues[0];
            currentAccelerationValues[1] = event.values[1] - currentOrientationValues[1];
            currentAccelerationValues[2] = event.values[2] - currentOrientationValues[2];

            // ベクトル値を求めるために差分を計算
            dx = currentAccelerationValues[0] - old_x;
            dy = currentAccelerationValues[1] - old_y;
            dz = currentAccelerationValues[2] - old_z;
//            dx = event.values[0] - old_x;
//            dy = event.values[1] - old_y;
//            dz = event.values[2] - old_z;
            vectorSize = Math.sqrt(dx * dx + dy * dy + dz * dz);


            // 一回目はノイズになるから省く
            if (noiseflg) {
                noiseflg = false;
            } else {
                float fvector = (float) vectorSize;
//                int fdx = (int) dx;
//                int fdy = (int) dy;
//                int fdz = (int) dz;

                SimpleDateFormat DF = new SimpleDateFormat("HH:mm:ss.SSS", Locale.JAPAN);
                String date = DF.format(new Date());
                if (activityStart && !activityFinish) {
                    accelerationData.append(date + "," + dx + "," + dy + "," + dz + "\\n");
                }

                //datalist.add(date + "," + fdx + "," + fdy + "," + fdz + "," + fvector + "\n");
                //datalist.add(date + "," + dx + "," + dy + "," + dz + "," + vectorSize + "\n");
                if (true/*fvector > 100 && dz <0.0f */) {
                    if (counted) {

                        /*String str = "ジャイロセンサー値:"
                                + "\nX軸中心:" + String.format("%d", fdx)
                                + "\nY軸中心:" + String.format("%d", fdy)
                                + "\nZ軸中心:" + String.format("%d", fdz)
                                + "\n累計:" + String.format("%d", fvector);
                        values.setText(str);*/

                        counted = false;
                        // System.out.println("count is "+counter);
                        // 最大値なら格納
                        if (vectorSize > vectorSize_max) {
                            vectorSize_max = vectorSize;
                        }
                    } else {
                        counted = true;
                    }

                }
            }
                /*System.out.println("x:" + Math.abs(Math.floor(gyro[0] * 10000)));
                System.out.println("y:" + Math.abs(Math.floor(gyro[1] * 10000)));
                System.out.println("z:" + Math.abs(Math.floor(gyro[2] * 10000)));*/
        } else if (event.sensor.getType() == android.hardware.Sensor.TYPE_ROTATION_VECTOR) {
            vector = event.values.clone();
            float vx = vector[0];
            float vy = vector[1];
            float vz = vector[2];
//            System.out.println(vx + "," + vy + "," + vz);
            if (activityStart && !activityFinish) {
                SimpleDateFormat DF = new SimpleDateFormat("HH:mm:ss.SSS", Locale.JAPAN);
                String date = DF.format(new Date());
                rotationData.append(date + "," + vx + "," + vy + "," + vz + "\\n");
                counter++;
                rotx.add(vx);
                roty.add(vy);
                x1 += vx;
                y1 += vy;
                float avgx = x1 / 50;
                float avgy = y1 / 50;
                if (counter >= 50) {
                    avglist.add((float) Math.sqrt(avgx * avgx + avgy * avgy));
                    timelist.add(countUpDownFlag ? firstTime - countNumber : countNumber);
                    x1 -= rotx.get(0);
                    y1 -= roty.get(0);
                    rotx.remove(0);
                    roty.remove(0);
                }
            }
//            x = vx;
//            y = vy;
//            z = vz;
//            String str = "ジャイロセンサー値:"
//                    + "\nX軸中心:" + String.format("%f", vector[0] * 180 * Math.PI)
//                    + "\nY軸中心:" + String.format("%f", vector[1] * 180 * Math.PI)
//                    + "\nZ軸中心:" + String.format("%f", vector[2] * 180 * Math.PI);
        }


        //if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {


        //} else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            /*str += "\nジャイロセンサー値:"
                    + "\nX軸中心:" + String.format("%3d", event.values[0])
                    + "\nY軸中心:" + String.format("%3d", event.values[1])
                    + "\nZ軸中心:" + String.format("%3d", event.values[2])
                    + "\n累計:" + String.format("%d", sum);*/


    }

    public int average() {
        int kokyu = 0, upcnt = 0, downcnt = 0;
        float min = 10, max = -10;
        boolean f = false;
        for (float i : avglist) {
            if (min > i) {
                min = i;
                downcnt++;
            } else if (max < i) {
                downcnt = 0;
                max = i;
                min = 10;
                upcnt++;
                f = upcnt > 20;
            }
            if (downcnt > 50 && f) {
                kokyu++;
                max = -10;
                upcnt = 0;
            }
        }
        return kokyu == 0 ? 0 : kokyu + 1;
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
            timerText.setText(dataFormat.format(0));
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
            long minute = (countNumber / (1000 * 60)) % 60;
            if (second != oldSec && ConfigActivity.config_value.getInt("ModeNumber", 2) == 0) {
                oldSec = second;
                if (oldSec % 10 == 0) {
                    soundPool.play(se2, 1f, 1f, 0, 0, 1f);
                }
                soundPool.play(se1, 1f, 1f, 0, 0, 1f);
            }
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
            timerText.setText(dataFormat.format(firstTime));
            countdownText.setText("終了!!");
            resultButton.setVisibility(View.VISIBLE);
        }

        // インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {
            countNumber = firstTime - millisUntilFinished;
            if (countNumber == firstTime) {
                onFinish();
            }
            long milli = countNumber % 1000 / 10;
            long second = (countNumber / 1000) % 60;
            long minute = (countNumber / (1000 * 60)) % 60;
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


