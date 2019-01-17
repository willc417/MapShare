package com.seniorsem.wdw.mapshare.data;

public class Marker {

    private Double Lat;
    private Double Lon;
    private String Title;
    private String Description;
    private String ImageURL;
    private String VideoURL;
    private String IconType;
    private Boolean isVisited;

    public Marker() {}

    public Marker(Double Lat, Double Lon, String Title, String Description, String ImageURL, String VideoURL, String IconType, Boolean isVisited){
        this.Lat = Lat;
        this.Lon = Lon;
        this.Title = Title;
        this.Title = Description;
        this.ImageURL = ImageURL;
        this.VideoURL = VideoURL;
        this.IconType = IconType;
        this.isVisited = isVisited;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLon() {
        return Lon;
    }

    public void setLon(Double lon) {
        Lon = lon;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getVideoURL() {
        return VideoURL;
    }

    public void setVideoURL(String videoURL) {
        VideoURL = videoURL;
    }

    public String getIconType() {
        return IconType;
    }

    public void setIconType(String iconType) {
        IconType = iconType;
    }

    public Boolean getVisited() {
        return isVisited;
    }

    public void setVisited(Boolean visited) {
        isVisited = visited;
    }
}
