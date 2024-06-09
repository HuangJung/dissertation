package com.example.huang.myapplication;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.huang.myapplication.getJsonString.GET;

public class SearchActivity extends AppCompatActivity {
    PlaceAutocompleteFragment autocompleteFragmentStart;
    PlaceAutocompleteFragment autocompleteFragmentEnd;
    AutocompleteFilter typeFilter;
    CheckBox cb_startCurrent;
    CheckBox cb_endCurrent;
    LinearLayout startPointLayout;
    LinearLayout endPointLayout;
    EditText txtStartPoint;
    EditText txtEndPoint;

    EditText startTime;
    EditText endTime;
    SetTime setTime;

    RadioButton rd_bus;
    RadioButton rd_rail;
    RadioButton rd_driving;
    RadioButton rd_walking;
    RadioButton rd_bicycling;

    CheckBox cb_opennow;
    CheckBox cb_park;
    CheckBox cb_museum;
    CheckBox cb_zoo;
    CheckBox cb_landmark;
    CheckBox cb_shopping;
    CheckBox cb_show;
    CheckBox cb_restaurant;
    CheckBox cb_cafe;
    CheckBox cb_nightlife;
    CheckBox cb_casino;
    CheckBox cb_spa;

    Button btn_search;

    PlaceModel startPoint;
    PlaceModel endPoint;

    CustomizedPlan currentLocation;
    GlobalVariable global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        global = (GlobalVariable) getApplicationContext();

        //region points
        autocompleteFragmentStart = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_start);
        autocompleteFragmentEnd = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_end);
        //filter specific country
        typeFilter = new AutocompleteFilter.Builder().setCountry("UK").build();
        autocompleteFragmentStart.setBoundsBias(new LatLngBounds(
                new LatLng(53.326943, -2.730913),
                new LatLng(53.686294, -1.908658)));
        autocompleteFragmentEnd.setBoundsBias(new LatLngBounds(
                new LatLng(53.326943, -2.730913),
                new LatLng(53.686294, -1.908658)));
        cb_startCurrent = (CheckBox) findViewById(R.id.cb_startCurrent);
        cb_endCurrent = (CheckBox) findViewById(R.id.cb_endCurrent);
        startPointLayout = (LinearLayout) findViewById(R.id.startPointLayout);
        endPointLayout = (LinearLayout) findViewById(R.id.endPointLayout);
        txtStartPoint = (EditText) autocompleteFragmentStart.getView().findViewById(R.id.place_autocomplete_search_input);
        txtEndPoint = (EditText) autocompleteFragmentEnd.getView().findViewById(R.id.place_autocomplete_search_input);
        txtStartPoint.setTextSize(15.0f);
        txtEndPoint.setTextSize(15.0f);
        //endregion

        //region getCurrentLocation
        currentLocation = new CustomizedPlan(this, this);
        currentLocation.getCurrentLocation();
        //endregion

        //region times
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        startTime = (EditText) findViewById(R.id.startTime);
        //default as current time
        startTime.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
        endTime = (EditText) findViewById(R.id.endTime);

        setTime = new SetTime(startTime, this);
        setTime = new SetTime(endTime, this);
        //endregion

        //region transport
        rd_bus = (RadioButton) findViewById(R.id.rd_bus);
        rd_rail = (RadioButton) findViewById(R.id.rd_rail);
        rd_driving = (RadioButton) findViewById(R.id.rd_driving);
        rd_walking = (RadioButton) findViewById(R.id.rd_walking);
        rd_bicycling = (RadioButton) findViewById(R.id.rd_bicycling);
        //endregion

        //region opennow & place types
        cb_opennow = (CheckBox) findViewById(R.id.cb_opennow);
        cb_park = (CheckBox) findViewById(R.id.cb_park);
        cb_museum = (CheckBox) findViewById(R.id.cb_museum);
        cb_zoo = (CheckBox) findViewById(R.id.cb_zoo);
        cb_landmark = (CheckBox) findViewById(R.id.cb_landmark);
        cb_shopping = (CheckBox) findViewById(R.id.cb_shopping);
        cb_show = (CheckBox) findViewById(R.id.cb_show);
        cb_restaurant = (CheckBox) findViewById(R.id.cb_restaurant);
        cb_cafe = (CheckBox) findViewById(R.id.cb_cafe);
        cb_nightlife = (CheckBox) findViewById(R.id.cb_nightlife);
        cb_casino = (CheckBox) findViewById(R.id.cb_casino);
        cb_spa = (CheckBox) findViewById(R.id.cb_spa);
        //endregion

        btn_search = (Button) findViewById(R.id.search);

        useCurrentPoint();
        submit_search();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void useCurrentPoint() {
        cb_startCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cb_startCurrent.isChecked()) {
                    startPointLayout.setVisibility(View.GONE);
                    currentLocation.getCurrentLocation();
                } else {
                    startPointLayout.setVisibility(View.VISIBLE);
                    input_startPoint();
                }
            }
        });

        cb_endCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cb_endCurrent.isChecked()) {
                    endPointLayout.setVisibility(View.GONE);
                    currentLocation.getCurrentLocation();
                } else {
                    endPointLayout.setVisibility(View.VISIBLE);
                    input_endPoint();
                }
            }
        });
    }

    private void input_startPoint() {
        autocompleteFragmentStart.setFilter(typeFilter);

        autocompleteFragmentStart.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("START POINT", "Place: " + place.getName());
                startPoint = new PlaceModel();
                startPoint.setLat(place.getLatLng().latitude);
                startPoint.setLng(place.getLatLng().longitude);
                startPoint.setName(place.getName().toString());
                startPoint.setType("start");
                startPoint.setId(place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("START POINT ERROR", "An error occurred: " + status);
            }
        });

        autocompleteFragmentStart.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // example : way to access view from PlaceAutoCompleteFragment
                        ((EditText) autocompleteFragmentStart.getView().findViewById(R.id.place_autocomplete_search_input)).setText("");
                        startPoint = null;
                    }
                });
    }

    private void input_endPoint() {
        autocompleteFragmentEnd.setFilter(typeFilter);

        autocompleteFragmentEnd.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("END POINT", "Place: " + place.getName());
                endPoint = new PlaceModel();
                endPoint.setLat(place.getLatLng().latitude);
                endPoint.setLng(place.getLatLng().longitude);
                endPoint.setName(place.getName().toString());
                endPoint.setType("end");
                endPoint.setId(place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("END POINT ERROR", "An error occurred: " + status);
            }
        });

        autocompleteFragmentEnd.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // example : way to access view from PlaceAutoCompleteFragment
                        ((EditText) autocompleteFragmentEnd.getView().findViewById(R.id.place_autocomplete_search_input)).setText("");
                        endPoint = null;
                    }
                });
    }

    private void submit_search() {
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextListener();
                String result = checkValidation();
                if (result.length() > 0) {
                    Snackbar.make(view, result, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (global.getStartLocation().distanceTo(global.getEndLocation()) > 0) {
                    setTransport();
                    //get start to end traveling time
                    String url = "https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyCU7SSKeFad-vfrtpBwXRye4tz9YzxK5HE" +
                            "&origin=" + global.getStartPoint().getLat() + "," + global.getStartPoint().getLng() +
                            "&destination=" + global.getEndPoint().getLat() + "," + global.getEndPoint().getLng();
                    switch (global.getTransport()) {
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

                    new HttpAsync().execute(url);
                } else {
                    setTransport();
                    setPlaceTypes();
                    startNextPage();
                }
            }
        });
    }

    private void setTextListener() {
        txtStartPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtStartPoint.setError(null);
            }
        });

        txtEndPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtEndPoint.setError(null);
            }
        });

        startTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startTime.setError(null);
                endTime.setError(null);
            }
        });

        endTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startTime.setError(null);
                endTime.setError(null);
            }
        });
    }

    private String checkValidation() {
        String result = "";
        //region check currentPoint
        if (cb_startCurrent.isChecked() || cb_endCurrent.isChecked()) {
            if (global.getCurrentLocation() == null) {
                currentLocation.getCurrentLocation();
                result = "Current Location unavailable. Try Again!";
            } else {
                double lat = global.getCurrentLocation().getLatitude();
                double lng = global.getCurrentLocation().getLongitude();
                if (lat < 53.326943 || lat > 53.686294 || lng < -2.730913 || lng > -1.908658) {
                    result = "You are not in Manchester now.";
                }
            }
        }
        //endregion
        //region check startPoint
        if (cb_startCurrent.isChecked()) {
            global.setStartLocation(global.getCurrentLocation());
            //global.setStartLat(global.getCurrentLat());
            //global.setStartLng(global.getCurrentLng());
            PlaceModel model = new PlaceModel();
            model.setLat(global.getCurrentLat());
            model.setLng(global.getCurrentLng());
            model.setName("current");
            model.setType("start");
            global.setStartPoint(model);
        } else {
            if (startPoint == null) {
                txtStartPoint.setError("Please select Start Point.");
                result = "Please select Start Point.";
            } else if (startPoint.getLat() < 53.326943 || startPoint.getLat() > 53.686294
                    || startPoint.getLng() < -2.730913 || startPoint.getLng() > -1.908658) {
                result = "Start Point is out of Manchester.";
            } else {
                Location point = new Location("service Provider");
                point.setLatitude(startPoint.getLat());
                point.setLongitude(startPoint.getLng());
                global.setStartLocation(point);
                global.setStartPoint(startPoint);
                //global.setStartLat(startPoint.getLatitude());
                //global.setStartLng(startPoint.getLongitude());
            }
        }
        //endregion
        //region check endPoint
        if (cb_endCurrent.isChecked()) {
            global.setEndLocation(global.getCurrentLocation());
            //global.setEndLat(global.getCurrentLat());
            //global.setEndLng(global.getCurrentLng());
            PlaceModel model = new PlaceModel();
            model.setLat(global.getCurrentLat());
            model.setLng(global.getCurrentLng());
            model.setName("current");
            model.setType("start");
            global.setEndPoint(model);
        } else {
            if (endPoint == null) {
                txtEndPoint.setError("Please select End Point.");
                result = "Please select End Point.";
            } else if (endPoint.getLat() < 53.326943 || endPoint.getLat() > 53.686294
                    || endPoint.getLng() < -2.730913 || endPoint.getLng() > -1.908658) {
                result = "End Point is out of Manchester.";
            } else {
                //global.setEndLocation(endPoint);
                //global.setEndLat(endPoint.getLatitude());
                //global.setEndLng(endPoint.getLongitude());
                Location point = new Location("service Provider");
                point.setLatitude(endPoint.getLat());
                point.setLongitude(endPoint.getLng());
                global.setEndLocation(point);
                global.setEndPoint(endPoint);
            }
        }
        //endregion
        //region check time
        if (endTime.getText().toString().length() == 0) {
            endTime.setError("Please select End Time.");
            result = "Please select End Time.";
        } else {
            String StartTime = startTime.getText().toString();
            String EndTime = endTime.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            try {
                Date start = dateFormat.parse(StartTime);
                Date end = dateFormat.parse(EndTime);
                long duration = (end.getTime() - start.getTime()) / (1000 * 60);
                if (start.after(end)) { //if start >end
                    startTime.setError("End Time must be after Start Time.");
                    endTime.setError("End Time must be after Start Time.");
                    result = "End Time must be after Start Time.";
                } else if (duration < 90) { //duration <1.5 hr (90 min)
                    startTime.setError("Time Duration must be longer than 1.5 hour.");
                    endTime.setError("Time Duration must be longer than 1.5 hour.");
                    result = "Time Duration must be longer than 1.5 hour.";
                } else {
                    global.setStartTime(StartTime);
                    global.setEndTime(EndTime);
                    global.setDuration(duration);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //endregion
        return result;
    }

    private void setTransport() {
        if (rd_bus.isChecked()) {
            global.setTransport("bus");
            global.setRadius(10000);
        } else if (rd_rail.isChecked()) {
            global.setTransport("rail");
            global.setRadius(30000);
        } else if (rd_driving.isChecked()) {
            global.setTransport("driving");
            global.setRadius(30000);
        } else if (rd_walking.isChecked()) {
            global.setTransport("walking");
            global.setRadius(5000);
        } else if (rd_bicycling.isChecked()) {
            global.setTransport("bicycling");
            global.setRadius(5000);
        }

    }

    class HttpAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            checkTravelingTime(new JSONparser().traveling_time(result));
        }
    }

    private void checkTravelingTime(long traveling_time) {
        long duration = global.getDuration();

        if ((duration - traveling_time) < 60) {
            View view = btn_search.getRootView();
            Snackbar.make(view, "No trip plan available. Please change preferences!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            setPlaceTypes();
            startNextPage();
        }
    }

    private void setPlaceTypes() {
        int number = (int) (global.getDuration() / 90);
        global.setPlace_number(number);
        ArrayList<String> place_types = new ArrayList<String>();
        if (cb_opennow.isChecked())
            global.setOpennow("opennow");
        else
            global.setOpennow("");
        if (cb_park.isChecked())
            place_types.add("park");
        if (cb_museum.isChecked())
            place_types.add("museum");
        if (cb_zoo.isChecked())
            place_types.add("zoo");
        if (cb_landmark.isChecked())
            place_types.add("landmarks");
        if (cb_shopping.isChecked())
            place_types.add("shopping");
        if (cb_show.isChecked())
            place_types.add("shows");
        if (cb_restaurant.isChecked())
            place_types.add("restaurants");
        if (cb_cafe.isChecked())
            place_types.add("cafe");
        if (cb_nightlife.isChecked())
            place_types.add("nightlife");
        if (cb_casino.isChecked())
            place_types.add("casino");
        if (cb_spa.isChecked())
            place_types.add("spa");

        if (place_types.size() == 0)
            place_types.add("attractions");

        global.setPlace_types(place_types);
    }

    private void startNextPage() {
        global.setPlanType("create");
        Intent intent = new Intent();
        //intent.setClass(this, MainActivity.class);
        intent.setClass(this, SummaryActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.i_map).setVisible(false);
        menu.findItem(R.id.i_list).setVisible(false);
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
                intent.putExtra("Name", "aaa");
                intent.putExtra("lat", "0");
                intent.putExtra("lng", "0");
                intent.setClass(this, MapsActivity.class);
                startActivity(intent);
                return true;
            case R.id.i_list:
                Toast.makeText(this, "This is Plan page.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
