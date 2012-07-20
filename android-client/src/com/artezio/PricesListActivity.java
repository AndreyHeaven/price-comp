package com.artezio;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.artezio.model.Item;
import com.artezio.model.Price;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * User: araigorodskiy
 * Date: 09.07.12
 * Time: 15:23
 */
public class PricesListActivity extends ListActivity {
    private JsonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String code = intent.getStringExtra(Constants.CODE);
        try {
            Item item = new Item(intent.getStringExtra(Constants.JSON.ITEM));
            if (item.getName() == null)
                setTitle(item.getCode());
            else
                setTitle(item.getName());
            adapter = new JsonAdapter(this, item.getJSONArray(Constants.JSON.PRICES), R.layout.price_list_item,new String[]{Constants.JSON.PRICE,Constants.JSON.STORE,Constants.JSON.DATE}, new int[]{R.id.priceTextView,R.id.storeTextView,R.id.dateTextView});
        } catch (JSONException e) {
            //
        }


        setListAdapter(adapter);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prices, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
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

    }

    private Item getItem() {
        try {
            return new Item(getIntent().getStringExtra("item"));
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
/*
                item.setActionView(new ProgressBar(getBaseContext()));
                getWindow().getDecorView().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                item.setActionView(new ProgressBar(getBaseContext()));
                            }
                        }, 1000);
*/
                break;

            case R.id.menu_add:
                addNewPrice();
                break;

            case R.id.menu_details:
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Constants.GOODSMATRIX_MOBILE, getIntent().getStringExtra(Constants.CODE))));
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewPrice() {
        String code = getIntent().getStringExtra(Constants.CODE);
        Intent intent = new Intent(this, AddPriceActivity.class);
        startActivity(intent);
    }
}
