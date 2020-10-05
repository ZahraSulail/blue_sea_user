package com.barmej.bluesea.domain.entity;

import java.io.Serializable;

public class User implements Serializable {
    private String id;

    private String assignedTrip;
    private String name;
    private String photo;

    public User(String id){
        this.id = id;
    }

    public User(){

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



}