package com.example.zazen.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.example.zazen.R;
import com.example.zazen.activity.LoginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpRequest_POST_Data extends AsyncTask<String, Void, JSONObject> {
    private Activity callerActivity;
    private String postData;
    private Button resultButton;

    private ProgressDialog postProgress;

    private boolean errorFlg = false;

    public HttpRequest_POST_Data(Activity activity, String string) {
        callerActivity = activity;
        postData = string;
        resultButton = callerActivity.findViewById(R.id.postResultButton);
    }


    @Override
    protected JSONObject doInBackground(String... params) {
        HttpURLConnection con = null;
        StringBuilder builder = new StringBuilder();
        JSONObject json = new JSONObject();
        try {
            URL url = new URL(params[0]);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            con.connect();

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(postData);
            out.flush();

            InputStream stream = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null)
                builder.append(line);
            stream.close();
            json = new JSONObject(builder.toString());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
            errorFlg = true;
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(e);
            errorFlg = true;
        } finally {
            con.disconnect();
        }
        return json;
    }

    @Override
    protected void onPreExecute() {
        postProgress = new ProgressDialog(this.callerActivity);
        postProgress.setMessage("データ送信中...");
        postProgress.show();
        return;
    }

    public void onPostExecute(JSONObject json) {
        if(errorFlg){
            resultButton.setEnabled(true);
            new AlertDialog.Builder(this.callerActivity)
                    .setCancelable(false)
                    .setTitle("エラー")
                    .setMessage("通信エラーが発生しました。\n通信環境を整えてやり直してください。")
                    .setPositiveButton("閉じる",null)
                    .show();
        }else{
            new AlertDialog.Builder(this.callerActivity)
                    .setCancelable(false)
                    .setTitle("送信完了")
                    .setMessage("データの送信が完了しました。")
                    .setPositiveButton("閉じる",null)
                    .show();
        }
        postProgress.dismiss();
        System.out.println(json);
    }
}
