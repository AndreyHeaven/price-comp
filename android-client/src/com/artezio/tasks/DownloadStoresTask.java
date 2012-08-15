package com.artezio.tasks;

import android.content.Context;
import com.artezio.Constants;
import com.artezio.JsonAdapter;
import com.artezio.model.Downloadable;
import com.artezio.model.Store;
import com.artezio.net.JsonHelper;
import com.artezio.util.StoreManager;
import com.google.android.maps.GeoPoint;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

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
        int i = 5000;
        return getStores(point, i);
    }

    public String getStores(GeoPoint point, int i) {
        double sLat = (point.getLatitudeE6() - i) / 1e6;
        double wLon = (point.getLongitudeE6() - i) / 1e6;
        double nLat = (point.getLatitudeE6() + i) / 1e6;
        double eLon = (point.getLongitudeE6() + i) / 1e6;

        List<Store> inArea = new StoreManager(getContext()).getInArea(nLat, eLon, sLat, wLon, new Downloadable() {
            @Override
            public void loaded(List<Store> stores) {
                JSONArray arr = new JSONArray();
                for (Store store : stores) {
                    arr.put(store);
                }
                adapter.setData(arr);
            }
        });
        JSONArray arr = new JSONArray();
        for (Store store : inArea) {
            arr.put(store);
        }
        return arr.toString();
//        return JsonHelper.get(String.format(Constants.URL_GET_STORES, point.getLatitudeE6(), point.getLongitudeE6(), i));
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
