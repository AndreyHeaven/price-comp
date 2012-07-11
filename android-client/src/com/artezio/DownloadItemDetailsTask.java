package com.artezio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: butt
 * Date: 09.07.12
 * Time: 22:44
 */
public class DownloadItemDetailsTask extends AsyncTask<String, Integer, String> {
    private MainActivity mainActivity;
    private Intent intent;
    private ProgressDialog progress;

    public DownloadItemDetailsTask(MainActivity mainActivity, Intent intent) {
        progress = new ProgressDialog(mainActivity);
        progress.setIndeterminate(true);
        progress.setMessage(mainActivity.getBaseContext().getString(R.string.label, intent.getStringExtra(Constants.CODE)));
        this.mainActivity = mainActivity;
        this.intent = intent;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        if (strings == null || strings.length < 1)
            return null;
        return downloadItemJson(strings[0], mainActivity.getLocation());
    }

    @Override
    protected void onPostExecute(String jsonObject) {
        progress.dismiss();
        intent.putExtra("item", jsonObject);
        mainActivity.startActivity(intent);
    }

    public String downloadItemJson(String code, Location location) {
        if (location != null)
            return downloadJson(String.format(Constants.URL_ITEM, code, location.getLatitude(), location.getLongitude(), location.getAccuracy()));
        else
            return downloadJson(String.format(Constants.URL_ITEM, code, null, null, null));
    }

    public static String downloadJson(String url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if ((statusCode / 100) == 2) {
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