package com.example.huang.myapplication;

import java.util.ArrayList;

public class TourManager {
    // Holds our cities
    private static ArrayList destinationCities = new ArrayList<PlaceModel>();

    private static PlaceModel startCity = new PlaceModel();
    private static PlaceModel endCity = new PlaceModel();
    public static void addStartCity(PlaceModel city) {
        startCity = city;
    }
    public static PlaceModel getStartCity() {
        return startCity;
    }
    public static void addEndCity(PlaceModel city) {
        endCity = city;
    }
    public static PlaceModel getEndCity() {
        return endCity;
    }


    // Adds a destination city
    public static void addCity(PlaceModel city) {
        destinationCities.add(city);
    }

    // Get a city
    public static PlaceModel getCity(int index){
        return (PlaceModel)destinationCities.get(index);
    }

    // Get the number of destination cities
    public static int numberOfCities(){
        return destinationCities.size();
    }

    // Clear  city array
    public static void clearCity() {
        destinationCities.clear();
    }
}
