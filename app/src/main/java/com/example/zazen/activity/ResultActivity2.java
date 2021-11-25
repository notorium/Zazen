package com.example.zazen.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.zazen.R;

public class ResultActivity2 extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);
        textView = findViewById(R.id.textView21);
        textView.setText(getIntent().getStringExtra("SetTime"));
    }

    public void onBackPressed() {
        backHome();
    }

    public void onHomeButton(View v) {
        backHome();
    }

    public void backHome() {
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
        this.finish();
    }
}