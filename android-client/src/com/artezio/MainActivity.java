package com.artezio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    private EditText text;
    private ImageButton buttonSearch;

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
                buttonSearch.setEnabled(text.getText().length() > 0);
                return false;
            }
        });
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
                Intent myIntent = new Intent(MainActivity.this, PricesListActivity.class);
                myIntent.putExtra("SCAN_RESULT", text.getText());
                MainActivity.this.startActivity(myIntent);
            }
        });
        buttonSearch.setEnabled(text.getText().length() > 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                text.setText(contents);
            }
//            else if (resultCode == RESULT_CANCELED) {
//            }
        }
        buttonSearch.setEnabled(text.getText().length() > 0);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        buttonSearch.setEnabled(text.getText().length() > 0);
    }
}
