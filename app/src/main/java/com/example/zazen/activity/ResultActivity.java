package com.example.zazen.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zazen.R;
import com.example.zazen.async.HttpRequest_POST;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    String timeData, measurementData;
    boolean gyroFlg = ConfigActivity.config_value.getBoolean("GyroChecked", false);

    EditText commentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        commentText = findViewById(R.id.editText);

        Intent intent = getIntent();
        timeData = gyroFlg ? intent.getStringExtra("TIME_DATA") : "";
        measurementData = gyroFlg ? intent.getStringExtra("MEASUREMENT_DATA") : "";
        TextView view = findViewById(R.id.textView7);
        view.setText(timeData);
    }

    public void postResult(View v) {
        SimpleDateFormat DF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN);
        String date = DF.format(new Date());
        String postStr = "{\"user_id\":\"" + "test" +
                "\",\"date\":\"" + date +
                "\",\"time\":\"" + "" +
                "\",\"comment\":\"" + commentText.getText().toString() +
                "\",\"flg\":\"" + (gyroFlg ? "1" : "0") +
                "\",\"timedata\":\"" + timeData +
                "\",\"measurementdata\":\"" + measurementData +
                "\"}";
        HttpRequest_POST httpRequestPost = new HttpRequest_POST(this, postStr);
        httpRequestPost.execute("http://fukuiohr2.sakura.ne.jp/2021/Zazen/postdata.php");
//        httpreq2.execute("http://fukuiohr2.sakura.ne.jp/2021/Zazen/test.php");
    }
}