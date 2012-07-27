package com.artezio.tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import com.artezio.Constants;
import com.artezio.PricesListActivity;
import com.artezio.net.JsonHelper;
import com.artezio.util.Utils;
import com.google.android.maps.GeoPoint;

/**
 * User: butt
 * Date: 09.07.12
 * Time: 22:44
 */
public class DownloadItemDetailsTask extends AbstractProgressAsyncTask<String> {
    private PricesListActivity activity;

    public DownloadItemDetailsTask(PricesListActivity activity, String msg) {
        super(activity, msg);
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (strings == null || strings.length < 1)
            return null;
        GeoPoint location = Utils.getLocationPoint(activity);
        return downloadItemJson(strings[0], location, activity.getRadius());
    }

    @Override
    protected void onPostExecute(String jsonObject) {
        super.onPostExecute(jsonObject);
//        intent.putExtra("item", jsonObject);
        String s = JsonHelper.checkErrors(activity, jsonObject);
        if (s != null) {
            new AlertDialog.Builder(activity).setMessage(s)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create().show();
        } else {
            activity.setItem(jsonObject);
        }
    }

    public String downloadItemJson(String code, GeoPoint location, int radius) {
        if (location != null)
            return JsonHelper.get(String.format(Constants.URL_ITEM, code, location.getLatitudeE6(), location.getLongitudeE6(), radius));
        else
            return JsonHelper.get(String.format(Constants.URL_ITEM, code, null, null, radius));
    }


}