package com.artezio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import com.artezio.model.Price;
import com.artezio.model.Store;
import com.artezio.net.JsonHelper;
import com.artezio.tasks.AbstractProgressAsyncTask;
import org.json.JSONException;

import java.util.Date;

/**
 * User: araigorodskiy
 * Date: 12.07.12
 * Time: 12:36
 */
public class AddPriceActivity extends Activity {

    private Button buttonChangeStore;
    private int time;
    private String stringExtra;
    private long startTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        updateStore(settings);
        setContentView(R.layout.addprice);
        final EditText price = (EditText) findViewById(R.id.addPriceDialogPrice);
        Button button = (Button) findViewById(R.id.addPriceDialogAddButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Price p = new Price();
                try {
                    p.put(Constants.JSON.CODE, getIntent().getStringExtra(Constants.JSON.CODE));
                } catch (JSONException e) {

                }

                p.setPrice(Double.parseDouble(price.getText().toString()));
                updateButtonLabel();
                Store actualStore = getActualStore();
                if (actualStore != null)
                    p.setStoreKey(actualStore.getKey());
                new AbstractProgressAsyncTask<Price>(AddPriceActivity.this, "Save price"){

                    @Override
                    protected String doInBackground(Price... prices) {
                        if (prices == null || prices.length < 1)
                            return null;
                        return JsonHelper.put(Constants.URL_ADD_PRICE, AddPriceActivity.this, prices[0], Constants.JSON.CODE, Constants.JSON.PRICE, Constants.JSON.STORE);
                    }
                }.execute(p);
                finish();
            }
        });
        buttonChangeStore = (Button) findViewById(R.id.buttonChangeStore);
//        buttonChangeStore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                changeStore(view);
//            }
//        });
        updateButtonLabel();
    }

    public void changeStore(View view) {
        Intent intent = new Intent(this, StoreSelectActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        saveStore(settings);
    }

    private void saveStore(SharedPreferences settings) {
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(Constants.STORE, stringExtra);
        edit.putInt(Constants.TIME, time);
        edit.putLong(Constants.START_TIME,startTime);
        edit.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            stringExtra = data.getStringExtra(Constants.STORE);
            time = data.getIntExtra(Constants.TIME, 0);
            startTime = data.getLongExtra(Constants.START_TIME, new Date().getTime());
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            saveStore(settings);
        }
        updateButtonLabel();
    }

    private void updateButtonLabel() {
        Store store = getActualStore();
        if (store != null) {
            buttonChangeStore.setText(store.getName());
        } else {
            buttonChangeStore.setText(getResources().getString(R.string.addPriceSelectStore));
        }
    }

    private void updateStore(SharedPreferences data) {
        if (data != null) {
            stringExtra = data.getString(Constants.STORE,"{}");
            time = data.getInt(Constants.TIME, 0);
            startTime = data.getLong(Constants.START_TIME, new Date().getTime());
        }
    }

    protected boolean isStoreExpired() {
        long now = new Date().getTime();
        return now - startTime > time;
    }

    protected Store getActualStore() {
        if (isStoreExpired())
            return null;
        else if (stringExtra != null) {
            try {
                return new Store(stringExtra);
            } catch (JSONException e) {
                return null;
            }
        } else
            return null;
    }
}
