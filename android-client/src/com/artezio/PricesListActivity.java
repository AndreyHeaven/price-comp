package com.artezio;

import android.app.DialogFragment;
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

import java.util.List;

/**
 * User: araigorodskiy
 * Date: 09.07.12
 * Time: 15:23
 */
public class PricesListActivity extends ListActivity {

    private List<Price> prices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String code = intent.getStringExtra(Constants.CODE);
        ArrayAdapter<BasicNameValuePair> adapter = new ArrayAdapter<BasicNameValuePair>(this, android.R.layout.simple_list_item_2) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TwoLineListItem row;
                if (convertView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
                } else {
                    row = (TwoLineListItem) convertView;
                }
                BasicNameValuePair data = getItem(position);
                row.getText1().setText(data.getName());
                row.getText2().setText(data.getValue());

                return row;
            }

        };

        try {
            Item item = new Item(intent.getStringExtra(Constants.JSON.ITEM));
            if (item.getName() == null)
                setTitle(item.getCode());
            else
                setTitle(item.getName());
            prices = item.getPrices();
            if (prices != null)
                for (Price price : prices) {
                    adapter.add(new BasicNameValuePair(price.getPrice().toString(), price.getDate()));
                }
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
        if (prices == null)
            return;
        Price price = prices.get(position);
        if (price == null || price.getLatitude() == null || price.getLongitude() == null)
            return;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:" + price.getLatitude() + "," + price.getLongitude()+"?z=18"));
        startActivity(intent);

    }

    private Item getItem() {
        try {
            return new Item(getIntent().getStringExtra("item"));
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
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
//        Item item = getItem();
//        if (item == null)
//            return;
        String code = getIntent().getStringExtra(Constants.CODE);
        Intent intent = new Intent(this, AddPriceDialogFragment.class);
        startActivity(intent);
//        DialogFragment newFragment = AddPriceDialogFragment.newInstance(code);
//        newFragment.show(getFragmentManager(), "dialog");
    }

    /*private void addNewPrice() {
        Item item = getItem();
        if (item == null)
            return;
        Context mContext = getApplicationContext();
        final Dialog dialog = new Dialog(mContext);

        dialog.setContentView(R.layout.addprice);
        dialog.setTitle("Custom Dialog");

        TextView text = (TextView) dialog.findViewById(R.id.addPriceNameLabel);
        text.setText(item.getName());

        final EditText price = (EditText) dialog.findViewById(R.id.addPriceDialogPrice);
        Button addButton = (Button) dialog.findViewById(R.id.addPriceDialogAddButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Price p = new Price();
                try {
                    p.put(Constants.JSON.ITEM,getIntent().getStringExtra(Constants.CODE));
                } catch (JSONException e) {
                    //
                }
                p.setPrice(Double.parseDouble(price.getText().toString()));
                Location l = Utils.getLocation(PricesListActivity.this);
                p.setLatitude(l.getLatitude());
                p.setLongitude(l.getLongitude());
                new AddPriceTask().execute(p);
            }
        });
        Button cancelButton = (Button) dialog.findViewById(R.id.addPriceDialogAddButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }*/
}
