package com.artezio;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.artezio.model.Store;
import com.artezio.tasks.AddStoreTask;
import com.artezio.util.Utils;
import com.google.android.maps.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 7/19/12
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddStoreActivity extends MapActivity {

    public static final int ARM = 7;
    private MapView mapView;
    private EditText storeName;
    public static final float CIRCLE_RADIUS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_add);
        storeName = (EditText) findViewById(R.id.storeNameEditText);
        Location location = Utils.getLocation(this);
        GeoPoint p = new GeoPoint((int) (location.getLatitude() * 1E6),
                (int) (location.getLongitude() * 1E6));
        mapView = (MapView) findViewById(R.id.mapview);
        final MapController mc = mapView.getController();
        mc.animateTo(p);
        mc.setZoom(16);
        List<Overlay> overlays = mapView.getOverlays();
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
        overlays.add(myLocationOverlay);
        overlays.add(new Overlay() {
            @Override
            public void draw(Canvas canvas, MapView mapView, boolean b) {
                Point pt = new Point();

                mapView.getProjection().toPixels(mapView.getMapCenter(), pt);

                Paint innerCirclePaint = new Paint();
                innerCirclePaint.setARGB(255, 77, 144, 254);
                innerCirclePaint.setStrokeWidth(3f);
                innerCirclePaint.setAntiAlias(true);

                innerCirclePaint.setStyle(Paint.Style.STROKE);

                canvas.drawCircle((float) pt.x, (float) pt.y, CIRCLE_RADIUS, innerCirclePaint);
                canvas.drawLine(pt.x - (CIRCLE_RADIUS + ARM), (float) (pt.y), pt.x - CIRCLE_RADIUS, (float) (pt.y), innerCirclePaint);
                canvas.drawLine((pt.x + CIRCLE_RADIUS), (float) (pt.y), pt.x + (CIRCLE_RADIUS + ARM), (float) (pt.y), innerCirclePaint);
                canvas.drawLine((float) (pt.x), pt.y - (CIRCLE_RADIUS + ARM), (float) (pt.x), pt.y - CIRCLE_RADIUS, innerCirclePaint);
                canvas.drawLine((float) (pt.x), pt.y + CIRCLE_RADIUS, (float) (pt.x), pt.y + (CIRCLE_RADIUS + ARM), innerCirclePaint);
            }
        });
        mapView.invalidate();


        Button addStoreButton = (Button) findViewById(R.id.addStoreButton);
        addStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Store store = new Store();
                String name = storeName.getText().toString();
                Log.d(AddStoreActivity.class.getName(), name);
                store.setName(name);
                store.setPoint(mapView.getMapCenter());
                new AddStoreTask(AddStoreActivity.this).execute(store);
                finish();
            }
        });

    }

    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }
}
