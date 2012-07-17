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
    public static final String URL_ITEM = APP_URL + "/good/%s";
    public static final String URL_ADD_PRICE = APP_URL + "/add_price/";
    public static class JSON {
        public static final String NAME = "name";
        public static final String ITEM = "item";
        public static final String PRICES = "prices";
        public static final String PRICE = "price";
        public static final String LATITUDE = "lat";
        public static final String LONGITUDE = "log";
        public static final String DATE = "date";
        public static final String CODE = "code";
    }
}
