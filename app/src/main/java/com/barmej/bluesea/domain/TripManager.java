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

    private FirebaseDatabase database;
    private static TripManager instance;
    private User user;
    private Trip trip;
    private Captain captain;
    private StatusCallBack statusCallBack;
    private ValueEventListener tripListener;
    FullStatus fullStatus;

    private TripManager() {
        database = FirebaseDatabase.getInstance();
    }

    public static TripManager getInstance() {
        if (instance == null) {
            instance = new TripManager();
        }
        return instance;
    }

    public void getUserProfile(final String userId, final CallBack callBack) {
        database.getReference(Constants.USER_REF_PATH ).child( userId ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue( User.class );
                callBack.onComplete(user != null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    public void startListeningToUupdates(StatusCallBack statusCallBack) {
        this.statusCallBack = statusCallBack;
        startMonitoringState();
    }

    private void startMonitoringState() {

        startMonitoringTrip( user.getAssignedTrip() );
    }

    private void notifyListener(FullStatus fullStatus) {
        if (statusCallBack != null) {
            statusCallBack.onUpdate( fullStatus );
        }
    }

    private void startMonitoringTrip(String id) {
        tripListener = database.getReference( Constants.TRIP_REF_PATH).child( id ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trip = snapshot.getValue( Trip.class );
                if (captain == null) {
                    database.getReference( Constants.CAPTAIN_REF_PATH ).child( trip.getCaptainId() ).addListenerForSingleValueEvent( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            captain = snapshot.getValue( Captain.class );
                            updateStatusWithTrip();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    } );
                } else {
                    updateStatusWithTrip();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void updateStatusWithTrip() {
        fullStatus = new FullStatus();
        fullStatus.setUser( user );
        fullStatus.setCaptain( captain );
        fullStatus.setTrip( trip );

        if (trip.getStatus().equals( Trip.Status.ARRIVED.name() )) {
            removeTripListener();

            notifyListener( fullStatus );

            user.setAssignedTrip( null );

            trip = null;
            captain = null;
            fullStatus.setTrip( null );
            fullStatus.setCaptain( null );
            database.getReference( Constants.TRIP_REF_PATH).child( user.getId() ).setValue( user );
            notifyListener( fullStatus );

        } else {
            notifyListener( fullStatus );
        }
    }

    private void removeTripListener() {
        if (tripListener != null && trip != null) {
            database.getReference( Constants.TRIP_REF_PATH).child( trip.getId() ).removeEventListener( tripListener );
            tripListener = null;
        }
    }

}

