package com.barmej.bluesea.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barmej.bluesea.R;
import com.barmej.bluesea.callback.OnTripClickListiner;
import com.barmej.bluesea.domain.entity.Trip;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripItemsAdapter extends  RecyclerView.Adapter<TripItemsAdapter.TripViewHolder> {

    private List<Trip> mTripList;
    private OnTripClickListiner mTripClickListiner;

    public TripItemsAdapter(List<Trip> tripList,  OnTripClickListiner tripClickListiner){
        this.mTripList = tripList;
        this.mTripClickListiner = tripClickListiner;

    }
    @NonNull
    @Override
    public TripItemsAdapter.TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.item_trip, parent, false );
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripItemsAdapter.TripViewHolder holder, int position) {
        holder.bind( mTripList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTripList.size();
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
                    Trip trip = mTripList.get(getAdapterPosition());
                    mTripClickListiner.onTripClick( trip );
                }
            } );
        }

        public void bind(Trip trip){

            mDateTextView.setText( trip.getFormattedDate());
            mPositionTextView.setText( trip.getPositionSeaPortName());
            mDestinationPortTextView.setText( trip.getDestinationSeaportName());
            mAvailableSeatsTextView.setText(String.valueOf( trip.getAvailableSeats()));
            mBookedSeatsTextView.setText(String.valueOf( trip.getBookedSeats()));

        }
    }


}
