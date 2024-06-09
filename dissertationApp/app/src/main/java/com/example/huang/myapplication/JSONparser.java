package com.example.huang.myapplication;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by huang on 2018/3/21.
 */

public class JSONparser {
    JSONObject json = null;

    public String JSONfileReader(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList getTemplatePlans(String type, String result) {
        ArrayList<TemplateModel> templates = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray planArry = obj.getJSONArray(type);
            // get plan list by type
            for (int i = 0; i < planArry.length(); i++) {
                TemplateModel template = new TemplateModel();
                ArrayList<PlaceModel> places = new ArrayList<>();
                ArrayList<TransportModel> transports = new ArrayList<>();
                //region set plan type
                switch (type) {
                    case "relax":
                        template.setPlanType("RELAX & ENTERTAINMENT");
                        break;
                    case "family":
                        template.setPlanType("FAMILY & KID");
                        break;
                    case "culture":
                        template.setPlanType("CULTURE & KNOWLEDGE");
                        break;
                    case "food":
                        template.setPlanType("FOOD & DRINK");
                        break;
                }
                //endregion

                //region get place list in a plan
                JSONArray placeArry = planArry.getJSONObject(i).getJSONArray("places");
                for (int j = 0; j < placeArry.length(); j++) {
                    PlaceModel place = new PlaceModel();
                    String name = placeArry.getJSONObject(j).getString("name");
                    double lat = Double.parseDouble(placeArry.getJSONObject(j).getJSONObject("location").getString("lat"));
                    double lng = Double.parseDouble(placeArry.getJSONObject(j).getJSONObject("location").getString("lng"));
                    String id = placeArry.getJSONObject(j).getString("id");
                    String types = placeArry.getJSONObject(j).getString("types");
                    String opennow = "";
                    String rating = placeArry.getJSONObject(j).getString("rating");

                    place.setName(name);
                    place.setLat(lat);
                    place.setLng(lng);
                    place.setId(id);
                    place.setType(types);
                    place.setOpen_now(opennow);
                    place.setRating(rating);

                    places.add(place);
                }
                template.setPlaces(places);
                //endregion

                //region get transport list in a plan
                JSONArray transportArry = planArry.getJSONObject(i).getJSONArray("transport");
                for (int k = 0; k < transportArry.length(); k++) {
                    TransportModel transport = new TransportModel();
                    String travel_mode = transportArry.getJSONObject(k).getString("travel_mode");
                    long duration = Long.parseLong(transportArry.getJSONObject(k).getString("duration"));
                    transport.setTravel_mode(travel_mode);
                    transport.setDuration(duration);

                    if (transportArry.getJSONObject(k).has("steps")) {
                        ArrayList<Steps> steps = new ArrayList<>();
                        JSONArray stepArry = transportArry.getJSONObject(k).getJSONArray("steps");
                        for (int l = 0; l < stepArry.length(); l++) {
                            Steps step = new Steps();
                            String travel_mode_s = stepArry.getJSONObject(l).getString("travel_mode");
                            long duration_s = Long.parseLong(stepArry.getJSONObject(l).getString("duration"));
                            String html_instructions = stepArry.getJSONObject(l).getString("html_instructions");
                            step.setTravel_mode(travel_mode_s);
                            step.setDuration(duration_s);
                            step.setHtml_instructions(html_instructions);

                            if (stepArry.getJSONObject(l).has("transit_details")) {
                                TransitDetails detail = new TransitDetails();
                                JSONObject detailObj = stepArry.getJSONObject(l).getJSONObject("transit_details");
                                String departure_stop = detailObj.getString("departure_stop");
                                String arrival_stop = detailObj.getString("arrival_stop");
                                String vehicle_name = detailObj.getString("vehicle_name");
                                String vehicle_type = detailObj.getString("vehicle_type");
                                detail.setDeparture_stop(departure_stop);
                                detail.setArrival_stop(arrival_stop);
                                detail.setVehicle_name(vehicle_name);
                                detail.setVehicle_type(vehicle_type);
                                step.setTransitDetails(detail);
                            }
                            steps.add(step);
                        }
                        transport.setSteps(steps);
                    }
                    transports.add(transport);
                }
                template.setTransports(transports);
                //endregion

                templates.add(template);
            }
            return templates;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList informList(String result) {
        ArrayList<PlaceModel> placeModels = new ArrayList<>();
        try {
            json = new JSONObject(result);
            JSONArray value = json.getJSONArray("results");
            int len = value.length();

            for (int i = 0; i < len; i++) {
                PlaceModel model = new PlaceModel();
                String name = value.getJSONObject(i).getString("name");
                double lat = Double.parseDouble(value.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat"));
                double lng = Double.parseDouble(value.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng"));
                String id = value.getJSONObject(i).getString("place_id");
                String types_string = value.getJSONObject(i).getString("types").replace("[", "").replace("]", "");
                ArrayList<String> types = new ArrayList<String>(Arrays.asList(types_string.split(",")));
                String opennow = "";
                if (value.getJSONObject(i).has("opening_hours"))
                    opennow = (value.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) ?
                            value.getJSONObject(i).getJSONObject("opening_hours").getString("open_now") : "";
                String rating = "";
                if (value.getJSONObject(i).has("rating"))
                    rating = value.getJSONObject(i).getString("rating");

                model.setName(name);
                model.setLat(lat);
                model.setLng(lng);
                model.setId(id);
                model.setType(types.get(0).replace("\"", ""));
                model.setOpen_now(opennow);
                model.setRating(rating);
                placeModels.add(model);
            }

            return placeModels;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * public long traveling_time(String result) {
     * //ArrayList<PlaceModel> placeModels= new ArrayList<>();
     * long time = 0;
     * try {
     * json = new JSONObject(result);
     * JSONArray value = json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
     * int len = value.length();
     * <p>
     * for (int i = 0; i < len; i++) {
     * //PlaceModel model = new PlaceModel();
     * String duration = value.getJSONObject(i).getJSONObject("duration").getString("value");
     * time = Long.parseLong(duration) / 60;
     * }
     * //return time;
     * <p>
     * } catch (JSONException e) {
     * time = -1;
     * e.printStackTrace();
     * }
     * return time;
     * }
     */
    public long traveling_time(String result) {
        //ArrayList<PlaceModel> placeModels= new ArrayList<>();
        long time = 0;
        try {
            json = new JSONObject(result);
            JSONArray value = null;
            //transport type is available
            if (json.getJSONArray("routes").length() > 0) {
                value = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                int len = value.length();
                for (int i = 0; i < len; i++) {
                    //PlaceModel model = new PlaceModel();
                    String duration = value.getJSONObject(i).getJSONObject("duration").getString("value");
                    time = Long.parseLong(duration) / 60;
                }
            } else {  //transport type is not available (change to walking)
                time = -1;
            }

            //return time;

        } catch (JSONException e) {
            time = -1;
            e.printStackTrace();
        }
        return time;
    }

    public double getTravelTime(String result) {
        //ArrayList<PlaceModel> placeModels= new ArrayList<>();
        double travelTime = 0;
        try {
            json = new JSONObject(result);
            JSONArray value = null;
            //transport type is available
            if (json.getJSONArray("routes").length() > 0) {
                value = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                int len = value.length();
                for (int i = 0; i < len; i++) {
                    //PlaceModel model = new PlaceModel();
                    String distance_value = value.getJSONObject(i).getJSONObject("duration").getString("value");
                    travelTime += Double.parseDouble(distance_value) / 60;
                }
            } else {  //transport type is not available (change to walking)
                travelTime = -1;
            }

            //return travelTime;

        } catch (JSONException e) {
            travelTime = -1;
            e.printStackTrace();
        }
        return travelTime;
    }

    public ArrayList waypoint_order(String result) {
        try {
            json = new JSONObject(result);
            JSONArray value = json.getJSONArray("routes");
            int len = value.length();
            String order = "";

            for (int i = 0; i < len; i++) {
                order = value.getJSONObject(i).getString("waypoint_order");
                order = order.replace("[", "");
                order = order.replace("]", "");
            }
            ArrayList<Integer> orderList = new ArrayList<>();
            if (order.trim().length() > 0) {
                ArrayList<String> list = new ArrayList<>(Arrays.asList(order.split(",")));
                for (String str : list) {
                    orderList.add(Integer.parseInt(str));
                }
            }
            return orderList;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TransportModel transport_detail(String result, String travel_mode, long duration) {
        TransportModel transportModel = new TransportModel();
        ArrayList<Steps> steps = new ArrayList<>();
        try {
            transportModel.setTravel_mode(travel_mode);
            transportModel.setDuration(duration);
            //"bus" and "rail" has steps & transit_details
            if (travel_mode.matches("bus") || travel_mode.matches("rail")) {
                json = new JSONObject(result);
                JSONArray value = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(0).getJSONArray("steps");
                int len = value.length();
                // may have multiple steps
                for (int i = 0; i < len; i++) {
                    Steps step = new Steps();
                    String s_travel_mode = value.getJSONObject(i).getString("travel_mode");
                    long s_duration = Long.parseLong(value.getJSONObject(i).getJSONObject("duration").getString("value")) / 60;
                    String html_instructions = value.getJSONObject(i).getString("html_instructions");

                    step.setTravel_mode(s_travel_mode);
                    step.setDuration(s_duration);
                    step.setHtml_instructions(html_instructions);

                    // "TRANSIT" has single transit_detail
                    if (s_travel_mode.matches("TRANSIT")) {
                        TransitDetails detail = new TransitDetails();
                        JSONObject transit_details = value.getJSONObject(i).getJSONObject("transit_details");
                        String departure_stop = transit_details.getJSONObject("departure_stop").getString("name");
                        String arrival_stop = transit_details.getJSONObject("arrival_stop").getString("name");
                        String vehicle_name = (transit_details.getJSONObject("line").has("short_name")) ?
                                transit_details.getJSONObject("line").getString("short_name") : transit_details.getJSONObject("line").getString("name");
                        String vehicle_type = transit_details.getJSONObject("line").getJSONObject("vehicle").getString("name");

                        detail.setDeparture_stop(departure_stop);
                        detail.setArrival_stop(arrival_stop);
                        detail.setVehicle_name(vehicle_name);
                        detail.setVehicle_type(vehicle_type);
                        step.setTransitDetails(detail);
                    }
                    steps.add(step);
                }
                transportModel.setSteps(steps);
            }
            return transportModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlaceDetailModel getPlaceDetail(String result) {
        PlaceDetailModel placeDetailModel = new PlaceDetailModel();
        try {
            json = new JSONObject(result);
            JSONObject value = json.getJSONObject("result");

            String address = (value.has("formatted_address")) ? value.getString("formatted_address") : "";
            String phone = (value.has("formatted_phone_number")) ? value.getString("formatted_phone_number") : "";
            ArrayList<String> weekday = new ArrayList<>();
            if (value.has("opening_hours")) {
                JSONArray weekdayArry = value.getJSONObject("opening_hours").getJSONArray("weekday_text");
                for (int i = 0; i < weekdayArry.length(); i++) {
                    weekday.add(weekdayArry.getString(i));
                }
            }
            ArrayList<Reviews> reviews = new ArrayList<>();
            if (value.has("reviews")) {
                JSONArray reviewArry = value.getJSONArray("reviews");
                for (int i = 0; i < reviewArry.length(); i++) {
                    Reviews review = new Reviews();
                    review.setAuthor_name(reviewArry.getJSONObject(i).getString("author_name"));
                    review.setRating(reviewArry.getJSONObject(i).getString("rating"));
                    review.setRelative_time(reviewArry.getJSONObject(i).getString("relative_time_description"));
                    review.setText(reviewArry.getJSONObject(i).getString("text"));
                    reviews.add(review);
                }
            }

            placeDetailModel.setAddress(address);
            placeDetailModel.setPhone(phone);
            placeDetailModel.setWeekday(weekday);
            placeDetailModel.setReviews(reviews);

            return placeDetailModel;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlaceDetailModel getPlaceDetailbySygic(String result, PlaceDetailModel placeDetailModel) {
        //PlaceDetailModel placeDetailModel = new PlaceDetailModel();
        try {
            json = new JSONObject(result);
            JSONObject value = json.getJSONObject("data");
            JSONArray placeArry = value.getJSONArray("places");
            if (placeArry.length() > 0) {
                JSONObject place = placeArry.getJSONObject(0);
                String duration = (place.has("duration")) ? String.valueOf((place.getDouble("duration") / 60) / 60) + " hr" : "";
                String description = (place.has("perex")) ? place.getString("perex") : "";
                placeDetailModel.setDuration(duration);
                placeDetailModel.setDescription(description);
            }

            return placeDetailModel;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPlaceIdbyFS(String result) {
        //PlaceDetailModel placeDetailModel = new PlaceDetailModel();
        try {
            json = new JSONObject(result);
            JSONObject value = json.getJSONObject("response");
            JSONArray placeArry = value.getJSONArray("venues");
            String id = null;
            if (placeArry.length() > 0) {
                JSONObject place = placeArry.getJSONObject(0);
                id = place.getString("id");
            }

            return id;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlaceDetailModel getPlaceDetailbyFS(String result, PlaceDetailModel placeDetailModel) {
        //PlaceDetailModel placeDetailModel = new PlaceDetailModel();
        try {
            json = new JSONObject(result);
            JSONObject value = json.getJSONObject("response").getJSONObject("venue");
            if(value.has("description")){
                String description = value.getString("description");
                placeDetailModel.setDescription(description);
            }
            String rating = (value.has("rating")) ? new String(Character.toChars(0x2B50))+value.getString("rating")+" out of 10" : "";
            placeDetailModel.setRating(rating);
            if(value.has("stats")){
                JSONObject stats = value.getJSONObject("stats");
                String visitsCount = new String(Character.toChars(0x1F64B))+stats.getString("visitsCount")+" visitors";
                placeDetailModel.setVisitsCount(visitsCount);
            }
            if(value.has("likes")){
                JSONObject likesObj = value.getJSONObject("likes");
                String likes = new String(Character.toChars(0x2764))+likesObj.getString("count")+" likes";
                placeDetailModel.setLikes(likes);
            }

            return placeDetailModel;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
