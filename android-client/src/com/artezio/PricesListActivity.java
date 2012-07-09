package com.artezio;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 09.07.12
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
public class PricesListActivity extends ListActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("====");
        String code = getIntent().getStringExtra(MainActivity.RESULT);
        new DownloadItemDetailsTask(this).execute(code);
        ArrayAdapter<BasicNameValuePair> adapter = new ArrayAdapter<BasicNameValuePair>(this, android.R.layout.simple_list_item_2) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TwoLineListItem row;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        new DownloadPrices(adapter).execute(code);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
    }


}
