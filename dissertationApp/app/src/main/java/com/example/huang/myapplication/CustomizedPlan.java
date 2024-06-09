package com.example.huang.myapplication;

import android.app.Activity;
import android.content.Context;
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

public class CustomizedPlan extends PlaceListActivity {
    GlobalVariable global;
    // Location請求物件
    private LocationRequest locationRequest;
    // 記錄目前最新的位置
    private Location currentLocation;

    private double longitude;
    private double latitude;
    private static final int MY_LOCATION_REQUEST_CODE = 111;
    private FusedLocationProviderClient mFusedLocationClient;

    private String plan = "";

    private String json;
    private String travel_mode;

    Context context;
    Activity activity;

    CustomizedPlan() {
    }

    CustomizedPlan(Context context, Activity activity) {
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

    //region get current location
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

                if (currentLocation != null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }
        }
    };
    //endregion

    public void queryResult() {
        try {
            String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyCQSk-KxMLZJGkWO9iH72SU7hVUlpAGTrU&language=en-GB" +
                    "&location=" + global.getStartPoint().getLat() + "," + global.getStartPoint().getLng() + "&radius=" + global.getRadius();
            if (global.getPlace_types() != null && global.getPlace_types().size() > 0) {
                url += "&query=";
                for (int i = 0; i < global.getPlace_types().size(); i++) {
                    if (i == 0)
                        url += global.getPlace_types().get(i);
                    else {
                        url += URLEncoder.encode("|" + global.getPlace_types().get(i), "UTF-8");
                    }
                }
                url += "+in+Manchester";
            }
            if (global.getOpennow() != null && global.getOpennow().length() > 0)
                url += "&opennow";

            new HttpAsync().execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HttpAsync extends AsyncTask<String, Void, String> {
        ArrayList<PlaceModel> placeModels;
        PlaceListActivity mActivity;
        SummaryActivity summaryActivity;

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            for (String url : urls) {
                result = GET(url);
                placeModels.addAll(new JSONparser().informList(result));
            }
            global.setPlaces_list(placeModels);
            selectTargetPlaces(placeModels);
            optimizeRoute();
            checkTravelingTime();
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
            placeModels = new ArrayList<>();
            mActivity = new PlaceListActivity();
            summaryActivity = new SummaryActivity();

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
            if (plan.matches("noPlan")) {
                //mActivity.noPlanMessage(activity, context);
                summaryActivity.noPlanMessage(activity, context);
            } else if (plan.matches("hasPlan")) {
                //mActivity.showPlan(global.getTarget_places(), context, activity);
                summaryActivity.showPlan(global.getTarget_places(), context, activity);
            }
            // HIDE THE SPINNER AFTER LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.GONE);

        }
    }

    /**
     * 每一次循环取完数据后，把list size - k -1 的元素 放到本次取到的index位置，
     * 下次循环的随机数最大值为list size - k
     */
    public void selectTargetPlaces(ArrayList<PlaceModel> placeModels) {
        int num = global.getPlace_number();
        if (placeModels.size() <= num) {
            global.setTarget_places(placeModels);
        } else {
            // 复制一份，以免对原数据造成影响
            ArrayList<PlaceModel> _placeModels = new ArrayList<>(placeModels);
            ArrayList<PlaceModel> targetList = new ArrayList<>(num);
            Random random = new Random();
            for (int k = 0; k < num; k++) {
                int i = random.nextInt(_placeModels.size() - k);
                PlaceModel placeModel = _placeModels.get(i);
                targetList.add(placeModel);
                // 取完后，把list size - k 的元素 放到本次取到的index位置
                _placeModels.set(i, _placeModels.get(_placeModels.size() - k - 1));
            }
            global.setTarget_places(targetList);
        }
    }

    public void re_selectTargetPlaces(ArrayList<PlaceModel> placeModels) {
        Random random = new Random();
        int i = random.nextInt(placeModels.size());
        PlaceModel placeModel = placeModels.get(i);
        // if already exist -> re-select
        for (PlaceModel target : global.getTarget_places()) {
            if (placeModel.getName().matches(target.getName())) {
                re_selectTargetPlaces(placeModels);
            }
        }
        global.getTarget_places().add(placeModel);
        placeModels.remove(i);
    }

    public void optimizeRoute() {
        String result = "";
        String url = "";
        try {
            url = "https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyCI_Hhrjjfvy9IJxBcQZbhEaWy1W7urLMA&" +
                    "origin=" + global.getStartPoint().getLat() + "," + global.getStartPoint().getLng() +
                    "&destination=" + global.getEndPoint().getLat() + "," + global.getEndPoint().getLng() +
                    "&waypoints=optimize:true";
            for (PlaceModel place : global.getTarget_places()) {
                url += URLEncoder.encode("|" + place.getLat() + "," + place.getLng(), "UTF-8");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        result = GET(url);
        global.setPlace_order(new JSONparser().waypoint_order(result));
        //change target places list order
        ArrayList<PlaceModel> targetPlaces = new ArrayList<>();
        for (int i : global.getPlace_order()) {
            targetPlaces.add(global.getTarget_places().get(i));
        }
        global.setTarget_places(targetPlaces);

        //optimize by algorithm
        //optimizeRouteByAlgorithm();
    }

    public void optimizeRouteByAlgorithm() {
        TourManager.addStartCity(global.getStartPoint());
        TourManager.clearCity();
        for (PlaceModel place : global.getTarget_places()) {
            TourManager.addCity(place);
        }
        TourManager.addEndCity(global.getEndPoint());

        // Initialize population
        Population pop = new Population(2, true);
        //System.out.println("Initial distance: " + pop.getFittest().getDistance());

        // Evolve population for 100 generations
        pop = GA.evolvePopulation(pop);
        for (int i = 0; i < 1; i++) {
           pop = GA.evolvePopulation(pop);
        }

        // Print final results
        System.out.println("Finished");
        //System.out.println("Final distance: " + pop.getFittest().getDistance());
        System.out.println("Solution:");
        System.out.println(pop.getFittest());

    }

    /**
     * private long distancematrix_url(String transport, int num, boolean end, ArrayList<PlaceModel> target_places) {
     * long time = 0;
     * String url = "https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyBb1tzHQO7Xc1f5btNK9AKw5fqLm1kdq_Q";
     * switch (transport) {
     * case "bus":
     * url += "&mode=transit&transit_mode=bus";
     * break;
     * case "rail":
     * url += "&mode=transit&transit_mode=rail";
     * break;
     * case "driving":
     * url += "&mode=driving";
     * break;
     * case "walking":
     * url += "&mode=walking";
     * break;
     * case "bicycling":
     * url += "&mode=bicycling";
     * break;
     * }
     * if (!end) {
     * PlaceModel place = global.getTarget_places().get(num);
     * if (num == 0) {
     * url += "&origins=" + global.getStartLat() + "," + global.getStartLng() +
     * "&destinations=" + place.getLat() + "," + place.getLng();
     * } else {
     * url += "&origins=" + global.getTarget_places().get(num - 1).getLat() + "," + global.getTarget_places().get(num - 1).getLng() +
     * "&destinations=" + place.getLat() + "," + place.getLng();
     * }
     * } else {
     * url += "&origins=" + target_places.get(num).getLat() + "," + target_places.get(num).getLng() +
     * "&destinations=" + global.getEndLat() + "," + global.getEndLng();
     * }
     * String json = GET(url);
     * time = new OdataToInfor().traveling_time(json);
     * if (time == -1) {
     * distancematrix_url("walking", num, end, target_places);
     * }
     * return time;
     * }
     */
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
        //if (time == -1) {
        //    directions_url("walking", num, end, target_places);
        //}
        travel_mode = transport;
        return time;
    }

    private void checkTravelingTime() {
        long duration = global.getDuration();
        long traveling_time = 0;
        long time = 0;
        ArrayList<PlaceModel> target_places = new ArrayList<>();
        ArrayList<TransportModel> target_transport = new ArrayList<>();

        for (int i = 0; i < global.getTarget_places().size(); i++) {

            PlaceModel place = global.getTarget_places().get(i);

            time = directions_url(global.getTransport(), i, false, global.getTarget_places());
            if (time == -1) {
                directions_url("walking", i, false, global.getTarget_places());
            }

            traveling_time += (time + 60); //total traveling time + staying time (60mins)
            if ((duration - traveling_time) < 0) {
                traveling_time -= (time + 60);
                break;
            } else {
                target_places.add(place);
                target_transport.add(new JSONparser().transport_detail(json, travel_mode, time));
            }
        }
        /** last point to end point:
         * if time is over : remove the last point -> recalculate time (until no target place or pass plan)
         * else : set target places -> return true
         */
        while (target_places.size() > 0) {
            int last = target_places.size() - 1;
            //time = distancematrix_url(global.getTransport(), last, true, target_places);
            time = directions_url(global.getTransport(), last, true, target_places);
            if (time == -1) {
                directions_url("walking", last, true, target_places);
            }
            traveling_time += time;
            if ((duration - traveling_time) < 0) {
                traveling_time -= time;
                target_places.remove(last);
                target_transport.remove(target_transport.size()-1);
            } else if ((duration - traveling_time) > 60 && target_places.size() < 10
                    && global.getPlaces_list().size() > 0 && global.getPlaces_list().size() > target_places.size()) {
                break;
            } else {
                global.setTarget_places(target_places);
                target_transport.add(new JSONparser().transport_detail(json, travel_mode, time));
                global.setTarget_transport(target_transport);
                plan = "hasPlan";
                return;
            }
        }
        /** re-select places :
         * if (place list is not empty) -> random 1 place add to target places
         * else: return false
         */
        global.setTarget_places(target_places);
        if (global.getPlaces_list().size() > 0) {
            re_selectTargetPlaces(global.getPlaces_list());
            optimizeRoute();
            checkTravelingTime();
        } else {
            plan = "noPlan";
            return;
        }
    }


}
