package com.barmej.bluesea.domain.entity;

import android.icu.text.DateFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;


public class Trip implements Serializable {
    private String status;
    private String id;
    private String captainId;
    private String userId;
    private double positionLat;
    private double positionLatng;
    private double destinationLat;
    private double destinationLng;
    private double currentLat;
    private double currentLng;
    private String positionSeaPortName;
    private String destinationSeaportName;
    private int availableSeats;
    private int bookedSeats;
    private long dateTime;



public Trip(){

}

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaptainId() {
        return captainId;
    }

    public void setCaptainId(String captainId) {
        this.captainId = captainId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getPositionLat() {
        return positionLat;
    }

    public void setPositionLat(double positionLat) {
        this.positionLat = positionLat;
    }

    public double getPositionLatng() {
        return positionLatng;
    }

    public void setPositionLatng(double positionLatng) {
        this.positionLatng = positionLatng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public String getPositionSeaPortName() {
        return positionSeaPortName;
    }

    public void setPositionSeaPortName(String positionSeaPortName) {
        this.positionSeaPortName = positionSeaPortName;
    }

    public String getDestinationSeaportName() {
        return destinationSeaportName;
    }

    public void setDestinationSeaportName(String destinationSeaportName) {
        this.destinationSeaportName = destinationSeaportName;
    }

    public int getAvailableSeats(){
    return availableSeats;
    }

    public void setAvailableSeats(int seats){
    this.availableSeats = seats;
    }

    public int getBookedSeats(){
    return bookedSeats;
    }

    public void setBookedSeats(int bookedSeats){
    this.bookedSeats = bookedSeats;
    }



    public String getFormattedDate(){
        return DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format( new Date(dateTime));
    }

    /*
      Statuses of trip
     */
    public enum status{

        GOING_TO_DESTINATION,
        ARRIVED


    }
}
