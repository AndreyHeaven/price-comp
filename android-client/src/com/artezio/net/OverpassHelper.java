package com.artezio.net;

import android.content.Context;
import com.artezio.Constants;
import com.artezio.model.Store;
import com.google.android.maps.GeoPoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 8/13/12
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverpassHelper {

    public static List<Store> getShops(Context context, double s, double w, double n, double e, int count) {
//        "[out:json];\n" +
//                "(\n" +
//                "way\n" +
//                "  [shop]\n" +
//                "  [building=yes]\n" +
//                "  (51.5,46.1,51.6,46.2);\n" +
//                "node\n" +
//                "  [shop]\n" +
//                "  (51.5,46.1,51.6,46.2);\n" +
//                ");\n" +
//                "out 100;"
        StringBuilder builder = new StringBuilder("[out:json];");
        builder.append('(');
        builder.append("way[shop][building=yes]").append("(")
                .append(s).append(',')
                .append(w).append(',')
                .append(n).append(',')
                .append(e)
                .append(");");
        builder.append("node[shop]").append("(")
                .append(s).append(',')
                .append(w).append(',')
                .append(n).append(',')
                .append(e)
                .append(");");
        builder.append(");");
        builder.append("out ").append(count).append(";");
        Map<String, String> map = new HashMap<String, String>();
        map.put("data", builder.toString());
        String post = JsonHelper.post("http://overpass-api.de/api/interpreter", context, map, false);
        try {

            JSONObject obj = new JSONObject(post);
            JSONArray arr = obj.getJSONArray("elements");
            List<Store> stores = new ArrayList<Store>();
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject point = arr.getJSONObject(i);
                    Store store = new Store();
                    store.setOsmId(point.getInt("id"));
                    store.setPoint(new GeoPoint((int) (point.getDouble(Constants.JSON.LATITUDE) * 1e6), (int) (point.getDouble(Constants.JSON.LONGITUDE) * 1e6)));
                    JSONObject tags = point.getJSONObject("tags");
                    store.setType(tags.getString("shop"));
                    try {
                        store.setName(tags.getString("name"));
                    } catch (JSONException ex) {
//                        store.setName("--");
                    }
                    stores.add(store);
                } catch (JSONException ex) {
                    //
                }
            }
            return stores;
        } catch (JSONException e1) {
            return new ArrayList<Store>();
        }
    }
}
