package com.example.huang.myapplication;

import java.util.ArrayList;

/**
 * Created by huang on 2018/3/21.
 */

public class PlaceModel {

    String name;
    double lat;
    double lng;
    String id;
    String type;
    String open_now;
    String rating;

    public PlaceModel() {
    }

    public PlaceModel(String name, double lat, double lng, String id, String type, String open_now, String rating) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.id = id;
        this.type = type;
        this.rating = rating;
        this.open_now = open_now;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpen_now() {
        return open_now;
    }

    public void setOpen_now(String open_now) {
        this.open_now = open_now;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}