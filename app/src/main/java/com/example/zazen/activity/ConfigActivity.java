package com.example.zazen.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
    static SharedPreferences config_value;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //各ツールの変数設定
        setTime = findViewById(R.id.time_seekBar);
        setGyro = findViewById(R.id.gyro_Switch);
        setDevice = findViewById(R.id.device_radioGroup);
        Dset1 = findViewById(R.id.radioButton4);
        Dset2 = findViewById(R.id.radioButton5);

        //設定値の初期値設定
        config_value = getSharedPreferences("Config", MODE_PRIVATE);
        editor = config_value.edit();

        setTime.setProgress(config_value.getInt("SeekValue", 0));
        setGyro.setChecked(config_value.getBoolean("GyroChecked", false));
        setDevice.check(config_value.getInt("Device", R.id.radioButton4));
        if(config_value.getBoolean("GyroChecked", false)){
            Dset2.setEnabled(false);
        }

        //時間設定(シークバー)
        setTime.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //ツマミのドラッグ時の処理
                        System.out.println(setTime.getProgress());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //ツマミに触ったときの処理
                        System.out.println(setTime.getProgress());
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //ツマミを離した時の処理
                        editor.putInt("SeekValue", seekBar.getProgress()).apply();
                        System.out.println(setTime.getProgress());
                    }
                }
        );

        //ジャイロ計測設定(スイッチ)
        setGyro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (setGyro.isChecked()) {
                //スイッチON
                editor.putBoolean("GyroChecked", true).apply();
                setDevice.check(R.id.radioButton4);
                Dset2.setEnabled(false);
            } else {
                //スイッチOFF
                editor.putBoolean("GyroChecked", false).apply();
                Dset2.setEnabled(true);
            }
            editor.commit();
        });

        //端末設置場所の設定(ラジオボタン)
        setDevice.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    //ラジオボタン選択時の処理
                    editor.putInt("Device", checkedId).apply();
                }
        );
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
