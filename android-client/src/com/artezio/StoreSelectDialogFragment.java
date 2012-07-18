package com.artezio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * User: araigorodskiy
 * Date: 7/18/12
 * Time: 3:21 PM
 */
public class StoreSelectDialogFragment extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_select);
        ListView list = (ListView) findViewById(R.id.selectStoreListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent data = new Intent();
                data.putExtra(Constants.STORE,""+i);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}
