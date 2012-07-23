package com.artezio.net;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.artezio.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 7/19/12
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonHelper {

    public static String put(String url, Context context, JSONObject o, String... keys) {
        StringBuilder builder = new StringBuilder();
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(params, "utf-8");
        HttpClient client = new DefaultHttpClient(params);
        HttpPut httpPut = new HttpPut(Constants.APP_URL+url);
        httpPut.setHeader("charset", "utf-8");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        Account[] accounts= AccountManager.get(context).getAccountsByType("com.google");
        String email = null;
        if (accounts.length > 0)
            email = accounts[0].name;
        for (String key : keys) {
            try {
                pairs.add(new BasicNameValuePair(key, o.getString(key)));
            } catch (JSONException e) {
                //
            }
        }
        if (email != null)
            pairs.add(new BasicNameValuePair("user", email));

        try {
            httpPut.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = client.execute(httpPut);
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
                Log.e(JsonHelper.class.toString(), "Failed to upload json "+ o.toString());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String get(String url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Constants.APP_URL + url);
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
                Log.e(JsonHelper.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

}
