package com.artezio;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.artezio.model.Price;
import com.artezio.tasks.AddPriceTask;
import com.artezio.util.Utils;
import org.json.JSONException;

/**
 * User: araigorodskiy
 * Date: 12.07.12
 * Time: 12:36
 */
public class AddPriceDialogFragment extends Activity {

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
//                Location l = Utils.getLocation(AddPriceDialogFragment.this);
//                p.setLatitude(l.getLatitude());
//                p.setLongitude(l.getLongitude());
                p.setStoreKey("");
                new AddPriceTask(AddPriceDialogFragment.this).execute(p);
                finish();
            }
        });
        Button buttonCancel = (Button) findViewById(R.id.addPriceDialogCancelButton);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        Button buttonChangeStore = (Button) findViewById(R.id.buttonChangeStore);
        buttonChangeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AddPriceDialogFragment.this, StoreSelectDialogFragment.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String stringExtra = data.getStringExtra(Constants.STORE);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
