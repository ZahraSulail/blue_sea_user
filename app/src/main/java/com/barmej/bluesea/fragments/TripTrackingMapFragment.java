package com.barmej.bluesea.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barmej.bluesea.R;
import com.barmej.bluesea.callback.PermissionFailListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class TripTrackingMapFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private Marker startPointMarker;
    private Marker destinationMarker;
    private Marker currentTripLocation;
    private PermissionFailListener permissionFailListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_tracking_map, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.tracking_map );
        if(mapFragment != null){
            mapFragment.getMapAsync( this );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
     mMap = googleMap;

        checkLocationPermissionAndSetUpUserLocation();
    }

    private void checkLocationPermissionAndSetUpUserLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setupUserLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @SuppressLint("MissingPermission")
    private void setupUserLocation() {
        if (mMap == null) return;
        mMap.setMyLocationEnabled(true);
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f);
                    mMap.moveCamera(update);
                }
            }
        });


    }

    public void removeMapLocationLayout(){
        if(mMap == null) return;
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled( true );
        }
    }

    public void showTripCurrentLocation(LatLng currentLocation){
        if(mMap == null){
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom( currentLocation, 16 );
            mMap.moveCamera( update );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (permissions.length == 1 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupUserLocation();
            } else {
                if(permissionFailListener != null){
                    permissionFailListener.onPermissionFail();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public LatLng captureCenter() {
        if (mMap == null) return null;
        return mMap.getCameraPosition().target;
    }

    public void setStartPointMarker(LatLng target) {
        if (mMap == null) return;
        if (startPointMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.position);
            MarkerOptions options = new MarkerOptions();
            options.icon(descriptor);
            options.position(target);
            startPointMarker = mMap.addMarker(options);
        } else {
            startPointMarker.setPosition(target);
        }
    }

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
        public void reset() {
            if (mMap == null) return;
            mMap.clear();
            startPointMarker= null;
            destinationMarker = null;
            currentTripLocation = null;
            setupUserLocation();
        }

    public void showTripCurrentLocationOnMap(LatLng tripLatlng){
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom( tripLatlng, 16 );
        mMap.moveCamera( update );
    }

    public void setOnPermissionFailListener(PermissionFailListener permissionFailListener){
        this.permissionFailListener = permissionFailListener;
    }

}




