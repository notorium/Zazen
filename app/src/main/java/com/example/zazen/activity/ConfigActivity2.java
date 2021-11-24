package com.example.zazen.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zazen.R;

public class ConfigActivity2 extends AppCompatActivity {
    private SeekBar setTime;
    private Switch setShowTimer;
    private Button upTime, downTime;
    private EditText timeEditText;
    private RadioGroup setTimeSelect;
    private RadioButton Dset1, Dset2;
    private View config2;

    static SharedPreferences config_value2;
    static SharedPreferences.Editor editor2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config2);

        //各ツールの変数設定
        setTime = findViewById(R.id.time_seekBar);
        setShowTimer = findViewById(R.id.timerSwitch);
        setTimeSelect = findViewById(R.id.device_radioGroup);
        upTime = findViewById(R.id.timeUpButton);
        downTime = findViewById(R.id.timeDownButton);
        timeEditText = findViewById(R.id.timeEditText);
        Dset1 = findViewById(R.id.radioButton1);
        Dset2 = findViewById(R.id.radioButton2);
        config2 = findViewById(R.id.config2);

        //設定値の初期値設定
        config_value2 = getSharedPreferences("Config", MODE_PRIVATE);
        editor2 = config_value2.edit();

        setTime.setProgress(config_value2.getInt("SeekValue", 0));
        timeEditText.setText(Integer.toString(config_value2.getInt("TextValue", 10)));
        setShowTimer.setChecked(config_value2.getBoolean("TimeChecked", false));
        setTimeSelect.check(config_value2.getInt("Select", R.id.radioButton1));

        switch (setTimeSelect.getCheckedRadioButtonId()) {
            case R.id.radioButton1:
                setTime.setEnabled(true);
                upTime.setEnabled(false);
                downTime.setEnabled(false);
                timeEditText.setEnabled(false);
                break;
            case R.id.radioButton2:
                setTime.setEnabled(false);
                upTime.setEnabled(true);
                downTime.setEnabled(true);
                timeEditText.setEnabled(true);
                break;
        }

        //EditTextのフォーカスを外す
        config2.setOnClickListener(v -> {
            timeEditText.clearFocus();
            editor2.putInt("TextValue", Integer.parseInt(String.valueOf(timeEditText.getText()))).apply();
        });

        //時間の詳細設定(ラジオボタン)
        setTimeSelect.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    editor2.putInt("TextValue", Integer.parseInt(String.valueOf(timeEditText.getText()))).apply();
                    switch (checkedId) {
                        case R.id.radioButton1:
                            setTime.setEnabled(true);
                            upTime.setEnabled(false);
                            downTime.setEnabled(false);
                            timeEditText.setEnabled(false);
                            break;
                        case R.id.radioButton2:
                            setTime.setEnabled(false);
                            upTime.setEnabled(true);
                            downTime.setEnabled(true);
                            timeEditText.setEnabled(true);
                            break;
                    }
                    //ラジオボタン選択時の処理
                    editor2.putInt("Select", checkedId).apply();
                }
        );

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
                        editor2.putInt("TextValue", Integer.parseInt(String.valueOf(timeEditText.getText()))).apply();
                        System.out.println(setTime.getProgress());
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //ツマミを離した時の処理
                        editor2.putInt("SeekValue", seekBar.getProgress()).apply();
                        System.out.println(setTime.getProgress());
                    }
                }
        );

        upTime.setOnClickListener(v -> {
            if (config_value2.getInt("TextValue", 10) < 999) {
                int tmp = config_value2.getInt("TextValue", 10);
                tmp++;
                timeEditText.setText(Integer.toString(tmp));
                editor2.putInt("TextValue", tmp).apply();
            }
        });

        downTime.setOnClickListener(v -> {
            if (config_value2.getInt("TextValue", 10) > 1) {
                int tmp = config_value2.getInt("TextValue", 10);
                tmp--;
                timeEditText.setText(Integer.toString(tmp));
                editor2.putInt("TextValue", tmp).apply();
            }
        });

        //時間表示設定(スイッチ)
        setShowTimer.setOnCheckedChangeListener((v, isChecked) -> {
            editor2.putInt("TextValue", Integer.parseInt(String.valueOf(timeEditText.getText()))).apply();
            if (setShowTimer.isChecked()) {
                //スイッチON
                editor2.putBoolean("GyroChecked", true).apply();
            } else {
                //スイッチOFF
                editor2.putBoolean("GyroChecked", false).apply();
            }
            editor2.commit();
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
                                "\n・計測をONにする場合は、端末の設置場所は手の上固定となります。" +
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
        editor2.putInt("TextValue", Integer.parseInt(String.valueOf(timeEditText.getText()))).apply();
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
