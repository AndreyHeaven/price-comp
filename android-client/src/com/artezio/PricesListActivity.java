package com.artezio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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
public class PricesListActivity extends Activity{
    private ArrayAdapter<BasicNameValuePair> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prices);
        TextView textView = (TextView) findViewById(R.id.titleLabel);
        textView.setText(getIntent().getStringExtra(MainActivity.RESULT));
        final List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        for (int i = 0; i < 10; i++) {
            list.add(new BasicNameValuePair("label "+i, "text "+i));
        }
        adapter = new ArrayAdapter<BasicNameValuePair>(this,android.R.layout.simple_list_item_2,list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                TwoLineListItem row;
                if(convertView == null){
                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = (TwoLineListItem)inflater.inflate(android.R.layout.simple_list_item_2, null);
                }else{
                    row = (TwoLineListItem)convertView;
                }
                BasicNameValuePair data = list.get(position);
                row.getText1().setText(data.getName());
                row.getText2().setText(data.getValue());

                return row;
            }
        };
        ((ListView)findViewById(R.id.listView)).setAdapter(adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


}
