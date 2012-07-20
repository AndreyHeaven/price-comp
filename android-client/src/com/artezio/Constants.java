package com.artezio;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 10.07.12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class Constants {

    public static final String CODE = "SCAN_RESULT";
    public static final String GOODSMATRIX_MOBILE = "http://goodsmatrix.ru/mobile/%s.html";
    public static final String APP_URL = "http://price-comp.appspot.com";
    public static final String URL_ITEM = "/good/%s/%s/%s/%s";
    public static final String URL_ADD_PRICE = APP_URL + "/price/";
    public static final String URL_STORE = "/stores/";
    public static final String URL_GET_STORES = URL_STORE + "%s/%s/%d";
    public static final String STORE = "store";
    public static final String TIME = "time";


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
        public static final String KEY = "key";
    }
}
