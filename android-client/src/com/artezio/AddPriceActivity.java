package com.artezio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.artezio.model.Price;
import com.artezio.model.Store;
import com.artezio.tasks.AddPriceTask;
import org.json.JSONException;

/**
 * User: araigorodskiy
 * Date: 12.07.12
 * Time: 12:36
 */
public class AddPriceActivity extends Activity {

    private Button buttonChangeStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addprice);
        final EditText price = (EditText) findViewById(R.id.addPriceDialogPrice);
        Button button = (Button) findViewById(R.id.addPriceDialogAddButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                Price p = new Price();
//                try {
//                    p.put(Constants.JSON.ITEM, getArguments().getString("code"));
//                } catch (JSONException e) {
//
//                }
                p.setPrice(Double.parseDouble(price.getText().toString()));
//                Location l = Utils.getLocation(AddPriceActivity.this);
//                p.setLatitude(l.getLatitude());
//                p.setLongitude(l.getLongitude());
                p.setStoreKey("");
                new AddPriceTask(AddPriceActivity.this).execute(p);
                finish();
            }
        });
        Button buttonCancel = (Button) findViewById(R.id.addPriceDialogCancelButton);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        buttonChangeStore = (Button) findViewById(R.id.buttonChangeStore);
        buttonChangeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPriceActivity.this, StoreSelectActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String stringExtra = data.getStringExtra(Constants.STORE);

        try {
            Store store = new Store(stringExtra);
            buttonChangeStore.setText(store.getName());
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
