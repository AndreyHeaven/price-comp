package com.artezio;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.artezio.util.Utils;
import com.google.android.maps.*;

public class MainActivity extends MapActivity {


    private EditText text;
    private ImageButton buttonSearch;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getLocation(this);
        setContentView(R.layout.main);
        text = ((EditText) findViewById(R.id.barcodeText));
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                updateScanButton();
                return false;
            }
        });

        final ImageButton button = (ImageButton) findViewById(R.id.buttonscan);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                scan(v);
            }
        });
        buttonSearch = (ImageButton) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = text.getText().toString();
                search(code);
            }
        });
        updateScanButton();
    }

    public void scan(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        String appPackage = Utils.findTargetAppPackage(this, intent);
        if (appPackage == null) {
            Utils.showDownloadDialog(this);
        } else {
//                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_store:
                Intent intent = new Intent(this, AddStoreActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }

    private void search(String code) {
        Intent myIntent = new Intent(this, PricesListActivity.class);
        myIntent.putExtra(Constants.CODE, code);
        startActivity(myIntent);
//        new DownloadItemDetailsTask(this, myIntent).execute(code);
    }

    public GeoPoint getSelectedLocation() {
        Location location = Utils.getLocation(this);
        GeoPoint p = null;
        if (location != null)
        p = new GeoPoint((int) (location.getLatitude() * 1E6),
                (int) (location.getLongitude() * 1E6));
        return p;
//        return mapView.getMapCenter();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                text.setText(contents);
//                search(contents);
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
