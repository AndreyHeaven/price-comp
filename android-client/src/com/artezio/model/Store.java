package com.artezio.model;

import com.artezio.Constants;
import com.google.android.maps.GeoPoint;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

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

    public Store(Map copyFrom) {
        super(copyFrom);
    }

    public Store(String json) throws JSONException {
        super(json);
    }

    public Store(JSONObject copyFrom) throws JSONException {
        super(copyFrom, new String[]{Constants.JSON.NAME,Constants.JSON.LATITUDE,Constants.JSON.LONGITUDE,Constants.JSON.ID,Constants.JSON.TYPE,Constants.JSON.OSM_ID});
    }

    public int getKey(){
        try {
            return getInt(Constants.JSON.ID);
        } catch (JSONException e) {
            return 0;
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

    public int getLatitude() {
        try {
            return getInt(Constants.JSON.LATITUDE);
        } catch (JSONException e) {
            return 0;
        }
    }

    public int getLongitude() {
        try {
            return getInt(Constants.JSON.LONGITUDE);
        } catch (JSONException e) {
            return 0;
        }

    }

    public void setOsmId(int id) {
        try {
            put(Constants.JSON.OSM_ID,id);
        } catch (JSONException e) {
            //
        }
    }

    public void setType(String shop) {
        try {
            put(Constants.JSON.TYPE,shop);
        } catch (JSONException e) {
            //
        }
    }

    public String getType() {
        try {
            return getString(Constants.JSON.TYPE);
        } catch (JSONException e) {
            return null;
        }
    }
}
