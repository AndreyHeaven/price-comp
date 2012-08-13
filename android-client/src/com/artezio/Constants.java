package com.artezio;

/**
 * User: araigorodskiy
 * Date: 10.07.12
 * Time: 17:26
 */
public class Constants {

    public static final String CODE = "SCAN_RESULT";
    public static final String GOODSMATRIX_MOBILE = "http://goodsmatrix.ru/mobile/%s.html";
    public static final String APP_URL = "http://price-comp.appspot.com";
//    public static final String APP_URL = "http://10.99.44.50:8080";
    public static final String URL_ITEM = "/good/%s/%s/%s/%s";
    public static final String URL_ADD_PRICE = "/price/";
    public static final String URL_STORE = "/store/";
    public static final String URL_GET_STORES = URL_STORE + "%s/%s/%d";
    public static final String STORE = "store";
    public static final String TIME = "time";
    public static final String START_TIME = "now";
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final int RADIUS_DELTA = 500;



    public static class JSON {
        public static final String NAME = "name";
        public static final String ITEM = "item";
        public static final String PRICES = "prices";
        public static final String PRICE = "price";
        public static final String LATITUDE = "lat";
        public static final String LONGITUDE = "lon";
        public static final String DATE = "date";
        public static final String CODE = "code";
        public static final String STORE = "store";
        public static final String ID = "id";
        public static final String ERROR = "error";
        public static final String OSM_ID = "osm_id";
        public static final String TYPE = "type";
    }

    public static class Prefs {
        public static final String RADIUS = "radius";
        public static final String HISTORY = "history";
    }
}
