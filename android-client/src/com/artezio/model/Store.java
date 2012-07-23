package com.artezio.model;

import com.artezio.Constants;
import com.google.android.maps.GeoPoint;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 7/18/12
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Store extends JSONObject {
    public Store() {
    }

    public Store(String json) throws JSONException {
        super(json);
    }

    public Store(JSONObject copyFrom) throws JSONException {
        super(copyFrom, new String[]{});
    }

    public String getKey(){
        try {
            return getString(Constants.JSON.ID);
        } catch (JSONException e) {
            return null;
        }
    }
    public String getName(){
        try {
            return getString(Constants.JSON.NAME);
        } catch (JSONException e) {
            return null;
        }
    }

    public void setName(String name){
        try {
            put(Constants.JSON.NAME,name);
        } catch (JSONException e) {
            //
        }
    }

    public void setPoint(GeoPoint point) {
        try {
            put(Constants.JSON.LONGITUDE, point.getLongitudeE6());
            put(Constants.JSON.LATITUDE, point.getLatitudeE6());
        } catch (JSONException e) {
            //
        }
    }
}
