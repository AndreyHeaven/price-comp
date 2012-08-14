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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final long WEEK = 1000 * 60 * 60 * 24 * 7;


    public StoreManager(Context ctx) {
        this.ctx = ctx;
        i = 7;
//        i = getResolution();
    }

    public List<Store> getInArea(double north, double east, double south, double west) {
        BoundingBox bb = new BoundingBox(north, east, south, west);
        List<String> cells = GeocellManager.bestBboxSearchCells(bb, new CostFunction() {

            public double defaultCostFunction(int numCells, int resolution) {
                return resolution == i ? 0 : Double.MAX_VALUE;
            }
        });

        final List<String> finalCells = new ArrayList<String>();
        final List<String> inCache = new ArrayList<String>();
        excludeCells(cells,inCache,finalCells);
        if (!cells.isEmpty())
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Store> shops = new ArrayList<Store>();
                    for (String cell : finalCells) {
                        BoundingBox box = GeocellUtils.computeBox(cell);
                        List<Store> s = OverpassHelper.getShops(ctx, box.getSouth(), box.getWest(), box.getNorth(), box.getEast(), 100);
                        shops.addAll(s);
                    }
                }
            }).start();
        return loadFromCache(inCache);
    }

    private List<Store> loadFromCache(final List<String> inCache) {
        File root = new File(Environment.getExternalStorageDirectory(), ".price-comp");
        if (!root.exists()) {
            if (!root.mkdirs()) {
                Log.e(getClass().getName() + " :: ", "Problem creating Image folder");
                return new ArrayList<Store>();
            }
        }
        root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return inCache.contains(s);
            }
        });
        return new ArrayList<Store>(); //TODO implement
    }

    private void excludeCells(List<String> cells, List<String> inCache,List<String> needtoload) {
        File root = new File(Environment.getExternalStorageDirectory(), ".price-comp");
        if (!root.exists()) {
            if (!root.mkdirs()) {
                Log.e(getClass().getName() + " :: ", "Problem creating Image folder");
                return;
            }
        }
        List<String> files = Arrays.asList(root.list());
        for (String cell : cells) {
            File file = new File(root,cell);
            if (!files.contains(cell)) {
                needtoload.add(cell);
            } else if (file.lastModified() + WEEK > System.currentTimeMillis()){
                file.delete();
                needtoload.add(cell);
            } else
                inCache.add(cell);
        }
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
