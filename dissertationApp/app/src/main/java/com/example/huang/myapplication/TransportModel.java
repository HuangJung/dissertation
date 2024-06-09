package com.example.huang.myapplication;

import java.util.ArrayList;

/**
 * Created by huang on 2018/3/21.
 */

public class TransportModel {

    String travel_mode;
    long duration;
    ArrayList<Steps> steps;


    public TransportModel() {
    }

    public TransportModel(String travel_mode, long duration, ArrayList<Steps> steps) {
        this.travel_mode = travel_mode;
        this.duration = duration;
        this.steps = steps;
    }

    public String getTravel_mode() {
        return travel_mode;
    }

    public void setTravel_mode(String travel_mode) {
        this.travel_mode = travel_mode;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Steps> steps) {
        this.steps = steps;
    }
}

class Steps {

    String travel_mode;
    long duration;
    String html_instructions;
    TransitDetails transitDetails;



    public Steps() {
    }

    public Steps(String travel_mode, long duration,String html_instructions, TransitDetails transitDetails) {
        this.travel_mode = travel_mode;
        this.duration = duration;
        this.html_instructions = html_instructions;
        this.transitDetails = transitDetails;
    }

    public String getTravel_mode() {
        return travel_mode;
    }

    public void setTravel_mode(String travel_mode) {
        this.travel_mode = travel_mode;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

    public TransitDetails getTransitDetails() {
        return transitDetails;
    }

    public void setTransitDetails(TransitDetails transitDetails) {
        this.transitDetails = transitDetails;
    }
}

class TransitDetails {

    String departure_stop;
    String arrival_stop;
    String vehicle_name;
    String vehicle_type;


    public TransitDetails() {
    }

    public TransitDetails(String departure_stop, String arrival_stop, String vehicle_name, String vehicle_type) {
        this.departure_stop = departure_stop;
        this.arrival_stop = arrival_stop;
        this.vehicle_name = vehicle_name;
        this.vehicle_type = vehicle_type;
    }

    public String getDeparture_stop() {
        return departure_stop;
    }

    public void setDeparture_stop(String departure_stop) {
        this.departure_stop = departure_stop;
    }

    public String getArrival_stop() {
        return arrival_stop;
    }

    public void setArrival_stop(String arrival_stop) {
        this.arrival_stop = arrival_stop;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }
}