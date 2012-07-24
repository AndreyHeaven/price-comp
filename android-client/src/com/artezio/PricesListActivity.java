package com.artezio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import com.artezio.model.Item;
import com.artezio.tasks.DownloadItemDetailsTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private JsonAdapter adapter;
    private ExpandableListView elvMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prices);
        Intent intent = getIntent();
        String code = intent.getStringExtra(Constants.CODE);
        elvMain = (ExpandableListView) findViewById(R.id.expandableListView);
//        adapter = new JsonAdapter(this, null, R.layout.price_list_item,new String[]{Constants.JSON.PRICE,Constants.JSON.STORE,Constants.JSON.DATE}, new int[]{R.id.priceTextView,R.id.storeTextView,R.id.dateTextView});
//        setListAdapter(adapter);
        updatePrices(code);
    }

    private void updatePrices(String code) {
        new DownloadItemDetailsTask(this, getResources().getString(R.string.label, code)).execute(code);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prices, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        try {
            Price price = new Price((JSONObject)adapter.getItem(position));
            if (price.getLatitude() == null || price.getLongitude() == null)
                return;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:" + price.getLatitude() + "," + price.getLongitude()+"?z=18"));
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }*/

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
            List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
            List<List<java.util.Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
                stringStringHashMap.put("name", jsonObject1.getString(Constants.JSON.NAME));
                groupData.add(stringStringHashMap);
                childData.add(getPrices(jsonObject1.getInt(Constants.JSON.ID), pricesJsonArray));
            }

            SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                    this,
                    groupData,
                    R.layout.price_store_item,
                    new String[]{"name"},
                    new int[]{R.id.storeName},
                    childData,
                    R.layout.price_list_item,
                    new String[]{"price", "store", "date"},
                    new int[]{R.id.priceTextView, R.id.storeTextView, R.id.dateTextView});
            elvMain.setAdapter(adapter);

        } catch (JSONException e) {
            //
        }

    }

    private List<Map<String, String>> getPrices(int id, JSONArray jsonArray) throws JSONException {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int store_id = jsonObject.getInt("store_id");
            if (store_id == id) {
                HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
                stringStringHashMap.put("price", jsonObject.getString(Constants.JSON.PRICE));
                stringStringHashMap.put("store", "" + store_id);
                stringStringHashMap.put("date", jsonObject.getString(Constants.JSON.DATE));
                list.add(stringStringHashMap);
            }
        }
        return list;
    }
}
