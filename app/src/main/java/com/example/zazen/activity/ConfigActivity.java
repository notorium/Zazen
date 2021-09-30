package com.example.zazen.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
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
        setGyro = findViewById(R.id.gyroSwitch);
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
        editor.putInt("ModeNumber", 2);

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
            modeNum = 0;
            editor.putInt("ModeNumber", modeNum);
            mode1.setBackgroundColor(getResources().getColor(R.color.red));
            mode2.setBackgroundColor(getResources().getColor(R.color.purple_500));
            mode3.setBackgroundColor(getResources().getColor(R.color.purple_500));

            setTime.setProgress(0);
            setGyro.setChecked(true);
            setDevice.check(R.id.radioButton1);
            Dset2.setEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setTime.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setGyro.setThumbTintList(ColorStateList.valueOf(getColor(R.color.red)));
                setGyro.setTrackTintList(ColorStateList.valueOf(getColor(R.color.red)));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Dset1.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            }
            editor.putInt("SeekValue", setTime.getProgress()).apply();
            editor.putBoolean("GyroChecked", true).apply();
        });

        mode2.setOnClickListener(v -> {
            modeNum = 1;
            editor.putInt("ModeNumber", modeNum);
            mode1.setBackgroundColor(getResources().getColor(R.color.purple_500));
            mode2.setBackgroundColor(getResources().getColor(R.color.red));
            mode3.setBackgroundColor(getResources().getColor(R.color.purple_500));
        });

        mode3.setOnClickListener(v -> {
            modeNum = 2;
            editor.putInt("ModeNumber", modeNum);
            mode1.setBackgroundColor(getResources().getColor(R.color.purple_500));
            mode2.setBackgroundColor(getResources().getColor(R.color.purple_500));
            mode3.setBackgroundColor(getResources().getColor(R.color.red));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setTime.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_200)));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setGyro.setThumbTintList(ColorStateList.valueOf(getColor(R.color.teal_200)));
                setGyro.setTrackTintList(ColorStateList.valueOf(getColor(R.color.teal_200)));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Dset1.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_200)));
            }
        });
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
    }
}
