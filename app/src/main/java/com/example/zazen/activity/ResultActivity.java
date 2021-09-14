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

    String accelerationData, rotationData;
    boolean gyroFlg = ConfigActivity.config_value.getBoolean("GyroChecked", false);

    EditText commentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        commentText = findViewById(R.id.editText);

        Intent intent = getIntent();
        accelerationData = gyroFlg ? intent.getStringExtra("ACCELERATION_DATA") : "";
        rotationData = gyroFlg ? intent.getStringExtra("ROTATION_DATA") : "";


//        TextView view = findViewById(R.id.textView7);
//        view.setText(timeData);
    }

    public void postResult(View v) {
        v.setEnabled(false);
        SimpleDateFormat DF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN);
        String date = DF.format(new Date());
        String postStr = "{\"user_id\":\"" + "test" +
                "\",\"date\":\"" + date +
                "\",\"time\":\"" + "" +
                "\",\"comment\":\"" + commentText.getText().toString() +
                "\",\"selfassessment\":\"" + "0" +
                "\",\"flg\":\"" + (gyroFlg ? "1" : "0") +
                "\",\"accelerationdata\":\"" + accelerationData +
                "\",\"rotationdata\":\"" + rotationData +
                "\",\"weather_id\":\"" + "1" +
                "\"}";
        HttpRequest_POST httpRequestPost = new HttpRequest_POST(this, postStr);
        httpRequestPost.execute("http://fukuiohr2.sakura.ne.jp/2021/Zazen/postdata.php");
    }

    public void shareResult(View v) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(shareIntent);
    }
}