package com.artezio.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.artezio.Constants;
import com.artezio.model.Store;
import com.artezio.net.JsonHelper;

/**
 * User: araigorodskiy
 * Date: 7/19/12
 * Time: 2:10 PM
 */
public class AddStoreTask extends AsyncTask<Store, Void, String> {
    private Context context;

    public AddStoreTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Store... stores) {
        if (stores == null || stores.length < 1)
            return null;
        JsonHelper.put(Constants.URL_STORE, context, stores[0], Constants.JSON.NAME, Constants.JSON.LATITUDE, Constants.JSON.LONGITUDE);
        return null;
    }
}
