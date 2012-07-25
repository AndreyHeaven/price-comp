package com.artezio.tasks;

import android.content.Context;
import com.artezio.Constants;
import com.artezio.JsonAdapter;
import com.artezio.net.JsonHelper;
import com.google.android.maps.GeoPoint;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 7/20/12
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DownloadStoresTask extends AbstractProgressAsyncTask<GeoPoint> {
    private JsonAdapter adapter;

    public DownloadStoresTask(JsonAdapter adapter, Context context) {
        super(context, "Download stores");
        this.adapter = adapter;
    }

    @Override
    protected String doInBackground(GeoPoint... points) {
        GeoPoint point = points[0];
        int i = 500;
        return getStores(point, i);
    }

    public static String getStores(GeoPoint point, int i) {
        return JsonHelper.get(String.format(Constants.URL_GET_STORES, point.getLatitudeE6(), point.getLongitudeE6(), i));
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONArray arr = new JSONArray(s);
            adapter.setData(arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
