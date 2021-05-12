package com.barmej.bluesea.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.bluesea.R;
import com.barmej.bluesea.callback.OnTripClickListener;
import com.barmej.bluesea.domain.entity.Trip;

import java.util.HashMap;
import java.util.List;

public class TripItemsAdapter extends RecyclerView.Adapter<TripItemsAdapter.TripViewHolder> {

    private List<Trip> mTripList;
    private OnTripClickListener mTripClickListiner;
    private HashMap<String,String> mReservedTrips;

    public TripItemsAdapter(List<Trip> tripList, OnTripClickListener tripClickListiner) {
        this.mTripList = tripList;
        this.mTripClickListiner = tripClickListiner;

    }

    @NonNull
    @Override
    public TripItemsAdapter.TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_trip, parent, false );
        return new TripViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull TripItemsAdapter.TripViewHolder holder, int position) {
        holder.bind( mTripList.get( position ) );
    }

    @Override
    public int getItemCount() {
        return mTripList.size();
    }

    public void setReservedTrips(HashMap<String, String> reservedTrips) {
        mReservedTrips = reservedTrips;
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {

        TextView mDateTextView;
        TextView mPositionTextView;
        TextView mDestinationPortTextView;
        TextView mAvailableSeatsTextView;
        TextView mBookedSeatsTextView;


        public TripViewHolder(@NonNull View itemView) {
            super( itemView );

            mDateTextView = itemView.findViewById( R.id.text_view_date );
            mPositionTextView = itemView.findViewById( R.id.text_view_position );
            mDestinationPortTextView = itemView.findViewById( R.id.text_view_destination );
            mAvailableSeatsTextView = itemView.findViewById( R.id.text_view_available_seats );
            mBookedSeatsTextView = itemView.findViewById( R.id.text_view_booked_seats );
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Trip trip = mTripList.get( getAdapterPosition() );
                    mTripClickListiner.onTripClick( trip );
                }
            } );
        }

        public void bind(Trip trip) {


            if(mReservedTrips != null && mReservedTrips.containsKey(trip.getId())) {
                // show reserved icon
                mBookedSeatsTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle_accent_color_24dp, 0, 0, 0);
            } else {
                // Hide reserved icon
                mBookedSeatsTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            mDateTextView.setText( trip.getFormattedDate());
            mPositionTextView.setText( trip.getStartPortName() );
            mDestinationPortTextView.setText( trip.getDestinationSeaportName() );
            mAvailableSeatsTextView.setText( String.valueOf( trip.getAvailableSeats() ) );
            mAvailableSeatsTextView.setVisibility(View.VISIBLE);
            mBookedSeatsTextView.setText( String.valueOf( trip.getBookedSeats() ) );

        }
    }
}
