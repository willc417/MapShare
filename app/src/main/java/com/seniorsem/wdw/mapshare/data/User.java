package com.seniorsem.wdw.mapshare.data;

import java.util.List;

public class User {

    private String UID;
    private List<String> createdMaps;
    private List<String> subMaps;
    private List<String> Friends;

    public User() {}

    public User(String UID, List<String> createdMaps, List<String> subMaps, List<String> friends) {

        this.UID = UID;
        this.createdMaps = createdMaps;
        this.subMaps = subMaps;
        this.Friends = friends;

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

    public List<String> getFriends() {
        return Friends;
    }

    public void setFriends(List<String> friends) {
        Friends = friends;
    }
}
