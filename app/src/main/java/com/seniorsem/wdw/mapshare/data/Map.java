package com.seniorsem.wdw.mapshare.data;

import java.util.List;

public class Map {

    private String CreaterUID;
    private List<Marker>Markers;
    private String Date;
    private int NumSubs;
    private int Privacy;

    public Map(String createrUID, List<Marker> markers, String date, int numSubs, int privacy) {

        CreaterUID = createrUID;
        Markers = markers;
        Date = date;
        NumSubs = numSubs;
        Privacy = privacy;
    }

    public String getCreaterUID() {
        return CreaterUID;
    }

    public void setCreaterUID(String createrUID) {
        CreaterUID = createrUID;
    }

    public List<Marker> getMarkers() {
        return Markers;
    }

    public void setMarkers(List<Marker> markers) {
        Markers = markers;
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
