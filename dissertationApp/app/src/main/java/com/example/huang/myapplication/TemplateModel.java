package com.example.huang.myapplication;

import java.util.ArrayList;

public class TemplateModel {
    String planType;
    ArrayList<PlaceModel> places;
    ArrayList<TransportModel> transports;

    public TemplateModel() {
    }

    public TemplateModel(String planType, ArrayList<PlaceModel> places, ArrayList<TransportModel> transports) {
        this.planType = planType;
        this.places = places;
        this.transports = transports;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public ArrayList<PlaceModel> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<PlaceModel> places) {
        this.places = places;
    }

    public ArrayList<TransportModel> getTransports() {
        return transports;
    }

    public void setTransports(ArrayList<TransportModel> transports) {
        this.transports = transports;
    }
}
