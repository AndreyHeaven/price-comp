package com.artezio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.artezio.util.Utils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends MapActivity {


    private EditText text;
//    private ImageButton buttonSearch;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getLocation(this);
        setContentView(R.layout.main);
        text = ((EditText) findViewById(R.id.barcodeText));
//        final ImageButton button = (ImageButton) findViewById(R.id.buttonscan);
//        buttonSearch = (ImageButton) findViewById(R.id.buttonSearch);
    }

    public void search(View view) {
        String code = text.getText().toString();
        if (code == null || code.trim().length() == 0) {
            new AlertDialog.Builder(this).setMessage(R.string.barCodeRequred)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create().show();
        } else
            search(code.trim());
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
            case R.id.menu_history:
                final Set<String> history = getHistory();
                final String[] items = history.toArray(new String[history.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.history);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        text.setText(items[item]);
                    }
                }).create().show();
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
        saveToHistory(code);
        Intent myIntent = new Intent(this, PricesListActivity.class);
        myIntent.putExtra(Constants.CODE, code);
        startActivity(myIntent);
//        new DownloadItemDetailsTask(this, myIntent).execute(code);
    }

    private void saveToHistory(String code) {
        Set<String> stringSet = getHistory();
        stringSet.add(code);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putStringSet(Constants.Prefs.HISTORY, stringSet);
        edit.commit();
    }

    private Set<String> getHistory() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getStringSet(Constants.Prefs.HISTORY, new HashSet<String>());
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
    }
}
