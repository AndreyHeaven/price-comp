package com.artezio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.artezio.model.Store;
import com.artezio.net.JsonHelper;
import com.artezio.net.OverpassHelper;
import com.artezio.tasks.AbstractProgressAsyncTask;
import com.artezio.util.StoreManager;
import com.artezio.util.Utils;
import com.google.android.maps.*;
import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.OverlayManager;
import de.android1.overlaymanager.lazyload.DummyListenerListener;
import de.android1.overlaymanager.lazyload.LazyLoadCallback;
import de.android1.overlaymanager.lazyload.LazyLoadException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: araigorodskiy
 * Date: 7/19/12
 * Time: 1:33 PM
 */
public class AddStoreActivity extends MapActivity {

    public static final int ARM = 7;
    public static final String OVERLAY = "lazyloadOverlay";
    private MapView mapView;
    private EditText storeName;
    public static final float CIRCLE_RADIUS = 10;
    OverlayManager overlayManager;
    private MyLocationOverlay myLocationOverlay;

    Map<String, Integer> icons = new HashMap<String, Integer>() {{
        put("alcohol", R.drawable.shopping_alcohol_n_16);
        put("book", R.drawable.shopping_book_n_16);
        put("butcher", R.drawable.shopping_butcher_n_16);
        put("convenience", R.drawable.shopping_convenience_n_16);
        put("supermarket", R.drawable.shopping_supermarket_n_16);
        put("bakery", R.drawable.shopping_bakery_n_16);
        put("bicycle", R.drawable.shopping_bicycle_n_16);
        put("car", R.drawable.shopping_car_n_16);
        put("car_repair", R.drawable.shopping_car_repair_n_16);
        put("clothes", R.drawable.shopping_clothes_n_16);
        put("confectionery", R.drawable.shopping_confectionery_n_16);
        put("diy", R.drawable.shopping_diy_n_16);
        put("fish", R.drawable.shopping_fish_n_16);
        put("garden_centre", R.drawable.shopping_garden_centre_n_16);
        put("gift", R.drawable.shopping_gift_n_16);
        put("greengrocer", R.drawable.shopping_greengrocer_n_16);
        put("hairdresser", R.drawable.shopping_hairdresser_n_16);
        put("hifi", R.drawable.shopping_hifi_n_16);
        put("jewelry", R.drawable.shopping_jewelry_n_16);
        put("laundrette", R.drawable.shopping_laundrette_n_16);
        put("motorcycle", R.drawable.shopping_motorcycle_n_16);
        put("music", R.drawable.shopping_music_n_16);
    }};


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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_change_map_view:
                mapView.setSatellite(!mapView.isSatellite());
                break;
        }
        return super.onOptionsItemSelected(item);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void addStoreAndFinish(View view) {
        addStore(null);
        finish();
    }

    public void addStore(final View view) {
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

                return JsonHelper.put(Constants.URL_STORE, AddStoreActivity.this, stores[0], Constants.JSON.NAME, Constants.JSON.LATITUDE, Constants.JSON.LONGITUDE);

            }

            @Override
            protected void onPostExecute(String jsonObject) {
                super.onPostExecute(jsonObject);
                if (view != null) {
                    overlayManager.getOverlay(OVERLAY).invokeLazyLoad(100);
                }
                String s = JsonHelper.checkErrors(AddStoreActivity.this, jsonObject);
                if (s != null) {
                    new AlertDialog.Builder(AddStoreActivity.this).setMessage(s)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                } else {
                    storeName.getText().clear();
                }
            }
        }.execute(store);

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

    public static Drawable boundCenter(Drawable d) {
        d.setBounds(d.getIntrinsicWidth() / -2, d.getIntrinsicHeight() / -2,
                d.getIntrinsicWidth() / 2, d.getIntrinsicHeight() / 2);
        return d;
    }

    public void createOverlayWithLazyLoading() {
        //animation will be rendered to this ImageView
//        ImageView loaderanim = (ImageView) findViewById(R.id.loader);

        Drawable icon = boundCenter(getResources().getDrawable(R.drawable.shoppingcart));
        ManagedOverlay managedOverlay = overlayManager.createOverlay(OVERLAY, icon);


        managedOverlay.setOnOverlayGestureListener(new DummyListenerListener() {
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
                double sLat = topLeft.getLatitudeE6() / 1e6;
                double wLon = topLeft.getLongitudeE6() / 1e6;
                double nLat = bottomRight.getLatitudeE6() / 1e6;
                double eLon = bottomRight.getLongitudeE6() / 1e6;
                int rLat = (bottomRight.getLatitudeE6() - topLeft.getLatitudeE6()) / 2;
                int rLon = (bottomRight.getLongitudeE6() - topLeft.getLongitudeE6()) / 2;
                float[] floats = new float[1];
                GeoPoint center = new GeoPoint(topLeft.getLatitudeE6() + rLat,
                        topLeft.getLongitudeE6() + rLon);
                Location.distanceBetween(sLat, wLon, center.getLatitudeE6() / 1e6, center.getLongitudeE6() / 1e6, floats);

                List<ManagedOverlayItem> items = new LinkedList<ManagedOverlayItem>();
                try {
                    List<Store> shops = new StoreManager(getBaseContext()).getInArea(nLat, eLon, sLat, wLon);
//                    List<Store> shops = OverpassHelper.getShops(AddStoreActivity.this, sLat, wLon, nLat, eLon, 100);
                    for (Store store : shops) {
                        ManagedOverlayItem item = new ManagedOverlayItem(new GeoPoint(store.getLatitude(), store.getLongitude()), store.getName(), store.getName());
                        String type = store.getType();
                        if (type != null && icons.get(type) != null) {
                            item.setMarker(boundCenter(getResources().getDrawable(icons.get(type))));
                        }
                        items.add(item);
                    }
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
