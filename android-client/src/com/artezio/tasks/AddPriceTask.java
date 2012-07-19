package com.artezio.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import com.artezio.Constants;
import com.artezio.model.Price;
import com.artezio.net.JsonHelper;
import org.json.JSONException;

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
        return JsonHelper.put(Constants.URL_ADD_PRICE, activity, prices[0], Constants.JSON.CODE, Constants.JSON.PRICE, Constants.JSON.STORE);
    }


}
