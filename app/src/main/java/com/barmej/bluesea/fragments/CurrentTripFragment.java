package com.barmej.bluesea.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barmej.bluesea.R;
import com.barmej.bluesea.domain.entity.FullStatus;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class CurrentTripFragment extends Fragment implements OnMapReadyCallback {
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String MAPVIEW_BUNDLE_KEY = "mapViewBundleKey";
    public static final String INITIAL_STATUS_EXTRA = "initial_status_extra";

    public static  CurrentTripFragment getInstance(FullStatus status) {
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
        positionTextView = view.findViewById( R.id.text_view_position );
        destinationTextView = view.findViewById( R.id.text_view_destination );
        mLinearLayout = view.findViewById( R.id.linearLayout );
        tripStatusTextView = view.findViewById( R.id.text_view_trip_status );
        toDestinationTextView = view.findViewById( R.id.text_view_to_destination );

        mapView = view.findViewById( R.id.map_view );
        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle( MAPVIEW_BUNDLE_KEY );
        }


        if (mapView != null) {
            mapView.onCreate( mapViewBundle );
            mapView.getMapAsync( this );
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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


