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
        setShowTimer.setChecked(config_value2.getBoolean("TimeChecked", true));
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
//        config2.setOnClickListener(v -> {
//            timeEditText.clearFocus();
//            textCheck();
//        });

        //時間の詳細設定(ラジオボタン)
        setTimeSelect.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    textCheck();
                    switch (checkedId) {
                        case R.id.radioButton1:
                            setTime.setEnabled(true);
                            upTime.setEnabled(false);
                            downTime.setEnabled(false);
                            timeEditText.setEnabled(false);
                            editor2.putInt("SelectNum", 1).apply();
                            break;
                        case R.id.radioButton2:
                            setTime.setEnabled(false);
                            upTime.setEnabled(true);
                            downTime.setEnabled(true);
                            timeEditText.setEnabled(true);
                            editor2.putInt("SelectNum", 2).apply();
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
                        textCheck();
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
            } else {
                timeEditText.setText("1");
                editor2.putInt("TextValue", 1).apply();
            }
        });

        downTime.setOnClickListener(v -> {
            if (config_value2.getInt("TextValue", 10) > 1) {
                int tmp = config_value2.getInt("TextValue", 10);
                tmp--;
                timeEditText.setText(Integer.toString(tmp));
                editor2.putInt("TextValue", tmp).apply();
            } else {
                timeEditText.setText("999");
                editor2.putInt("TextValue", 999).apply();
            }
        });

        //時間表示設定(スイッチ)
        setShowTimer.setOnCheckedChangeListener((v, isChecked) -> {
            textCheck();
            if (setShowTimer.isChecked()) {
                //スイッチON
                editor2.putBoolean("TimeChecked", true).apply();
            } else {
                //スイッチOFF
                editor2.putBoolean("TimeChecked", false).apply();
            }
            editor2.commit();
        });
    }

    public void textCheck() {
        int num;
        try {
            num = Integer.valueOf(timeEditText.getText().toString());
            if (num <= 0) {
                num = 1;
            }
        } catch (NumberFormatException e) {
            num = 1;
        }
        timeEditText.setText(Integer.toString(num));
        editor2.putInt("TextValue", num).apply();
    }

    public void help(View v) {
        switch (getResources().getResourceEntryName(v.getId())) {
            case "help1":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("時間設定")
                        .setMessage("・規定値、指定なしまたはカスタムで時間を設定できます。\n" +
                                "・時間指定なしの上限は999分です。\n" +
                                "・カスタムで設定できる時間は1～999分です。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
            case "help2":
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("時間表示設定")
                        .setMessage("・画面に時間を表示するか設定します。")
                        .setPositiveButton("閉じる", null)
                        .show();
                break;
        }
    }

    public void start(View v) {
        textCheck();
        //開始前確認メッセージ
        new AlertDialog.Builder(this)
                .setTitle("開始前確認")
                .setCancelable(false)
                .setMessage("この設定で開始しますか？")
                .setPositiveButton("はい", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                    startActivity(intent);
                    this.finish();
                })
                .setNegativeButton("いいえ", null)
                .show();
    }
}
