package com.example.zazen.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zazen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.zazen.activity.StartActivity.loginStatus;
import static com.example.zazen.activity.StartActivity.login_editor;

public class HttpRequest_POST_Login extends AsyncTask<String, Void, JSONObject> {

    private final Activity callerActivity;
    private TextView errorText;
    private Button loginButton;
    private EditText useridInput, passwordInput;
    private final String postData;
    private ProgressDialog loginProgress;

    public HttpRequest_POST_Login(Activity activity, String string) {
        callerActivity = activity;
        postData = string;
        errorText = callerActivity.findViewById(R.id.errorText);
        loginButton = callerActivity.findViewById(R.id.loginButton);
        useridInput = callerActivity.findViewById(R.id.userid_editText);
        passwordInput = callerActivity.findViewById(R.id.password_editText);
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
            errorText.setText("通信エラーが発生しました。");
            errorText.setTextColor(Color.rgb(255, 0, 0));
        } catch (JSONException e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return json;
    }

    @Override
    protected void onPreExecute() {
        loginProgress = new ProgressDialog(this.callerActivity);
        loginProgress.setMessage("ログイン中...");
        loginProgress.show();
        return;
    }

    public void onPostExecute(JSONObject json) {
        boolean result = false;
        String userid = null, username = null;
        System.out.println(json);
        try {
            result = json.getBoolean("result");
            userid = json.getString("UserId");
            username = json.getString("UserName");

            if (result) {
                login_editor.putString("UserId", userid).apply();
                login_editor.putString("UserName", username).apply();
                login_editor.putBoolean("LoginFlg", true).apply();
            } else {
                errorText.setText("ユーザIDまたはパスワードが違います。");
                errorText.setTextColor(callerActivity.getResources().getColor(R.color.red));
            }
        } catch (JSONException e) {
            System.out.println(e);
//            errorText.setText("ユーザIDまたはパスワードが違います。");
            errorText.setTextColor(callerActivity.getResources().getColor(R.color.red));
            e.printStackTrace();
        }

        if (loginStatus.getBoolean("LoginFlg", false)) {
            errorText.setText("ログインしました");
            errorText.setTextColor(callerActivity.getResources().getColor(R.color.green));
//            useridInput.setText("");
//            passwordInput.setText("");
            useridInput.setEnabled(false);
            passwordInput.setEnabled(false);
        } else {
            loginButton.setEnabled(true);
        }

        loginProgress.dismiss();
    }
}
