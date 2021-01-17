package com.barmej.bluesea.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.barmej.bluesea.R;
import com.barmej.bluesea.callback.BookTripInterface;
import com.barmej.bluesea.domain.entity.Trip;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

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
    private GoogleMap mGoogleMap;
    private Bundle mapViewBundle;
    private Task<Void> databaseReference;
    private Trip mTrip;
    private int availableSeats;

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

        //databaseReference = FirebaseDatabase.getInstance().getReference().child( "Teip_Details" );
        mTrip = new Trip();

        mapView = findViewById( R.id.map );
        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle( MAPVIEW_BUNDLE_KEY );
        }


        mTrip = (Trip) getIntent().getSerializableExtra( TRIP_DATA );
        if (mTrip != null) {
            mDateTextView.setText( mTrip.getFormattedDate() );
            mPositionTextView.setText( mTrip.getStartPortName() );
            mDestinationTextView.setText( mTrip.getDestinationSeaportName() );
            mAvailableSeatsTextView.setText( String.valueOf( mTrip.getAvailableSeats() ) );

        }

        if (mapView != null) {
            mapView.onCreate( mapViewBundle );
            mapView.getMapAsync( this );
        }

        mBookButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               bookTrip();
            }
        } );
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState( outState );
        //Bundle to save state of the map
        mapViewBundle = outState.getBundle( MAPVIEW_BUNDLE_KEY );
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle( MAPVIEW_BUNDLE_KEY, mapViewBundle );
        }
        mapView.onSaveInstanceState( mapViewBundle );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        if (mTrip.getCurrentLat() != 0 && mTrip.getCurrentLng() != 0) {
            LatLng currentLatLng = new LatLng( mTrip.getCurrentLat(), mTrip.getCurrentLng() );
            googleMap.addMarker( new MarkerOptions().icon( BitmapDescriptorFactory.fromResource( R.drawable.boat ) ) )
                    .setPosition( currentLatLng );
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( currentLatLng, 16 );
            googleMap.moveCamera( cameraUpdate );


        }

        if (mTrip.getStartLat() != 0 && mTrip.getStartLng() != 0) {
            LatLng positionLatLng = new LatLng( mTrip.getStartLat(), mTrip.getStartLng() );
            googleMap.addMarker( new MarkerOptions().position( positionLatLng ).icon( BitmapDescriptorFactory.fromResource( R.drawable.position ) ) );


        }

        if (mTrip.getDestinationLat() != 0 && mTrip.getDestinationLng() != 0) {

            LatLng destinationLatng = new LatLng( mTrip.getDestinationLat(), mTrip.getDestinationLng() );
            googleMap.addMarker( new MarkerOptions().position( destinationLatng ).icon( BitmapDescriptorFactory.fromResource( R.drawable.destination ) ) );
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

    public void bookTrip() {
        availableSeats = mTrip.getAvailableSeats();
        availableSeats = availableSeats - 1;

        databaseReference = FirebaseDatabase.getInstance().getReference().child( "Trip_Details" ).child( "availableSeats" ).setValue( availableSeats ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mAvailableSeatsTextView.setText( String.valueOf(availableSeats) );

                }
            }
        } );
    }
}



