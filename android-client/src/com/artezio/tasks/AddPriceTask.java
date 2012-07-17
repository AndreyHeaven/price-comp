package com.artezio.tasks;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.artezio.Constants;
import com.artezio.model.Price;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * User: araigorodskiy
 * Date: 12.07.12
 * Time: 10:17
 */
public class AddPriceTask extends AsyncTask<Price, Integer, String> {
    private Activity activity;

    public AddPriceTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Price... prices) {
        if (prices == null || prices.length < 1)
            return null;
        String code = null;
        try {
            code = prices[0].getString(Constants.JSON.ITEM);
        } catch (JSONException e) {
            //
        }
        return putPrice(String.format(Constants.URL_ADD_PRICE, code), code, prices[0]);
    }


    public String putPrice(String url, String code, Price price) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        Account[] accounts= AccountManager.get(activity.getBaseContext()).getAccountsByType("com.google");
        String email = null;
        if (accounts.length > 0)
            email = accounts[0].name;
        pairs.add(new BasicNameValuePair("code", code));
        pairs.add(new BasicNameValuePair("price", price.getPrice().toString()));
        pairs.add(new BasicNameValuePair("accuracy", "10"));
        pairs.add(new BasicNameValuePair("longitude", price.getLongitude().toString()));
        pairs.add(new BasicNameValuePair("latitude", price.getLatitude().toString()));
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
                Log.e(AddPriceTask.class.toString(), "Failed to upload price");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
