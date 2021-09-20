package com.example.zazen.async;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest_GET_Img extends AsyncTask<String, Void, Bitmap> {
    private Activity callerActivity;

    public HttpRequest_GET_Img(Activity activity) {
        callerActivity = activity;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        HttpURLConnection con = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(params[0]);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setReadTimeout(10000);
            con.setConnectTimeout(5000);
            con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            con.connect();

            switch (con.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    try (InputStream is = con.getInputStream()) {
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return bitmap;
    }

    public void onPostExecute(JSONObject json) {
        StringBuilder builder = new StringBuilder();
        try {
            JSONArray array = json.getJSONArray("");
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject obj = array.getJSONObject(i);
//                builder.append(obj.getString("no") + "\n");
//                builder.append(obj.getString("name") + "\n");
//                builder.append(obj.getJSONObject("address").getString("state"));
//                builder.append(obj.getJSONObject("address").getString("city"));
//                builder.append(obj.getJSONObject("address").getString("address1") + "\n");
//                builder.append(obj.getString("phone") + "\n");
//                builder.append(obj.getString("mail") + "\n");
//            }
            System.out.println(json);
        } catch (JSONException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
