package com.artezio.model;

import com.artezio.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 12.07.12
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class Price extends JSONObject implements Serializable{
    public Price() {
    }

    public Price(JSONObject copyFrom) throws JSONException {
        super(copyFrom, new String[]{Constants.JSON.PRICE,Constants.JSON.DATE,Constants.JSON.STORE});
    }

    public Double getPrice(){
        try {
            return getDouble(Constants.JSON.PRICE);
        } catch (JSONException e) {
            return null;
        }
    }

    public void setPrice(double price){
        try {
            put(Constants.JSON.PRICE,price);
        } catch (JSONException e) {

        }
    }
    public void setStoreKey(String store){
        try {
            put(Constants.JSON.STORE,store);
        } catch (JSONException e) {

        }
    }

    public String getDate() {
        try {
            return getString(Constants.JSON.DATE);
        } catch (JSONException e) {
            return null;
        }
    }

    public int getStoreId() {
        try {
            return getInt(Constants.JSON.STORE);
        } catch (JSONException e) {
            return 0;
        }
    }
}
