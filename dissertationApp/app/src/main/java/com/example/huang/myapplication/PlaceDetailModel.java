package com.example.huang.myapplication;

import java.util.ArrayList;

/**
 * Created by huang on 2018/3/21.
 */

public class PlaceDetailModel {

    String address;
    String phone;
    ArrayList<String> weekday;
    String duration;
    String description;
    String rating;
    String visitsCount;
    String likes;
    ArrayList<Reviews> reviews;

    public PlaceDetailModel() {
    }

    public PlaceDetailModel(String address, String phone, ArrayList<String> weekday, String duration, String description, String rating, String visitsCount, String likes, ArrayList<Reviews> reviews) {
        this.address = address;
        this.phone = phone;
        this.weekday = weekday;
        this.duration = duration;
        this.description = description;
        this.rating = rating;
        this.visitsCount = visitsCount;
        this.likes = likes;
        this.reviews = reviews;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<String> getWeekday() {
        return weekday;
    }

    public void setWeekday(ArrayList<String> weekday) {
        this.weekday = weekday;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getVisitsCount() {
        return visitsCount;
    }

    public void setVisitsCount(String visitsCount) {
        this.visitsCount = visitsCount;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public ArrayList<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Reviews> reviews) {
        this.reviews = reviews;
    }
}

class Reviews {
    String author_name;
    String rating;
    String relative_time;
    String text;

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRelative_time() {
        return relative_time;
    }

    public void setRelative_time(String relative_time) {
        this.relative_time = relative_time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}