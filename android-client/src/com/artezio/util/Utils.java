package com.artezio.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.android.maps.GeoPoint;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 11.07.12
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    private static Location location;

    public static String findTargetAppPackage(Activity activity, Intent intent) {
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> availableApps = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (availableApps != null) {
            for (ResolveInfo availableApp : availableApps) {
                String packageName = availableApp.activityInfo.packageName;
                if ("com.google.zxing.client.android".equals(packageName)) {
                    return packageName;
                }
            }
        }
        return null;
    }

    public static AlertDialog showDownloadDialog(final Activity activity) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle("Install Barcode Scanner?");
        downloadDialog.setMessage("This application requires Barcode Scanner. Would you like to install it?");
        downloadDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://details?id=" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    // Hmm, market is not installed
                    Log.w(Utils.class.getName(), "Android Market is not installed; cannot install Barcode Scanner");
                }
            }
        });
        downloadDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        return downloadDialog.show();
    }

    public static Location getLocation(Activity activity){
        updateLocation(activity);
        return location;
    }
     public static GeoPoint getLocationPoint(Activity activity){
         Location location = getLocation(activity);
         if (location == null)
             return null;
         return new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
     }
    private static void updateLocation(Activity activity) {
        LocationManager locationManager;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (location == null) {
            try {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 500L, 250.0f, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Utils.setLocation(location);
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onProviderEnabled(String s) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onProviderDisabled(String s) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
            } catch (Exception e) {
                //
            }
        }
    }

    private static void setLocation(Location l) {
        location = l;
    }

}
