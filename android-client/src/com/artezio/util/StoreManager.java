package com.artezio.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.artezio.model.Store;
import com.artezio.net.OverpassHelper;
import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.GeocellUtils;
import com.beoui.geocell.model.BoundingBox;
import com.beoui.geocell.model.CostFunction;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 8/14/12
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoreManager {

    private Context ctx;
    private int i;

    public StoreManager(Context ctx) {
        this.ctx = ctx;
        i = 7;
//        i = getResolution();
    }

    public List<Store> getInArea(double north, double east, double south, double west) {
        BoundingBox bb = new BoundingBox(north, east, south, west);
        List<String> cells = GeocellManager.bestBboxSearchCells(bb, new CostFunction() {

            public double defaultCostFunction(int numCells, int resolution) {


                if (resolution == i) {
                    return 0;
                } else {
                    return Double.MAX_VALUE;
                }
            }
        });
        cells = excludeCells(cells);
        List<Store> shops = new ArrayList<Store>();
        for (String cell : cells) {
            BoundingBox box = GeocellUtils.computeBox(cell);
            shops.addAll(OverpassHelper.getShops(ctx, box.getSouth(), box.getWest(), box.getNorth(), box.getEast(), 100));
        }
        return shops;
    }

    private List<String> excludeCells(List<String> cells) {
        File root = new File(Environment.getExternalStorageDirectory(),".price-comp");
        if (!root.exists()) {
            if (!root.mkdirs()) {
                Log.e(getClass().getName()+" :: ", "Problem creating Image folder");
            }
        }
        File[] files = root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        for (File file : files) {
            if (cells.contains(file.getName())){

            }
        }
        return cells;//TODO exclude cells that present in cache and alive
    }

    private int getResolution() {
        //6 - WIFI
        //7 - 3G
        //8 - EDGE
        final ConnectivityManager connectManager =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectManager.getActiveNetworkInfo();
        // Only update if WiFi or 3G is connected and not roaming
        int netType = info.getType();
        int netSubtype = info.getSubtype();
        int i = 8;
        if (netType == ConnectivityManager.TYPE_WIFI) {
            i = 6;
        } else if (netType == ConnectivityManager.TYPE_MOBILE
                && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS) {
            i = 7;
        }
        return i;
    }
}
