package com.artezio.model;

import com.artezio.Constants;
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
            return getString(Constants.JSON.KEY);
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
}
