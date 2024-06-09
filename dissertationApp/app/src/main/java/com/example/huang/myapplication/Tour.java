package com.example.huang.myapplication;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import static com.example.huang.myapplication.getJsonString.GET;

public class Tour {
    // Holds our tour of cities
    private ArrayList tour = new ArrayList<PlaceModel>();
    // Cache
    private double fitness = 0;
    private double travelTime = 0;

    private static PlaceModel startCity = new PlaceModel();
    private static PlaceModel endCity = new PlaceModel();

    // Constructs a blank tour
    public Tour() {
        for (int i = 0; i < TourManager.numberOfCities(); i++) {
            tour.add(null);
        }
    }

    public Tour(ArrayList tour) {
        this.tour = tour;
    }

    // Creates a random individual
    public void generateIndividual() {
        startCity = TourManager.getStartCity();
        endCity = TourManager.getEndCity();
        // Loop through all our destination cities and add them to our tour
        for (int cityIndex = 0; cityIndex < TourManager.numberOfCities(); cityIndex++) {
            setCity(cityIndex, TourManager.getCity(cityIndex));
        }
        // Randomly reorder the tour
        Collections.shuffle(tour);
    }

    // Gets a city from the tour
    public PlaceModel getCity(int tourPosition) {
        return (PlaceModel) tour.get(tourPosition);
    }

    // Sets a city in a certain position within a tour
    public void setCity(int tourPosition, PlaceModel city) {
        tour.set(tourPosition, city);
        // If the tours been altered we need to reset the fitness and travelTime
        fitness = 0;
        travelTime = 0;
    }

    // Gets the tours fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1 / (double) getDistance();
        }
        return fitness;
    }

    // Gets the total travelTime of the tour
    public double getDistance() {
        if (travelTime == 0) {
            double travelTime = 0;
            String url = "https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyBkW7P6dkzTYpCvmCnFPUOVHMz7qvjWUA4" +
                    "&origin=" + startCity.getLat() + "," + startCity.getLng() +
                    "&destination=" + endCity.getLat() + "," + endCity.getLng();
            url += "&waypoints=";
            for (int cityIndex = 0; cityIndex < tourSize(); cityIndex++) {
                try {
                    if (cityIndex == 0) {
                        url += URLEncoder.encode(getCity(cityIndex).getLat() + "," + getCity(cityIndex).getLng(), "UTF-8");
                    } else {
                        url += URLEncoder.encode("|" + getCity(cityIndex).getLat() + "," + getCity(cityIndex).getLng(), "UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            String json = GET(url);
            travelTime = new JSONparser().getTravelTime(json);
        }
        return travelTime;
    }

    // Get number of cities on our tour
    public int tourSize() {
        return tour.size();
    }

    // Check if the tour contains a city
    public boolean containsCity(PlaceModel city) {
        return tour.contains(city);
    }

    @Override
    public String toString() {
        String geneString = "|";
        geneString += startCity.getName() + "|";
        for (int i = 0; i < tourSize(); i++) {
            geneString += getCity(i).getName() + "|";
        }
        geneString += endCity.getName();
        return geneString;
    }
}
