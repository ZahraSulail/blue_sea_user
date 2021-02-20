package com.barmej.bluesea.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.barmej.bluesea.R;
import com.barmej.bluesea.domain.entity.FullStatus;
import com.barmej.bluesea.domain.entity.Trip;

public class CurrentTripStatusFragment extends Fragment {

    private CardView cardView;
    private TextView tripStartTextView;
    private TextView tripDestinationTextView;
    private TextView tripStatusTextView;
    private TextView gotToDestinationTextView;

    public static final String INITIAL_STATUS_EXTRA = "INITIAL_STATUS_EXTRA ";

     public static CurrentTripStatusFragment getInstance(FullStatus status){
         CurrentTripStatusFragment fragment = new CurrentTripStatusFragment();
         Bundle bundle = new Bundle();
         bundle.putSerializable(INITIAL_STATUS_EXTRA, status);
         fragment.setArguments(bundle);
         return fragment;
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_trip_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardView = view.findViewById(R.id.card_view);
        tripStartTextView = view.findViewById(R.id.curr_trp_text_view_start);
        tripDestinationTextView = view.findViewById(R.id.curr_trip_text_view_destination);
        tripStatusTextView = view.findViewById(R.id.curr_trip_text_view_trip_status);
        gotToDestinationTextView = view.findViewById(R.id.curr_trip_text_view_to_destination);

        FullStatus status = (FullStatus) getArguments().getSerializable( INITIAL_STATUS_EXTRA );
        updateWithStatus( status );


    }

    public void updateWithStatus(FullStatus status){

        tripStartTextView.setText(status.getTrip().getStartPortName());
        tripDestinationTextView.setText(status.getTrip().getDestinationSeaportName());
        String tripStatus = status.getTrip().getStatus();
        String tripStatusText = "";

        if(tripStatus.equals(Trip.Status.GOING_TO_DESTINATION)){
          tripStatusText = getString(R.string.going_to_destination);
        }else if(tripStatus.equals(Trip.Status.ARRIVED)){
            tripStatusText = getString(R.string.trip_arrived);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(tripStatusText);
            builder.setPositiveButton(R.string.ok, null);
            builder.show();
        }
        tripStatusTextView.setText(tripStatusText);


    }

    public void goToCurrentTripFragment(View view){

    }
}