package com.example.zazen.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zazen.R;

public class ConfigActivity extends AppCompatActivity {
    private SeekBar setTime;
    private Switch setGyro;
    private RadioGroup setDevice;
    private RadioButton Dset1, Dset2;
    private Button mode1, mode2, mode3;
    private View modeGroup;

    private int modeNum = 2;
    static SharedPreferences config_value;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //各ツールの変数設定
        setTime = findViewById(R.id.time_seekBar);
        setGyro = findViewById(R.id.timerSwitch);
        setDevice = findViewById(R.id.device_radioGroup);
        Dset1 = findViewById(R.id.radioButton1);
        Dset2 = findViewById(R.id.radioButton2);
        mode1 = findViewById(R.id.modeButton1);
        mode2 = findViewById(R.id.modeButton2);
        mode3 = findViewById(R.id.modeButton3);
        modeGroup = findViewById(R.id.modeGroup);

        //設定値の初期値設定
        config_value = getSharedPreferences("Config", MODE_PRIVATE);
        editor = config_value.edit();

        setTime.setProgress(config_value.getInt("SeekValue", 0));
        setGyro.setChecked(config_value.getBoolean("GyroChecked", false));
        setDevice.check(config_value.getInt("Device", R.id.radioButton1));
        if (config_value.getBoolean("GyroChecked", false)) {
            Dset2.setEnabled(false);
        }
        mode3.setBackgroundColor(getResources().getColor(R.color.red));
        editor.putInt("ModeNumber", modeNum).apply();

        //時間設定(シークバー)
        setTime.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //ツマミのドラッグ時の処理
                        System.out.println(setTime.getProgress());
                        if (modeNum == 0) {
                            seekBar.setProgress(0);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //ツマミに触ったときの処理
                        System.out.println(setTime.getProgress());
                        if (modeNum == 0) {
                            seekBar.setProgress(0);
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //ツマミを離した時の処理
                        if (modeNum == 0) {
                            seekBar.setProgress(0);
                        } else {
                            editor.putInt("SeekValue", seekBar.getProgress()).apply();
                            System.out.println(setTime.getProgress());
                        }

                    }
                }
        );

        //ジャイロ計測設定(スイッチ)
        setGyro.setOnCheckedChangeListener((v, isChecked) -> {
            if (modeNum == 0) {
                v.setChecked(true);
            } else {
                if (setGyro.isChecked()) {
                    //スイッチON
                    editor.putBoolean("GyroChecked", true).apply();
                    setDevice.check(R.id.radioButton1);
                    Dset2.setEnabled(false);
                } else {
                    //スイッチOFF
                    editor.putBoolean("GyroChecked", false).apply();
                    Dset2.setEnabled(true);
                }
                editor.commit();
            }
        });

        //端末設置場所の設定(ラジオボタン)
        setDevice.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    //ラジオボタン選択時の処理
                    editor.putInt("Device", checkedId).apply();
                }
        );

        mode1.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("開始前確認")
                    .setCancelable(false)
                    .setMessage("呼吸の練習を開始しますか？\n" +
                            "※設定値は固定となります。" +
                            "\n時間：3分\nジャイロ：あり\n端末設置場所：手の上")
                    .setPositiveButton("はい", (dialog, which) -> {
                        modeNum = 0;
                        editor.putInt("ModeNumber", modeNum).apply();
                        mode1.setBackgroundColor(getResources().getColor(R.color.red));
                        mode2.setBackgroundColor(getResources().getColor(R.color.default_purple));
                        mode3.setBackgroundColor(getResources().getColor(R.color.default_purple));


                        setTime.setProgress(0);
                        setGyro.setChecked(true);
                        setDevice.check(R.id.radioButton1);

                        setTime.setEnabled(false);
                        setGyro.setEnabled(false);
                        Dset1.setEnabled(false);
                        Dset2.setEnabled(false);

                        editor.putInt("SeekValue", setTime.getProgress()).apply();
                        editor.putBoolean("GyroChecked", true).apply();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        this.finish();
                    })
                    .setNegativeButton("いいえ", null)
                    .show();
        });

        mode2.setOnClickListener(v -> {
            modeNum = 1;
            editor.putInt("ModeNumber", modeNum).apply();

            setGyro.setEnabled(false);
            Dset1.setEnabled(true);
            Dset2.setEnabled(true);
            setGyro.setChecked(false);
            editor.putBoolean("GyroChecked", false).apply();

            mode1.setBackgroundColor(getResources().getColor(R.color.default_purple));
            mode2.setBackgroundColor(getResources().getColor(R.color.red));
            mode3.setBackgroundColor(getResources().getColor(R.color.default_purple));
        });

        mode3.setOnClickListener(v -> {
            modeNum = 2;
            editor.putInt("ModeNumber", modeNum).apply();
            mode1.setBackgroundColor(getResources().getColor(R.color.default_purple));
            mode2.setBackgroundColor(getResources().getColor(R.color.default_purple));
            mode3.setBackgroundColor(getResources().getColor(R.color.red));

            setTime.setEnabled(true);
            setGyro.setEnabled(true);
            Dset1.setEnabled(true);
            setGyro.setChecked(true);
            editor.putBoolean("GyroChecked", true).apply();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                setTime.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_200)));
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                setGyro.setThumbTintList(ColorStateList.valueOf(getColor(R.color.teal_200)));
//                setGyro.setTrackTintList(ColorStateList.valueOf(getColor(R.color.teal_200)));
//            }
//            setGyro.getThumbDrawable().setColorFilter(getResources().getColor(R.color.teal_200), PorterDuff.Mode.MULTIPLY);
//            setGyro.getTrackDrawable().setColorFilter(getResources().getColor(R.color.teal_200), PorterDuff.Mode.MULTIPLY);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Dset1.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_200)));
//            }
        });
    }

    public void help(View v) {
        switch (getResources().getResourceEntryName(v.getId())) {
            case "help1":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("モード")
                        .setMessage("モードの選択をします。\n" +
                                "練習モード：\n　呼吸の練習をするモードです。\n" +
                                "　時間：3分　ジャイロ：あり\n" +
                                "　設定値は固定です。\n" +
                                "瞑想モード：\n　ジャイロ計測をしないモードです。\n" +
                                "　自由な体勢で瞑想ができます。\n" +
                                "　ジャイロ計測はOFF固定です。\n" +
                                "　時間と端末の設置場所を自由に設定できます。\n" +
                                "フリーモード：\n　設定値をカスタムできるモードです。\n" +
                                "　設定値を自由に設定できます。\n")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
            case "help2":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("時間")
                        .setMessage("座禅を行う時間を設定します。\n" +
                                "時間指定ありの場合：\n　・設定時間分の記録を行います。\n" +
                                "　・中断すると記録が残りません。\n" +
                                "時間指定なしの場合：\n　・記録できる時間の上限は60分です。\n" +
                                "　・開始から1分未満で中断すると記録が残りません。\n" +
                                "　・開始から1分以上から中断しても記録が残るようになります。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
            case "help3":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("ジャイロ")
                        .setMessage("ジャイロの計測を行うかを設定します。" +
                                "\n・計測をONにする場合は、端末の設置場所は手の上固定となります。"+
                                "\n・瞑想モードの場合はジャイロなし固定です。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
            case "help4":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("端末の設置場所")
                        .setMessage("座禅をする際の端末の設置場所を設定します。\n" +
                                "・ジャイロ計測ONの場合は手の上固定となります。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
        }
    }

    public void start(View v) {
        //開始前確認メッセージ
        new AlertDialog.Builder(this)
                .setTitle("開始前確認")
                .setCancelable(false)
                .setMessage("この設定で開始しますか？")
                .setPositiveButton("はい", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    this.finish();
                })
                .setNegativeButton("いいえ", null)
                .show();
        new AlertDialog.Builder(this)
                .setTitle("手順確認")
                .setCancelable(false)
                .setMessage("座禅の手順を確認しますか？")
                .setPositiveButton("はい", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), WebActivity2.class);
                    startActivity(intent);
                })
                .setNegativeButton("いいえ", null)
                .show();
    }
}
