package com.barmej.bluesea.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.barmej.bluesea.R;
import com.barmej.bluesea.callback.BookTripInterface;
import com.barmej.bluesea.domain.entity.Trip;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TripDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TRIP_DATA = "trip_data";
    private static final String MAPVIEW_BUNDLE_KEY = "mapViewBundleKey";

    private CardView mCardView;
    private TextView mDateTextView;
    private TextView mPositionTextView;
    private TextView mDestinationTextView;
    private TextView mAvailableSeatsTextView;
    private Button mBookButton;
    private MapView mapView;

    Bundle mapViewBundle;
    Trip mTrip;

    BookTripInterface bookTripInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_trip_details );

        mCardView = findViewById( R.id.card_view );
        mDateTextView = findViewById( R.id.det_text_view_date );
        mPositionTextView = findViewById( R.id.det_text_view_position );
        mDestinationTextView = findViewById( R.id.det_text_view_destination );
        mAvailableSeatsTextView = findViewById( R.id.det_text_available_seats );
        mBookButton = findViewById( R.id.button_book );

        mapView = findViewById( R.id.map);
        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle( MAPVIEW_BUNDLE_KEY );
        }


        if (mapView != null) {
            mapView.onCreate( mapViewBundle );
            mapView.getMapAsync( this );
        }

        if(getIntent() != null && getIntent().getExtras() != null){
            Trip trip = (Trip) getIntent().getExtras().getSerializable( TRIP_DATA );
            if(trip != null){
                mDateTextView.setText( trip.getFormattedDate());
                mPositionTextView.setText( trip.getPositionSeaPortName());
                mDestinationTextView.setText( trip.getDestinationSeaportName());
                mAvailableSeatsTextView.setText( String.valueOf( trip.getAvailableSeats()));

            }

        }

        mBookButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTrip.getAvailableSeats() != 0) {
                    bookTrip();
                }else{
                    Toast.makeText( TripDetailsActivity.this, R.string.no_available_seats_to_book, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } );
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState( outState );
        //Bundle to save state of the map
        mapViewBundle = outState.getBundle( MAPVIEW_BUNDLE_KEY );
        if(mapViewBundle ==null){
            mapViewBundle = new Bundle();
            outState.putBundle( MAPVIEW_BUNDLE_KEY, mapViewBundle );
        }
        mapView.onSaveInstanceState( mapViewBundle );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (mTrip.getCurrentLat() != 0 && mTrip.getCurrentLng() != 0) {
            LatLng currentLatLng = new LatLng( mTrip.getCurrentLat(), mTrip.getCurrentLng());
            googleMap.addMarker( new MarkerOptions() )
                    .setPosition( currentLatLng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( currentLatLng, 16 );
            googleMap.moveCamera( cameraUpdate );
            return;
        }
        if(mTrip.getPositionLat() != 0 && mTrip.getPositionLatng() != 0){
            LatLng positionLatLng = new LatLng( mTrip.getPositionLat(), mTrip.getPositionLatng());
            googleMap.addMarker( new MarkerOptions() )
                    .setPosition( positionLatLng );
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( positionLatLng, 16 );
            googleMap.moveCamera( cameraUpdate );

            return;
        }

        if(mTrip.getDestinationLat() != 0 && mTrip.getDestinationLng() != 0){
            LatLng destinationLatng = new LatLng( mTrip.getDestinationLat(), mTrip.getDestinationLng());
            googleMap.addMarker( new MarkerOptions() )
                    .setPosition(destinationLatng );
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( destinationLatng, 16 );
            googleMap.moveCamera( cameraUpdate );

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void setTripActionsInterface( BookTripInterface bookTripInterface){
        this.bookTripInterface = bookTripInterface;
    }

    private void bookTrip(){
        if(bookTripInterface != null){
            bookTripInterface.bookTrip();
        }
        }
    }



