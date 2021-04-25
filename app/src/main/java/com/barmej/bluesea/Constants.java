package com.barmej.bluesea;

public class Constants {

    //Constant reference request code to use it with requset location permission
    public static final int PERMISSION_REQUEST_READ_STORAGE = 1;

    //Constant reference request code to access device storage
    public static final int REQUEST_GET_PHOTO = 2;

    //Constant reference to pass trip data from TripListFragment to TripDetailsActivity
    public static final String TRIP_DATA = "trip_data";

    //Constant reference: string key to save the mapView in a bundle
    public static final String MAPVIEW_BUNDLE_KEY = "mapViewBundleKey";

    //Constant reference: path of the user table in firebase databse
    public static final String USER_REF_PATH = "Users";

    //Constant reference: path of the captain table in firebase databse
    public static final String CAPTAIN_REF_PATH = "captains";

    //Constant reference: path of the trips table in firebase databse
    public static final String TRIP_REF_PATH = "Trip_Details";

}
