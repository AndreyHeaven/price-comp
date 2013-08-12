package com.artezio;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import com.artezio.tasks.DownloadStoresTask;
import com.artezio.util.Utils;
import com.google.android.maps.GeoPoint;
import org.json.JSONObject;

/**
 * User: araigorodskiy
 * Date: 7/18/12
 * Time: 3:21 PM
 */
public class StoreSelectActivity extends Activity {
    int time;
    private JsonAdapter storesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        time = 0;
        setContentView(R.layout.store_select);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerTime);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                time = (i + 1) * 5 * 60 * 1000;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                time = 0;
            }
        });
        String[] stringArray = getResources().getStringArray(R.array.times_array);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        ListView list = (ListView) findViewById(R.id.selectStoreListView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent data = new Intent();
                data.putExtra(Constants.TIME, time);
                JSONObject o = (JSONObject) storesAdapter.getItem(i);
                data.putExtra(Constants.STORE, o.toString());
                setResult(RESULT_OK, data);
                finish();
            }
        });
        storesAdapter = new JsonAdapter(this, R.layout.store_list_view, new String[]{Constants.JSON.TYPE,Constants.JSON.NAME}, new int[]{R.id.imageView,R.id.nameTextView});
        list.setAdapter(storesAdapter);

        updateList();
    }

    private void updateList() {
        GeoPoint location = Utils.getLocationPoint(this);
        new DownloadStoresTask(storesAdapter, this).execute(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stores, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_store:
                Intent intent = new Intent(this, AddStoreActivity.class);
                startActivityForResult(intent, 123);
        }
        return super.onOptionsItemSelected(item);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123)
            updateList();
        super.onActivityResult(requestCode, resultCode, data);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
