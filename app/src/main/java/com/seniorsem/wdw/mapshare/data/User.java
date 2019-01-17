package com.seniorsem.wdw.mapshare.data;

import java.util.List;

public class User {

    private String UID;
    private List<Map> createdMaps;
    private List<Map> subMaps;
    private List<User> Friends;

    public User(String UID, List<Map> createdMaps, List<Map> subMaps, List<User> friends) {

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

    public List<Map> getCreatedMaps() {
        return createdMaps;
    }

    public void setCreatedMaps(List<Map> createdMaps) {
        this.createdMaps = createdMaps;
    }

    public List<Map> getSubMaps() {
        return subMaps;
    }

    public void setSubMaps(List<Map> subMaps) {
        this.subMaps = subMaps;
    }

    public List<User> getFriends() {
        return Friends;
    }

    public void setFriends(List<User> friends) {
        Friends = friends;
    }
}
