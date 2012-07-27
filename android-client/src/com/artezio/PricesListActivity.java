package com.artezio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import com.artezio.model.Item;
import com.artezio.model.Price;
import com.artezio.model.Store;
import com.artezio.net.JsonHelper;
import com.artezio.tasks.DownloadItemDetailsTask;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: araigorodskiy
 * Date: 09.07.12
 * Time: 15:23
 */
public class PricesListActivity extends Activity {
    //    private JsonAdapter adapter;
    private ExpandableListView elvMain;
    private List<Map<String, String>> groupData;
    private List<List<Map<String, String>>> childData;
    private TextView radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prices);
        radius = (TextView) findViewById(R.id.radiusTextView);
        elvMain = (ExpandableListView) findViewById(R.id.expandableListView);
        elvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                if (groupData != null && childData != null) {
                    Map<String, String> stringStringMap = groupData.get(i);
                    Store store = new Store(stringStringMap);
                    double lat = store.getLatitude() / 1e6;
                    double lon = store.getLongitude() / 1e6;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + store.getName() + ")"));
                    startActivity(intent);
                    return true;
                } else
                    return false;
            }
        });
        changeRadius(0);
    }

    private void updatePrices(String code) {
        new DownloadItemDetailsTask(this, getResources().getString(R.string.label, code)).execute(code);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prices, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        String code = getIntent().getStringExtra(Constants.CODE);
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                updatePrices(code);
                break;

            case R.id.menu_add:
                addNewPrice();
                break;

            case R.id.menu_details:
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Constants.GOODSMATRIX_MOBILE, code)));
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewPrice() {
        String code = getIntent().getStringExtra(Constants.CODE);
        Intent intent = new Intent(this, AddPriceActivity.class);
        intent.putExtra(Constants.JSON.CODE, code);
        startActivity(intent);
    }

    public void setItem(String jsonObject) {
        try {
            Item item = new Item(jsonObject);
            if (item.getName() == null)
                setTitle(item.getCode());
            else
                setTitle(item.getName());
//            adapter.setData(jsonArray);
            JSONArray jsonArray = item.getJSONArray("stores");
            JSONArray pricesJsonArray = item.getJSONArray(Constants.JSON.PRICES);
            groupData = new ArrayList<Map<String, String>>();
            childData = new ArrayList<List<Map<String, String>>>();
            for (int i = 0; i < jsonArray.length(); i++) {
                Store store = new Store(jsonArray.getJSONObject(i));
                Map<String, String> stringStringMap = JsonHelper.toStringMap(store);
                List<Map<String, String>> prices = getPrices(store.getKey(), pricesJsonArray);
                stringStringMap.put(Constants.JSON.PRICE, getMinPrice(prices));
                groupData.add(stringStringMap);
                childData.add(prices);
            }

            SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                    this,
                    groupData,
//                    android.R.layout.simple_list_item_1,
                    R.layout.price_store_item,
//                    new String[]{Constants.JSON.NAME},
                    new String[]{Constants.JSON.NAME, Constants.JSON.PRICE},
//                    new int[]{android.R.id.text1},
                    new int[]{R.id.storeName, R.id.minPrice},
                    childData,
//                    android.R.layout.simple_list_item_1,
                    R.layout.price_list_item,
//                    new String[]{Constants.JSON.PRICE},
                    new String[]{Constants.JSON.PRICE, Constants.JSON.DATE},
//                    new int[]{android.R.id.text1}
                    new int[]{R.id.priceTextView, R.id.dateTextView}
            );
            elvMain.setAdapter(adapter);

        } catch (JSONException e) {
            //
        }

    }

    private String getMinPrice(List<Map<String, String>> prices) {
        Double min = null;
        for (Map<String, String> price : prices) {
            try {
                Double price1 = new Double(price.get("price"));
                if (min == null || min > price1)
                    min = price1;
            } catch (NumberFormatException e) {
                //
            }
        }
        return min != null ? min.toString() : null;
    }

    private List<Map<String, String>> getPrices(int id, JSONArray jsonArray) throws JSONException {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Price price = new Price(jsonArray.getJSONObject(i));
            int store_id = price.getStoreId();
            if (store_id == id) {
                Map<String, String> stringStringHashMap = JsonHelper.toStringMap(price);
                list.add(stringStringHashMap);
            }
        }
        return list;
    }

    public void plusRadius(View v) {
        changeRadius(Constants.RADIUS_DELTA);
    }

    public void minusRadius(View v) {
        changeRadius(-Constants.RADIUS_DELTA);
    }

    private void changeRadius(int i) {
        int anInt = getRadius();
        anInt = anInt + i;
        anInt = Math.max(anInt, Constants.RADIUS_DELTA);
        anInt = Math.min(anInt, Constants.RADIUS_DELTA * 10);
        radius.setText(getResources().getString(R.string.radiusLabel,anInt/1000f));
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(Constants.Prefs.RADIUS, anInt);
        edit.commit();
        updatePrices(getIntent().getStringExtra(Constants.CODE));
    }

    public int getRadius() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getInt(Constants.Prefs.RADIUS, Constants.RADIUS_DELTA);

    }
}
