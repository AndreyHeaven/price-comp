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
        super(copyFrom, new String[]{Constants.JSON.PRICE,Constants.JSON.DATE,Constants.JSON.LONGITUDE,Constants.JSON.LATITUDE});
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

    public Double getLongitude() {
        try {
            return getDouble(Constants.JSON.LONGITUDE);
        } catch (JSONException e) {
            return null;
        }
    }
    public Double getLatitude() {
        try {
            return getDouble(Constants.JSON.LATITUDE);
        } catch (JSONException e) {
            return null;
        }
    }

    public void setLatitude(double latitude) {
        try {
            put(Constants.JSON.LATITUDE,latitude);
        } catch (JSONException e) {
            //
        }
    }

    public void setLongitude(double l) {
        try {
            put(Constants.JSON.LONGITUDE,l);
        } catch (JSONException e) {
            //
        }
    }

    public String getDate() {
        try {
            return getString(Constants.JSON.DATE);
        } catch (JSONException e) {
            return null;
        }
    }
}
