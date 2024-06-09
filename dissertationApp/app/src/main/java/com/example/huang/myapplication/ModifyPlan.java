package com.example.huang.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.huang.myapplication.getJsonString.GET;

public class ModifyPlan extends PlaceListActivity {
    GlobalVariable global;
    // Location請求物件
    private LocationRequest locationRequest;
    // 記錄目前最新的位置
    private Location currentLocation;

    private double longitude;
    private double latitude;
    private static final int MY_LOCATION_REQUEST_CODE = 111;
    private FusedLocationProviderClient mFusedLocationClient;

    private String json;
    private String travel_mode;

    Context context;
    Activity activity;

    ModifyPlan() {
    }

    ModifyPlan(Context context, Activity activity) {
        global = (GlobalVariable) activity.getApplicationContext();
        // 建立Location請求物件
        createLocationRequest();
        this.context = context;
        this.activity = activity;
    }

    // 建立Location請求物件
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setInterval(10000);
        // 設定讀取位置資訊最快的間隔時間為五秒（5000ms）
        locationRequest.setFastestInterval(5000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void getCurrentLocation() {
        currentLocation = null;
        //global.setCurrentLocation(null);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity);

        /** check location authority and location is open */
        if (global.checkLocationPermission(context, activity) && global.checkLocation(activity)) {
            // get current location -> mLocationCallback to get result
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        }

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);

                currentLocation = location;
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                global.setCurrentLocation(currentLocation);
                global.setCurrentLng(longitude);
                global.setCurrentLat(latitude);
                //new HttpAsync().execute("https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyAt8JYd8R5t3c7Z5Ee657u2MkOMUyhUlJg&language=en&query=Manchester" +
                //        "&location=" + latitude + "," + longitude + "&radius=30000&type=museum&opennow");

                if (currentLocation != null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }
        }
    };


    class HttpAsync extends AsyncTask<String, Void, String> {
        SummaryActivity summaryActivity = new SummaryActivity();

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            for (String url : urls) {
                result = GET(url);
                global.setPlace_order(new JSONparser().waypoint_order(result));
                //change target places list order
                ArrayList<PlaceModel> targetPlaces = new ArrayList<>();
                for (int i : global.getPlace_order()) {
                    targetPlaces.add(global.getTarget_places().get(i));
                }
                global.setTarget_places(targetPlaces);
            }
            result = setTargetTransport();

            //return GET(urls[0]);
            return result;
        }

        LinearLayout linlaHeaderProgress = (LinearLayout) activity.findViewById(R.id.linlaHeaderProgress);
        private ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.pbHeaderProgress);
        private TextView txtProgress = (TextView) activity.findViewById(R.id.txtProgress);
        private int pStatus = 0;
        private Handler handler = new Handler();

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (pStatus <= 100) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(pStatus);
                                //txtProgress.setText(pStatus + " %");
                                txtProgress.setText("loading...");
                            }
                        });
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pStatus++;
                    }
                }
            }).start();
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if (result.matches("noPlan")) {
                summaryActivity.noPlanMessage(activity, context);
            } else if (result.matches("hasPlan")) {
                //summaryActivity.showPlan(global.getTarget_places(), global.getSummaryContext(), global.getSummaryActivity());
                Intent intent = new Intent(context, SummaryActivity.class);
                intent.putExtra("modify", "true");
                context.startActivity(intent);
            }
            // HIDE THE SPINNER AFTER LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }


    public void optimizeRoute() {
        String url = "";
        try {
            url = "https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyCI_Hhrjjfvy9IJxBcQZbhEaWy1W7urLMA&" +
                    "origin=" + global.getStartPoint().getLat() + "," + global.getStartPoint().getLng() +
                    "&destination=" + global.getEndPoint().getLat() + "," + global.getEndPoint().getLng() +
                    "&waypoints=optimize:true";
            for (PlaceModel place : global.getTarget_places()) {
                url += URLEncoder.encode("|" + place.getLat() + "," + place.getLng(), "UTF-8");
            }

            new HttpAsync().execute(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private long directions_url(String transport, int num, boolean end, ArrayList<PlaceModel> target_places) {
        long time = 0;
        String url = "https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyBkW7P6dkzTYpCvmCnFPUOVHMz7qvjWUA4";
        switch (transport) {
            case "bus":
                url += "&mode=transit&transit_mode=bus";
                break;
            case "rail":
                url += "&mode=transit&transit_mode=rail";
                break;
            case "driving":
                url += "&mode=driving";
                break;
            case "walking":
                url += "&mode=walking";
                break;
            case "bicycling":
                url += "&mode=bicycling";
                break;
        }
        if (!end) {
            PlaceModel place = global.getTarget_places().get(num);
            if (num == 0) {
                url += "&origin=" + global.getStartPoint().getLat() + "," + global.getStartPoint().getLng() +
                        "&destination=" + place.getLat() + "," + place.getLng();
            } else {
                url += "&origin=" + global.getTarget_places().get(num - 1).getLat() + "," + global.getTarget_places().get(num - 1).getLng() +
                        "&destination=" + place.getLat() + "," + place.getLng();
            }
        } else {
            url += "&origin=" + target_places.get(num).getLat() + "," + target_places.get(num).getLng() +
                    "&destination=" + global.getEndPoint().getLat() + "," + global.getEndPoint().getLng();
        }
        json = GET(url);
        time = new JSONparser().traveling_time(json);
        travel_mode = transport;
        return time;
    }

    private String setTargetTransport() {
        String result;
        long time = 0;
        ArrayList<PlaceModel> target_places = new ArrayList<>();
        ArrayList<TransportModel> target_transport = new ArrayList<>();

        for (int i = 0; i < global.getTarget_places().size(); i++) {
            PlaceModel place = global.getTarget_places().get(i);
            time = directions_url(global.getTransport(), i, false, global.getTarget_places());
            if (time == -1) {
                directions_url("walking", i, false, global.getTarget_places());
            }
            target_places.add(place);
            target_transport.add(new JSONparser().transport_detail(json, travel_mode, time));
        }

        if (target_places.size() > 0) {

            int last = target_places.size() - 1;
            time = directions_url(global.getTransport(), last, true, target_places);
            if (time == -1) {
                directions_url("walking", last, true, target_places);
            }
            target_transport.add(new JSONparser().transport_detail(json, travel_mode, time));

            global.setTarget_places(target_places);
            global.setTarget_transport(target_transport);
            result = "hasPlan";
        } else {
            result = "noPlan";
        }
        return result;
    }
}
