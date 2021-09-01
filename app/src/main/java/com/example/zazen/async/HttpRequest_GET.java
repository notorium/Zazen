package com.example.zazen.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest_GET extends AsyncTask<String, Void, JSONObject> {
    private Activity callerActivity;

    public HttpRequest_GET(Activity activity) {
        callerActivity = activity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpURLConnection con = null;
        StringBuilder builder = new StringBuilder();
        JSONObject json = new JSONObject();
        try {
            URL url = new URL(params[0]);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            con.connect();

            InputStream stream = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null)
                builder.append(line);
            stream.close();

            json = new JSONObject(builder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return json;
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
