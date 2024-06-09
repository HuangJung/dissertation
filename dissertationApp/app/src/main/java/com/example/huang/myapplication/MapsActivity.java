package com.example.huang.myapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener {

    GlobalVariable global;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        global = (GlobalVariable) getApplicationContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //region override functions
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onClick(View view) {

    }
    //endregion

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //取得上一頁傳過來的 Key為Name的 Value
        String Name = getIntent().getStringExtra("Name");
        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);

        mMap = googleMap;

        /** check location authority and location is open */
        if (global.checkLocationPermission(this, this) && global.checkLocation(this)) {
            mMap.setMyLocationEnabled(true);
        }

        LatLng res = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(res).draggable(true).title(Name));


        //if (global.getTarget_places() != null && global.getTarget_places().size() > 0) {
        PolylineOptions line = new PolylineOptions();
        Drawable circleDrawable = getResources().getDrawable(R.drawable.map_circle_pin);
        BitmapDescriptor markerIcon;

        LatLng start = null;
        LatLng end = null;
        if (global.getPlanType().matches("create")) {
            markerIcon = getMarkerIconFromDrawable(circleDrawable, "S");
            start = new LatLng(global.getStartPoint().getLat(), global.getStartPoint().getLng());
            mMap.addMarker(new MarkerOptions()
                    .position(start)
                    .draggable(true)
                    .icon(markerIcon)
                    .anchor(0.5f, 1)
                    .title("Start")
            );
            line.add(start);

            markerIcon = getMarkerIconFromDrawable(circleDrawable, "E");
            end = new LatLng(global.getEndPoint().getLat(), global.getEndPoint().getLng());
            mMap.addMarker(new MarkerOptions()
                    .position(end)
                    .draggable(true)
                    .icon(markerIcon)
                    .anchor(0.5f, 1)
                    .title("End")
            );
        }

        for (int i = 0; i < global.getTarget_places().size(); i++) {
            markerIcon = getMarkerIconFromDrawable(circleDrawable, String.valueOf(i + 1));

            PlaceModel place = global.getTarget_places().get(i);
            LatLng point = new LatLng(place.getLat(), place.getLng());
            mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .draggable(true)
                    .icon(markerIcon)
                    //.icon(BitmapDescriptorFactory.fromBitmap(bmp))
                    .anchor(0.5f, 1)
                    .title(String.valueOf(i + 1) + place.getName())
            );
            line.add(point);

            if (i == 0 && global.getPlanType().matches("template")) {
                start = new LatLng(place.getLat(), place.getLng());
            }
        }
        if (global.getPlanType().matches("create")) {
            line.add(end);
        }
        line.color(Color.parseColor("#2F90BD"));
        mMap.addPolyline(line);
        //}


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
        mMap.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
        mMap.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能

        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));     // 放大地圖到 15倍大

        refresh();
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable, String mText) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);

        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        /* SET FONT COLOR (e.g. WHITE -> rgb(255,255,255)) */
        paint.setColor(Color.BLACK);
        /* SET FONT SIZE (e.g. 15) */
        paint.setTextSize((int) (15 * scale));
        /* SET SHADOW WIDTH, POSITION AND COLOR (e.g. BLACK) */
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        canvas.drawText(mText, bitmap.getWidth() / 2, (int) (bitmap.getHeight() / 1.5), paint);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void refresh() {
        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (global.getPlanType().matches("template")) {
                    /** random 0 [include] - global.getTemplateList().size() [exclude] **/
                    Random r = new Random();
                    int num = r.nextInt(global.getTemplateList().size());
                    TemplateModel templateModel = global.getTemplateList().get(num);
                    global.setTarget_places(templateModel.getPlaces());
                    global.setTarget_transport(templateModel.getTransports());
                }
                startActivity(new Intent(MapsActivity.this, SummaryActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.i_map).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = new Intent();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.i_home:
                intent.setClass(this, categoryActivity.class);
                startActivity(intent);
                return true;
            case R.id.i_map:
                Toast.makeText(this, "This is Map page.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.i_list:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
