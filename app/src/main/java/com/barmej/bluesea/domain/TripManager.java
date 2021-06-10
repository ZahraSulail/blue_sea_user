package com.barmej.bluesea.domain;

import androidx.annotation.NonNull;

import com.barmej.bluesea.Constants;
import com.barmej.bluesea.callback.CallBack;
import com.barmej.bluesea.callback.StatusCallBack;
import com.barmej.bluesea.domain.entity.Captain;
import com.barmej.bluesea.domain.entity.FullStatus;
import com.barmej.bluesea.domain.entity.Trip;
import com.barmej.bluesea.domain.entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripManager {

    /*
    References of objects
     */
    private static TripManager instance;
    private final FirebaseDatabase database;
    private User user;
    private Trip trip;
    private Captain captain;
    private StatusCallBack statusCallBack;
    private ValueEventListener tripListener;

    /*
     Constructor of TripManager class
     */
    private TripManager() {
        //Get an instance of FirebaseDatabase
        database = FirebaseDatabase.getInstance();
    }

    /*
     Get instance of TripManager
     */
    public static TripManager getInstance() {
        if (instance == null) {
            instance = new TripManager();
        }
        return instance;
    }

    /*
     Get user profile information one time of callback
     */
    public void getUserProfile(final String userId, final CallBack callBack) {
        database.getReference(Constants.USER_REF_PATH).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                callBack.onComplete(user != null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
     listening for status
     */
    public void startListeningToUpdates(StatusCallBack statusCallBack) {
        this.statusCallBack = statusCallBack;
       // startMonitoringState();
    }

    /*
    monitor user's assignedTrip
     */
   /* private void startMonitoringState() {
        startMonitoringTrip(user.getAssignedTrip());
    }*/

    /*
     notify status callback
     */
    private void notifyListener(FullStatus fullStatus) {
        if (statusCallBack != null) {
            statusCallBack.onUpdate(fullStatus);
        }
    }

    /*
    monitor trip and get snapshot from database
     */
    public void startMonitoringTrip(String id) {
        tripListener = database.getReference(Constants.TRIP_REF_PATH).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trip = snapshot.getValue(Trip.class);
                /*if (captain == null) {
                    database.getReference(Constants.CAPTAIN_REF_PATH).child(trip.getCaptainId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            captain = snapshot.getValue(Captain.class);
                            updateStatusWithTrip();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {*/
                    updateStatusWithTrip();
                /*}*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
    Update TripStatus ana set status of user, captain, and trip
     */
    private void updateStatusWithTrip() {
        FullStatus fullStatus = new FullStatus();
        fullStatus.setUser(user);
        fullStatus.setCaptain(captain);
        fullStatus.setTrip(trip);

        if (trip.getStatus().equals(Trip.Status.ARRIVED.name())) {
            removeTripListener();
            notifyListener(fullStatus);
            user.setAssignedTrip(null);
            trip = null;
            captain = null;
            fullStatus.setTrip(null);
            fullStatus.setCaptain(null);
            database.getReference(Constants.TRIP_REF_PATH).child(user.getId()).setValue(user);
            notifyListener(fullStatus);
        } else {
            notifyListener(fullStatus);
        }
    }

    /*
     removeTripListener when trip status is arrived
     */
    private void removeTripListener() {
        if (tripListener != null && trip != null) {
            database.getReference(Constants.TRIP_REF_PATH).child(trip.getId()).removeEventListener(tripListener);
            tripListener = null;
        }
    }

}

