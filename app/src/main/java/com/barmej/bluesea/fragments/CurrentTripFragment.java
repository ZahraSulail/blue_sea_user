package com.barmej.bluesea.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barmej.bluesea.R;
import com.barmej.bluesea.domain.entity.FullStatus;
import com.barmej.bluesea.domain.entity.Trip;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class CurrentTripFragment extends Fragment implements OnMapReadyCallback {
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String MAPVIEW_BUNDLE_KEY = "mapViewBundleKey";
    public static final String INITIAL_STATUS_EXTRA = "initial_status_extra";

    public static CurrentTripFragment getInstance(FullStatus status) {
        CurrentTripFragment fragment = new CurrentTripFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable( INITIAL_STATUS_EXTRA, status );
        fragment.setArguments( bundle );
        return fragment;
    }


    private CardView mCardView;
    private TextView positionTextView;
    private TextView destinationTextView;
    private LinearLayout mLinearLayout;
    private TextView tripStatusTextView;
    private TextView toDestinationTextView;
    private MapView mapView;
    Bundle mapViewBundle;
    private GoogleMap mMap;
    private Marker positionMarker;
    private Marker destinationMarker;
    private Marker shipMarker;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_current_trip, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        mCardView = view.findViewById( R.id.card_view_trip );
        positionTextView = view.findViewById( R.id.curr_text_view_position );
        destinationTextView = view.findViewById( R.id.curr_text_view_destination );
        mLinearLayout = view.findViewById( R.id.linearLayout );
        tripStatusTextView = view.findViewById( R.id.curr_text_view_trip_status );
        toDestinationTextView = view.findViewById( R.id.curr_text_view_to_destination );

        mapView = view.findViewById( R.id.current_trip_map_view );
        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle( MAPVIEW_BUNDLE_KEY );
        }


        if (mapView != null) {
            mapView.onCreate( mapViewBundle );
            mapView.getMapAsync( this );
        }

        FullStatus status = (FullStatus) getArguments().getSerializable( INITIAL_STATUS_EXTRA );
        updateWithStatus( status );

    }

    public void updateWithStatus(FullStatus status) {
        positionTextView.setText( status.getTrip().getPositionSeaPortName() );
        destinationTextView.setText( status.getTrip().getDestinationSeaportName() );
        toDestinationTextView.setText( status.getTrip().getDestinationSeaportName() );
        String tripStatus = status.getTrip().getStatus();
        String tripStatusText = "";

        if (tripStatus.equals( Trip.status.GOING_TO_DESTINATION )) {
            tripStatusText = getString( R.string.trip_going_to_destination );
        } else if (tripStatus.equals( Trip.status.ARRIVED )) {
            tripStatusText = getString( R.string.trip_arrived );

            AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
            builder.setMessage( tripStatusText );
            builder.setPositiveButton( R.string.ok, null );
            builder.show();
        }

        tripStatusTextView.setText( tripStatus );
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

    private void checkLocationPermissinAndSetUpUserLocation() {
        if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {
            setUpUserLocation();

        } else {
            ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION );

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpUserLocation();
            } else {
                Toast.makeText( getActivity(), R.string.location_permission_needed, Toast.LENGTH_LONG ).show();
            }

        } else {
            super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        }
    }

    private void setUpUserLocation() {
        if (mMap != null)
            return;
        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled( true );
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient( getActivity() );
        locationClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    LatLng currentLatlng = new LatLng( location.getLatitude(), location.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom( currentLatlng, 16 );
                    mMap.moveCamera( update );
                }
            }
        } );

    }

    public void setDestinationMarker(LatLng target){
        if(mMap != null) return;;
        if(destinationMarker == null){
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.destination );
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( target);
            destinationMarker = mMap.addMarker( options );
        }else {
            destinationMarker.setPosition( target );
        }

    }

    public void setShipMarker(LatLng target){
        if(mMap != null) return;;
        if(shipMarker == null){
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.boat);
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( target);
            shipMarker= mMap.addMarker( options );
        }else {
            shipMarker.setPosition( target );
        }
    }

    public void removeMapLocationLayout(){
        if(ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled( false );
        }
    }

    public LatLng captureCenter(){
        if(mMap == null) return null;

        return mMap.getCameraPosition().target;
    }

    public void setPositionMarker(LatLng target){
        if(mMap != null) return;;
        if(positionMarker == null){
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.position );
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( target);
            positionMarker = mMap.addMarker( options );
        }else {
            positionMarker.setPosition( target );
        }
    }

    public void reset(){
        if(mMap != null) return;;
        mMap.clear();
        positionMarker= null;
        destinationMarker = null;
        shipMarker= null;
        setUpUserLocation();
    }

    public void showTripCurrentLocationOnMap(LatLng tripLatng){
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom( tripLatng, 16 );
        mMap.moveCamera( update );

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
      mMap = googleMap;
      checkLocationPermissinAndSetUpUserLocation();
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


