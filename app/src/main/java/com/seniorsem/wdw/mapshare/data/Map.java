package com.seniorsem.wdw.mapshare.data;

import java.util.List;

public class Map {

    private String CreaterUID;
    private List<MyMarker> MyMarkers;
    private String Date;
    private int NumSubs;
    private int Privacy;
    private String Title;
    private String Description;
    private List<String> Subscribers;

    public Map() {
    }

    public Map(String createrUID, List<MyMarker> markers, String date, int numSubs, int privacy, String title, String description, List<String> subscribers) {

        this.CreaterUID = createrUID;
        this.MyMarkers = markers;
        this.Date = date;
        this.NumSubs = numSubs;
        this.Privacy = privacy;
        this.Title = title;
        this.Description = description;
        this.Subscribers = subscribers;
    }

    public String getDescription() {
        return Description;
    }

    public List<String> getSubscribers() {
        return Subscribers;
    }

    public void setSubscribers(List<String> subscribers) {
        Subscribers = subscribers;
    }

    public void setDescription(String description) {
        Description = description;
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getCreaterUID() {
        return CreaterUID;
    }

    public void setCreaterUID(String createrUID) {
        CreaterUID = createrUID;
    }

    public List<MyMarker> getMyMarkers() {
        return MyMarkers;
    }

    public void setMyMarkers(List<MyMarker> markers) {
        MyMarkers = markers;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getNumSubs() {
        return NumSubs;
    }

    public void setNumSubs(int numSubs) {
        NumSubs = numSubs;
    }

    public int getPrivacy() {
        return Privacy;
    }

    public void setPrivacy(int privacy) {
        Privacy = privacy;
    }

}
