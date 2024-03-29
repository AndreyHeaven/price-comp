package com.artezio.net;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;
import com.artezio.Constants;
import com.artezio.R;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * User: araigorodskiy
 * Date: 7/19/12
 * Time: 2:12 PM
 */
public class JsonHelper {
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
    private static Map<Integer, Integer> errors = new HashMap<Integer, Integer>() {{
        put(500, R.string.error_incorrect_value);
        put(301, R.string.error_store_already_exist);
    }};

    public static String put(String url, Context context, JSONObject o, String... keys) {
        StringBuilder builder = new StringBuilder();
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(params, "utf-8");
        DefaultHttpClient client = getDefaultHttpClient(params);
        HttpPut httpPut = new HttpPut(Constants.APP_URL + url);
        httpPut.setHeader("charset", "utf-8");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        String email = null;
        if (accounts.length > 0)
            email = accounts[0].name;
        for (String key : keys) {
            try {
                pairs.add(new BasicNameValuePair(key, o.getString(key)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (email != null)
            pairs.add(new BasicNameValuePair("user", email));

        try {
            httpPut.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
            HttpResponse response = client.execute(httpPut);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if ((statusCode / 100) == 2) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(JsonHelper.class.toString(), "Failed to upload json " + o.toString());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static DefaultHttpClient getDefaultHttpClient(HttpParams params) {
        DefaultHttpClient client = new DefaultHttpClient(params);
        client.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                // Add header to accept gzip content
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                // Inflate any responses compressed with gzip
                final HttpEntity entity = response.getEntity();
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });
        return client;
    }

    public static String post(String url, Context context, Map<String, String> o, boolean addEmail) {
        StringBuilder builder = new StringBuilder();
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(params, "utf-8");
        HttpClient client = getDefaultHttpClient(params);
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("charset", "utf-8");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (addEmail) {
            String email = null;
            Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
            if (accounts.length > 0)
                email = accounts[0].name;
            if (email != null)
                pairs.add(new BasicNameValuePair("user", email));
        }
        for (Map.Entry<String, String> entry : o.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        try {
            httpPut.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
            HttpResponse response = client.execute(httpPut);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if ((statusCode / 100) == 2) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(JsonHelper.class.toString(), "Failed to upload json " + o.toString());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String get(String url) {
        StringBuilder builder = new StringBuilder();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.APP_URL + url);
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if ((statusCode / 100) == 2) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(JsonHelper.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    public static Map<String, String> toStringMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)).toString());
        }
        return map;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    public static String checkErrors(Context context, String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
            JSONObject object = jsonObject.getJSONObject(Constants.JSON.ERROR);
            int anInt = object.getInt(Constants.JSON.ID);
            return errors.get(anInt) != null ? context.getResources().getString(errors.get(anInt)) : object.getString("msg");
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Simple {@link HttpEntityWrapper} that inflates the wrapped
     * {@link HttpEntity} by passing it through {@link GZIPInputStream}.
     */
    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}
