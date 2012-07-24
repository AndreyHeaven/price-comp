package com.artezio.model;

import com.artezio.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 12.07.12
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
public class Item extends JSONObject {

    private ArrayList<Price> prices;

    public Item(String json) throws JSONException {
        super(json);
    }

    public Item(Map copyFrom) {
        super(copyFrom);
    }

    public String getName() {
        try {
            return getString(Constants.JSON.NAME);
        } catch (JSONException e) {
            return null;
        }
    }
    public String getCode() {
        try {
            return getString(Constants.JSON.CODE);
        } catch (JSONException e) {
            return null;
        }
    }

    public List<Price> getPrices() {
        if (prices == null) {
            prices = new ArrayList<Price>();
            try {
                JSONArray jsonArray = getJSONArray(Constants.JSON.PRICES);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Price price = new Price(jsonObject);
                    prices.add(price);
                }
            } catch (JSONException e) {
                //
            }
        }
        return prices;
    }
}
