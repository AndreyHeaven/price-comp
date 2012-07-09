package com.artezio;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: butt
 * Date: 09.07.12
 * Time: 22:53
 */
public class DownloadPrices extends AsyncTask<String,Integer,JSONArray>{
    public static final String URL_ITEM = "http://dl.dropbox.com/u/618578/prices.json";
    private ArrayAdapter<BasicNameValuePair> adapter;

    public DownloadPrices(ArrayAdapter<BasicNameValuePair> adapter) {
        this.adapter = adapter;
    }

    @Override
    protected JSONArray doInBackground(String... strings) {
        if (strings == null || strings.length < 1)
            return null;
        return getPrices(strings[0]);
    }

    public JSONArray getPrices(String code){
        String content = DownloadItemDetailsTask.downloadJson(URL_ITEM);
        try {
            return new JSONArray(content);
        } catch (JSONException e) {
            return null;
        }

    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        for(int i=0; i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                adapter.add(new BasicNameValuePair(jsonObject.getString("price"),jsonObject.getString("date")));
            } catch (JSONException e) {

            }
        }
    }
}
