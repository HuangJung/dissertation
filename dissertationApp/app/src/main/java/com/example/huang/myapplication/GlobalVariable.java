package com.example.huang.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.huang.myapplication.GlobalActivity.checkLocationPerm;
import static com.example.huang.myapplication.GlobalActivity.isLocationEnable;

public class GlobalVariable extends Application {
    // region proprities
    GlobalActivity globalActivity;

    private Activity mainActivity;
    private Context mainContext;
    private Activity summaryActivity;
    private Context summaryContext;

    private String planType;
    private ArrayList<TemplateModel> templateList;

    private Location currentLocation;
    private double currentLng;
    private double currentLat;

    private Location startLocation;
    private PlaceModel startPoint;

    private Location endLocation;
    private PlaceModel endPoint;

    private String startTime;
    private String endTime;
    private long duration; //minutes

    private String transport;
    private int radius;

    private String opennow;
    private ArrayList<String> place_types;
    private int place_number;

    private ArrayList<PlaceModel> places_list;
    private ArrayList<PlaceModel> target_places;
    private ArrayList<Integer> place_order;

    private ArrayList<TransportModel> target_transport;
    //endregion

    public GlobalVariable() {
        globalActivity = new GlobalActivity();
    }

    //region getter & setter
    public Activity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Context getMainContext() {
        return mainContext;
    }

    public void setMainContext(Context mainContext) {
        this.mainContext = mainContext;
    }

    public Activity getSummaryActivity() {
        return summaryActivity;
    }

    public void setSummaryActivity(Activity summaryActivity) {
        this.summaryActivity = summaryActivity;
    }

    public Context getSummaryContext() {
        return summaryContext;
    }

    public void setSummaryContext(Context summaryContext) {
        this.summaryContext = summaryContext;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public ArrayList<TemplateModel> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(ArrayList<TemplateModel> templateList) {
        this.templateList = templateList;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPlace_number() {
        return place_number;
    }

    public void setPlace_number(int place_number) {
        this.place_number = place_number;
    }

    public ArrayList<String> getPlace_types() {
        return place_types;
    }

    public void setPlace_types(ArrayList<String> place_types) {
        this.place_types = place_types;
    }

    public String getOpennow() {
        return opennow;
    }

    public void setOpennow(String opennow) {
        this.opennow = opennow;
    }

    public ArrayList<PlaceModel> getTarget_places() {
        return target_places;
    }

    public void setTarget_places(ArrayList<PlaceModel> target_places) {
        this.target_places = target_places;
    }

    public ArrayList<Integer> getPlace_order() {
        return place_order;
    }

    public void setPlace_order(ArrayList<Integer> place_order) {
        this.place_order = place_order;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public ArrayList<PlaceModel> getPlaces_list() {
        return places_list;
    }

    public void setPlaces_list(ArrayList<PlaceModel> places_list) {
        this.places_list = places_list;
    }

    public PlaceModel getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(PlaceModel startPoint) {
        this.startPoint = startPoint;
    }

    public PlaceModel getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PlaceModel endPoint) {
        this.endPoint = endPoint;
    }

    public ArrayList<TransportModel> getTarget_transport() {
        return target_transport;
    }

    public void setTarget_transport(ArrayList<TransportModel> target_transport) {
        this.target_transport = target_transport;
    }
    //endregion

    public boolean checkLocation(Activity a) {
        boolean b = true;
        if (!isLocationEnable(this)) {
            if (globalActivity.setLocationService(a)) {
                //定位已打开的处理
                Toast.makeText(this, "Please pull to refresh.", Toast.LENGTH_SHORT).show();
                b = true;
            } else {
                //定位依然没有打开的处理
                Toast.makeText(this, "Please open location to continue.", Toast.LENGTH_SHORT).show();
                b = false;
            }
        }
        return b;
    }

    public boolean checkLocationPermission(Context c, Activity a) {
        boolean b = true;
        if (!checkLocationPerm(c)) {
            if (globalActivity.setLocationPermit(a)) {
                //定位已打开的处理
                //Toast.makeText(c, "Please pull to refresh.", Toast.LENGTH_SHORT).show();
                b = true;
            } else {
                //定位依然没有打开的处理
                Toast.makeText(c, "Please pull to refresh.", Toast.LENGTH_SHORT).show();
                b = false;
            }
        }
        return b;
    }



}
