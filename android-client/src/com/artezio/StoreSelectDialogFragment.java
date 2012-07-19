package com.artezio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

/**
 * User: araigorodskiy
 * Date: 7/18/12
 * Time: 3:21 PM
 */
public class StoreSelectDialogFragment extends Activity {
    int timeInMins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeInMins = 0;
        setContentView(R.layout.store_select);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerTime);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeInMins = (i + 1) * 5;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                timeInMins = 0;
            }
        });
        String[] stringArray = getResources().getStringArray(R.array.times_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        ListView list = (ListView) findViewById(R.id.selectStoreListView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent data = new Intent();
                data.putExtra(Constants.TIME,timeInMins);
                data.putExtra(Constants.STORE,""+i);
                setResult(RESULT_OK, data);
                finish();
            }
        });
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
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
