package com.example.zazen.async;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

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

    public HttpRequest_POST_Data(Activity activity, String string) {
        callerActivity = activity;
        postData = string;
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
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(e);
        } finally {
            con.disconnect();
        }
        return json;
    }

    public void onPostExecute(JSONObject json) {
        System.out.println(json);
    }
}
