package com.artezio;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: butt
 * Date: 09.07.12
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class DownloadItemDetailsTask extends AsyncTask<String, Integer, JSONObject> {
    public static final String URL_ITEM = "http://dl.dropbox.com/u/618578/item.json";
//    public static final String URL_ITEM = "http://dl.dropbox.com/u/618578/%s.json";
    private PricesListActivity textView;

    public DownloadItemDetailsTask(PricesListActivity textView) {
        this.textView = textView;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
//            publishProgress(0);
        if (strings == null || strings.length < 1)
            return null;
        try {
            return getItem(downloadItemJson(strings[0]));
        } catch (JSONException e) {
            return null;
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        if (jsonObject == null){
            textView.setTitle("");
            return;
        }
        try {
            textView.setTitle(jsonObject.getString("name"));
        } catch (JSONException e) {
            textView.setTitle("");
        }
    }

    public JSONObject getItem(String code) throws JSONException {
        return new JSONObject(downloadItemJson(code));
    }

    public String downloadItemJson(String code) {
        return downloadJson(String.format(URL_ITEM, code));
    }

    public static String downloadJson(String url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if ((statusCode /100) == 2) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(PricesListActivity.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

}