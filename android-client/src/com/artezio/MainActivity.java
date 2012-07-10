package com.artezio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends Activity implements LocationListener {


    private EditText text;
    private ImageButton buttonSearch;
    private LocationManager locationManager;
    private Location location;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = ((EditText) findViewById(R.id.barcodeText));
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                updateScanButton();
                return false;
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500L, 250.0f, this);
        final ImageButton button = (ImageButton) findViewById(R.id.buttonscan);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });
        buttonSearch = (ImageButton) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = text.getText().toString();
                search(code);
//                MainActivity.this.startActivity(myIntent);
            }
        });
        updateScanButton();
    }

    private void search(String code) {
        Intent myIntent = new Intent(this, PricesListActivity.class);
        myIntent.putExtra(Constants.CODE, code);
        new DownloadItemDetailsTask(this,myIntent).execute(code);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderEnabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderDisabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                text.setText(contents);
                search(contents);
            }
//            else if (resultCode == RESULT_CANCELED) {
//            }
        }
        updateScanButton();
    }

    private void updateScanButton() {
        buttonSearch.setEnabled(!TextUtils.isEmpty(text.getText()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        updateScanButton();
    }
}
