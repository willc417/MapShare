package com.seniorsem.wdw.mapshare.data;

import java.util.List;

public class User {

    private String UID;
    private List<String> createdMaps;
    private List<String> subMaps;
    private List<String> following;
    private List<String> followers;
    private String profilePicture;

    public User() {}

    public User(String UID, List<String> createdMaps, List<String> subMaps, List<String> following, List<String> followers, String profPic) {

        this.UID = UID;
        this.createdMaps = createdMaps;
        this.subMaps = subMaps;
        this.following = following;
        this.followers = followers;
        this.profilePicture = profPic;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public List<String> getCreatedMaps() {
        return createdMaps;
    }

    public void setCreatedMaps(List<String> createdMaps) {
        this.createdMaps = createdMaps;
    }

    public List<String> getSubMaps() {
        return subMaps;
    }

    public void setSubMaps(List<String> subMaps) {
        this.subMaps = subMaps;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public String getProfilePicture(){ return profilePicture; }

    public void setProfilePicture(String pic){ profilePicture = pic; }
}
