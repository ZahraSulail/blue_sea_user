package com.barmej.bluesea.domain.entity;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {

    private String id;
    private String assignedTrip;
    private String name;
    private String photo;
    private String email;
    private String password;
    private String userPhoneNo;
    private HashMap<String, String> trips = new HashMap<>();


    public User(String id) {
        this.id = id;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAssignedTrip() {
        return assignedTrip;
    }

    public void setAssignedTrip(String assignedTrip) {
        this.assignedTrip = assignedTrip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public void setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
    }

    public void setTrips(HashMap<String, String> trips) {
        this.trips = trips;
    }

    public HashMap<String, String> getTrips() {
        return trips;
    }
}
