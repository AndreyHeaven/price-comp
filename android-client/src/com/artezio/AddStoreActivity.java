package com.artezio;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.artezio.model.Store;
import com.artezio.net.JsonHelper;
import com.artezio.tasks.AbstractProgressAsyncTask;
import com.artezio.tasks.DownloadStoresTask;
import com.artezio.util.Utils;
import com.google.android.maps.*;
import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.OverlayManager;
import de.android1.overlaymanager.lazyload.DummyListenerListener;
import de.android1.overlaymanager.lazyload.LazyLoadCallback;
import de.android1.overlaymanager.lazyload.LazyLoadException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * User: araigorodskiy
 * Date: 7/19/12
 * Time: 1:33 PM
 */
public class AddStoreActivity extends MapActivity {

    public static final int ARM = 7;
    private MapView mapView;
    private EditText storeName;
    public static final float CIRCLE_RADIUS = 10;
    OverlayManager overlayManager;
    private MyLocationOverlay myLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_add);
        storeName = (EditText) findViewById(R.id.storeNameEditText);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        final MapController mc = mapView.getController();
        mc.setZoom(16);
        GeoPoint location = Utils.getLocationPoint(this);
        if (location != null) {
            mc.animateTo(location);
        }
        overlayManager = new OverlayManager(getApplication(), mapView);
        List<Overlay> overlays = mapView.getOverlays();
        createOverlayWithLazyLoading();
        myLocationOverlay = new MyLocationOverlay(this, mapView);
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
                new AbstractProgressAsyncTask<Store>(AddStoreActivity.this, "Save store") {

                    @Override
                    protected String doInBackground(Store... stores) {
                        if (stores == null || stores.length < 1)
                            return null;
                        JsonHelper.put(Constants.URL_STORE, AddStoreActivity.this, stores[0], Constants.JSON.NAME, Constants.JSON.LATITUDE, Constants.JSON.LONGITUDE);
                        return null;

                    }
                }.execute(store);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLocationOverlay.disableMyLocation();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }
    public static Drawable boundCenter(Drawable d)
    {
        d.setBounds(d.getIntrinsicWidth() /- 2, d.getIntrinsicHeight() / -2,
                d.getIntrinsicWidth() / 2, d.getIntrinsicHeight() / 2);
        return d;
    }
    public void createOverlayWithLazyLoading() {
        //animation will be rendered to this ImageView
//        ImageView loaderanim = (ImageView) findViewById(R.id.loader);

        Drawable icon = boundCenter(getResources().getDrawable(R.drawable.shoppingcart));
        ManagedOverlay managedOverlay = overlayManager.createOverlay("lazyloadOverlay", icon);


        managedOverlay.setOnOverlayGestureListener(new DummyListenerListener(){
            @Override
            public boolean onSingleTap(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item) {
                if (item != null) {
                    Toast.makeText(AddStoreActivity.this, item.getTitle(), 1000).show();
                    return true;
                }
                return super.onSingleTap(e, overlay, point, item);
            }
        });
        // default built-in animation
//        managedOverlay.enableLazyLoadAnimation(loaderanim);
        // custom animation
        // managedOverlay.enableLazyLoadAnimation(loaderanim).setAnimationDrawable((AnimationDrawable)getResources().getDrawable(R.anim.myanim));
        managedOverlay.setLazyLoadCallback(new LazyLoadCallback() {
            @Override
            public List<ManagedOverlayItem> lazyload(GeoPoint topLeft, GeoPoint bottomRight, ManagedOverlay overlay) throws LazyLoadException {
                int rLat = (bottomRight.getLatitudeE6() - topLeft.getLatitudeE6()) / 2;
                int rLon = (bottomRight.getLongitudeE6() - topLeft.getLongitudeE6()) / 2;
                float[] floats = new float[1];
                GeoPoint center = new GeoPoint(topLeft.getLatitudeE6() + rLat,
                        topLeft.getLongitudeE6() + rLon);
                Location.distanceBetween(topLeft.getLatitudeE6()/1e6,topLeft.getLongitudeE6()/1e6,center.getLatitudeE6()/1e6,center.getLongitudeE6()/1e6,floats);

                List<ManagedOverlayItem> items = new LinkedList<ManagedOverlayItem>();
                try {
                    String stores = DownloadStoresTask.getStores(center, (int)floats[0]);
                    JSONArray arr = new JSONArray(stores);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject point = arr.getJSONObject(i);
                        Store store = new Store(point);
                        ManagedOverlayItem item = new ManagedOverlayItem(new GeoPoint(store.getLatitude(), store.getLongitude()), store.getName(), store.getName());
                        items.add(item);
                    }
                    // lets simulate a latency
//                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    throw new LazyLoadException(e.getMessage());
                }
                return items;
            }
        });

        // A LazyLoadListener is optional!
/*
        managedOverlay.setLazyLoadListener(new LazyLoadListener() {
            long debug_lazyload_runtime;

            @Override
            public void onBegin(ManagedOverlay overlay) {
                debug_lazyload_runtime = System.currentTimeMillis();
            }

            @Override
            public void onSuccess(ManagedOverlay overlay) {
                Toast.makeText(getApplicationContext(), "LazyLoadRuntime: " + (System.currentTimeMillis() - debug_lazyload_runtime) + " ms", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(LazyLoadException exception, ManagedOverlay overlay) {

            }
        });
*/
        overlayManager.populate();
        managedOverlay.invokeLazyLoad(1000);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }
}
