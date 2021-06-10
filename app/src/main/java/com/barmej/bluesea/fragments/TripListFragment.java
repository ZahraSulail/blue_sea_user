package com.barmej.bluesea.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.bluesea.Constants;
import com.barmej.bluesea.R;
import com.barmej.bluesea.activities.LoginActivity;
import com.barmej.bluesea.activities.TripDetailsActivity;
import com.barmej.bluesea.adapter.TripItemsAdapter;
import com.barmej.bluesea.callback.OnTripClickListener;
import com.barmej.bluesea.domain.entity.FullStatus;
import com.barmej.bluesea.domain.entity.Trip;
import com.barmej.bluesea.domain.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TripListFragment extends Fragment implements OnTripClickListener {

    /*
     Define variables required in this fragment
     */
    private RecyclerView mRecyclerView;
    private TripItemsAdapter mAdapter;
    private ArrayList<Trip> mTrips;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_trips_list, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        /*
         Find recycleview by id and assigned it to it's variable
         */
        mRecyclerView = view.findViewById( R.id.trip_list_recycler_view );

        /*
         set layoutManager of the recycleview
         */
        mRecyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );

        /*
         Get new object of trips ArrayList
         */
        mTrips = new ArrayList<>();

        /*
         Get new object of TripItemsAdapter
         */
        mAdapter = new TripItemsAdapter( mTrips, TripListFragment.this );

        /*
         Pass mAdapter parameter to mRecyclerView object
         */
        mRecyclerView.setAdapter( mAdapter );

        /*
         Add divider to recyclerview items
         */
        mRecyclerView.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.HORIZONTAL ) );

        /*
         Check if no user found in firebase then return to LoginActivity
         */
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }

        /*
         Get instance of FirebaseDatabse and get userId to add trips to the trps list of user table in firebase
         */
        FirebaseDatabase.getInstance().getReference(Constants.USER_REF_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                HashMap<String, Object> trips = user.getTrips();
                mAdapter.setReservedTrips(trips);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Add trip to the list of trips if it is available
        FirebaseDatabase.getInstance().getReference( Constants.TRIP_REF_PATH ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mTrips.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Trip trip = dataSnapshot.getValue(Trip.class);
                        trip.setId(dataSnapshot.getKey());

                        //check if trip status is AVAILABLE then show trip within trip list items
                        if(trip.getStatus().equals(Trip.Status.AVAILABLE.name())) {
                            mTrips.add(trip);
                        }else if(trip.getStatus().equals(Trip.Status.GOING_TO_DESTINATION.name())) {

                            //check if trip id equals to any trip in users table
                            System.out.println("Going to dest " + trip.getId());
                            FirebaseDatabase.getInstance().getReference(Constants.USER_REF_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("trips").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if(dataSnapshot.getKey().equals(trip.getId())) {
                                            moveToCurrentTripStatusFragnment(trip);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            //add user id to trip table after reservation, then check if current user id exist in trip users
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );



    }

    /*
     CurrentTripStatusFragment displayed to the user while trip ON_GOING_TO_DESTINATION
     */
    public void moveToCurrentTripStatusFragnment(Trip trip){
        FullStatus status = new FullStatus();
        status.setTrip(trip);
        CurrentTripStatusFragment currentTripStatusFragment = CurrentTripStatusFragment.getInstance(status);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.layout_main, currentTripStatusFragment );
        transaction.commit();
    }

    /*
     onTripClick(): click on the trip to move to TripDetailsActivity and can reserve trip
     */
    @Override
    public void onTripClick(Trip trip) {
        Intent intent = new Intent( getContext(), TripDetailsActivity.class );
        intent.putExtra( Constants.TRIP_DATA, trip );
        startActivity( intent );
    }
}