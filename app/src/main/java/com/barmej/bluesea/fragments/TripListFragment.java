package com.barmej.bluesea.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barmej.bluesea.R;
import com.barmej.bluesea.adapter.TripItemsAdapter;
import com.barmej.bluesea.callback.OnTripClickListiner;
import com.barmej.bluesea.domain.entity.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TripListFragment  extends Fragment implements OnTripClickListiner {

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

        mRecyclerView = view.findViewById( R.id.trip_list_recycler_view );
        mRecyclerView.setLayoutManager( new LinearLayoutManager( getContext()));
        mTrips = new ArrayList<>();
        mAdapter = new TripItemsAdapter( mTrips, TripListFragment.this );
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.HORIZONTAL));
        FirebaseDatabase.getInstance().getReference( "Trip_Details").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Trip trip = dataSnapshot.getValue(Trip.class);
                        mTrips.add( trip );
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    @Override
    public void onTripClick(Trip trip) {

    }
}
