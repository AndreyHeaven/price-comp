package com.artezio;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: araigorodskiy
 * Date: 09.07.12
 * Time: 15:23
 */
public class PricesListActivity extends ListActivity{

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

        setListAdapter(adapter);

        try {
            JSONObject item = new JSONObject(intent.getStringExtra(Constants.JSON.ITEM));
            setTitle(item.getString(Constants.JSON.NAME));
            JSONArray jsonArray = item.getJSONArray(Constants.JSON.PRICES);
            for(int i=0; i<jsonArray.length();i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    adapter.add(new BasicNameValuePair(jsonObject.getString("price"),jsonObject.getString("date")));
                } catch (JSONException e) {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prices,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        try {
            JSONObject item = new JSONObject(getIntent().getStringExtra("item"));
            JSONArray jsonArray = item.getJSONArray("prices");
            JSONObject jsonObject = jsonArray.getJSONObject(position);

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                Toast.makeText(this, "Tapped add", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_details:
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Constants.GOODSMATRIX_MOBILE, getIntent().getStringExtra(Constants.CODE))));
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
