package com.barmej.bluesea.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.barmej.bluesea.Constants;
import com.barmej.bluesea.R;
import com.barmej.bluesea.callback.StatusCallBack;
import com.barmej.bluesea.domain.TripManager;
import com.barmej.bluesea.domain.entity.FullStatus;
import com.barmej.bluesea.domain.entity.Trip;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CurrentTripFragment extends Fragment implements OnMapReadyCallback {


    /*
     Define required varaiables for this fragment
     */
    private CardView mCardView;
    private TextView startPointTextView;
    private TextView destinationTextView;
    private LinearLayout mLinearLayout;
    private TextView tripStatusTextView;
    private TextView toDestinationTextView;
    private MapView mapView;
    Bundle mapViewBundle;
    private GoogleMap mMap;
    private Marker startPointMarker;
    private Marker destinationMarker;
    private Marker shipMarker;


    /*
     Get an instance of CurrentTripFragment and save the it's intiail status in a bundle
     */
    public static CurrentTripFragment getInstance(FullStatus status) {
        CurrentTripFragment fragment = new CurrentTripFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.INITIAL_STATUS_EXTRA, status );
        fragment.setArguments( bundle );
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_current_trip, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        /*
         Find views by ids and assigned them to varaibles
         */
        mCardView = view.findViewById( R.id.card_view_trip );
        startPointTextView = view.findViewById( R.id.curr_text_view_position );
        destinationTextView = view.findViewById( R.id.curr_text_view_destination );
        mLinearLayout = view.findViewById( R.id.linearLayout );
        tripStatusTextView = view.findViewById( R.id.curr_text_view_trip_status );
        toDestinationTextView = view.findViewById( R.id.curr_text_view_to_destination );
        mapView = view.findViewById( R.id.current_trip_map_view );

        /*
         map saveInstanceState in a bundle
         */
        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle( Constants.MAPVIEW_BUNDLE_KEY );
        }

        /*
          Call getMapAsync() to register the callback.
         */
        if (mapView != null) {
            mapView.onCreate( mapViewBundle );
            mapView.getMapAsync( this );
        }

        /*
         Get serializable
         */
        FullStatus status = (FullStatus) getArguments().getSerializable( Constants.INITIAL_STATUS_EXTRA );
        updateWithStatus( status );

        TripManager.getInstance().startListeningToUpdates(new StatusCallBack() {
            @Override
            public void onUpdate(FullStatus fullStatus) {
                updateWithStatus(fullStatus);
            }
        });

        TripManager.getInstance().startMonitoringTrip(status.getTrip().getId());

    }

    /*
     Update the views of this fragment depends on trip status
     */
    public void updateWithStatus(FullStatus status) {
        Trip trip = status.getTrip();
        startPointTextView.setText( status.getTrip().getStartPortName() );
        destinationTextView.setText( status.getTrip().getDestinationSeaportName() );
        toDestinationTextView.setText( status.getTrip().getDestinationSeaportName() );
        String tripStatus = status.getTrip().getStatus();
        String tripStatusText = "";
        if (tripStatus.equals( Trip.Status.GOING_TO_DESTINATION )) {
            tripStatusText = getString( R.string.trip_going_to_destination );
        } else if (tripStatus.equals( Trip.Status.ARRIVED )) {
            tripStatusText = getString( R.string.trip_arrived );
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
            builder.setMessage( tripStatusText );
            builder.setPositiveButton( R.string.ok, null );
            builder.show();
        }
        tripStatusTextView.setText( tripStatus );
        setStartPointMarker(new LatLng(trip.getStartLat(), trip.getStartLng()));
        setDestinationMarker(new LatLng(trip.getDestinationLat(), trip.getDestinationLng()));
        setShipMarker(new LatLng(trip.getCurrentLat(), trip.getCurrentLng()));
    }

    /*
     onSaveInstanceState to save the map
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState( outState );
        //Bundle to save state of the map
        mapViewBundle = outState.getBundle( Constants.MAPVIEW_BUNDLE_KEY );
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle( Constants.MAPVIEW_BUNDLE_KEY, mapViewBundle );
        }
        mapView.onSaveInstanceState( mapViewBundle );
    }







    /*
     Destination marker displyed during the trip
     */
    public void setDestinationMarker(LatLng target) {
        if (mMap == null) return;

        if (destinationMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.destination );
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( target );
            destinationMarker = mMap.addMarker( options );
        } else {
            destinationMarker.setPosition( target );
        }

    }

    /*
    current ship "captain" marker displyed during the trip
     */
    public void setShipMarker(LatLng target) {
        if (mMap == null) return;
        ;
        if (shipMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.boat );
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( target );
            shipMarker = mMap.addMarker( options );
        } else {
            shipMarker.setPosition( target );
        }
    }

    /*
     remove user location from the map when the trip status is arrived
     */
    public void removeMapLocationLayout() {
        if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled( false );
        }
    }

    /*
     Set the pen of the map on the center on the screen
     */
    public LatLng captureCenter() {
        if (mMap == null) return null;

        return mMap.getCameraPosition().target;
    }

    public void setStartPointMarker(LatLng target) {
        if (mMap == null) return;
        if (startPointMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.position );
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( target );
            startPointMarker = mMap.addMarker( options );
        } else {
            startPointMarker.setPosition( target );
        }
    }

    /*
     reset oll markers and return to user location after the trip arrived
     */
    public void reset() {
        if (mMap != null) return;
        ;
        mMap.clear();
        startPointMarker = null;
        destinationMarker = null;
        shipMarker = null;

    }

    /*
     Display current location of the ship or captian during the trip going to destination
     */
    public void showTripCurrentLocationOnMap(LatLng tripLatng) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom( tripLatng, 16 );
        mMap.moveCamera( update );

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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

}


