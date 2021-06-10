package com.barmej.bluesea.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.barmej.bluesea.Constants;
import com.barmej.bluesea.R;
import com.barmej.bluesea.domain.entity.Trip;
import com.barmej.bluesea.domain.entity.User;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class TripDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = TripDetailsActivity.class.getSimpleName();
    private TextView mDateTextView;
    private TextView mPositionTextView;
    private TextView mDestinationTextView;
    private TextView mAvailableSeatsTextView;
    private Button mCancelButton;
    private Button mBookButton;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private Bundle mapViewBundle;
    private TextView tripStatusTextView;
    private DatabaseReference databaseReference;
    private Trip mTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        /*
         Find views by ids and assigned them to variables
         */
        /*
         Define required variables
         */
        CardView mCardView = findViewById(R.id.card_view);
        mDateTextView = findViewById(R.id.det_text_view_date);
        mPositionTextView = findViewById(R.id.det_text_view_position);
        mDestinationTextView = findViewById(R.id.det_text_view_destination);
        mAvailableSeatsTextView = findViewById(R.id.det_text_available_seats);
        mBookButton = findViewById(R.id.button_book);
        mCancelButton = findViewById(R.id.button_cancel);
        tripStatusTextView = findViewById(R.id.text_view_trip_status);

        /*
         Get instance of DatabseReference
         */
        databaseReference = FirebaseDatabase.getInstance().getReference();

        /*
         Find mapView by id and save it in saveInstance
         */
        mapView = findViewById(R.id.map);
        mapViewBundle = null;

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }

        /*
         Get Trip data from TripListFragment and set information on the views
         */
        mTrip = (Trip) getIntent().getSerializableExtra(Constants.TRIP_DATA);

        if (mTrip != null) {
            mDateTextView.setText(mTrip.getFormattedDate());
            mPositionTextView.setText(mTrip.getStartPortName());
            mDestinationTextView.setText(mTrip.getDestinationSeaportName());
            mAvailableSeatsTextView.setText(String.valueOf(mTrip.getAvailableSeats()));
        } else {
            finish();
            return;
        }

        databaseReference.child(Constants.USER_REF_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                HashMap<String, Object> trips = user.getTrips();
                if (mTrip.getStatus().equals(Trip.Status.GOING_TO_DESTINATION.name())) {
                    // Show on the way label
                    // Hide reserve button
                    Log.d(TAG, "onCreate: GOING_TO_DESTINATION");
                    mCancelButton.setVisibility(View.GONE);
                    mBookButton.setVisibility(View.GONE);
                    tripStatusTextView.setVisibility(View.VISIBLE);
                } else if (mTrip.getStatus().equals(Trip.Status.ARRIVED.name())) {
                    // Show arrived label
                    // Hide reserve button
                    Log.d(TAG, "onCreate: ARRIVED");
                    mCancelButton.setVisibility(View.GONE);
                    mBookButton.setVisibility(View.GONE);
                    tripStatusTextView.setVisibility(View.VISIBLE);
                    tripStatusTextView.setText("Arrived");
                } else {
                    // Show the reserve button
                    Log.d(TAG, "onCreate: AVAILABLE");
                    if (trips.get(mTrip.getId()) == null) {
                        mBookButton.setVisibility(View.VISIBLE);
                        mCancelButton.setVisibility(View.GONE);
                    } else {
                        mBookButton.setVisibility(View.GONE);
                        mCancelButton.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
         Call getMapAsync() to register the callback.
         */
        if (mapView != null) {
            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);
        }

        /*
         Click this button to implement bookTrip method
         */
        mBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookTrip();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTrip();
            }

        });
    }


    /*
     Save state of the map
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Bundle to save state of the map
        mapViewBundle = outState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(Constants.MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    /*
     onMapReady() to display the trip positions on the map and show its markers
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        if (mTrip.getCurrentLat() != 0 && mTrip.getCurrentLng() != 0) {
            LatLng currentLatLng = new LatLng(mTrip.getCurrentLat(), mTrip.getCurrentLng());
            googleMap.addMarker(new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.boat)));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16);
            googleMap.moveCamera(cameraUpdate);


        }

        if (mTrip.getStartLat() != 0 && mTrip.getStartLng() != 0) {
            LatLng positionLatLng = new LatLng(mTrip.getStartLat(), mTrip.getStartLng());
            googleMap.addMarker(new MarkerOptions().position(positionLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.position)));


        }

        if (mTrip.getDestinationLat() != 0 && mTrip.getDestinationLng() != 0) {

            LatLng destinationLatng = new LatLng(mTrip.getDestinationLat(), mTrip.getDestinationLng());
            googleMap.addMarker(new MarkerOptions().position(destinationLatng).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination)));
        }
    }

    /*
     Map lifecycle methods
     */
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

    /*
     bookTrip method allow user to reserve one trip at a time and save it to Fierbase RealTime Databse
     */
    public void bookTrip() {

        databaseReference.child(Constants.USER_REF_PATH).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                // Check if current user has another trip at the same time of current trip
                assert user != null;
                HashMap<String, Object> trips = user.getTrips();
                if (trips.get(mTrip.getId()) == null) {
                    boolean isTimeFree = true;
                    Iterator it = trips.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        String date = entry.getValue().toString();
                        if (date.equals(mTrip.getFormattedDate())) {
                            isTimeFree = false;
                            // If you have reserved another trip at the same time
                            Toast.makeText(TripDetailsActivity.this, R.string.you_have_trip_at_the_same_time, Toast.LENGTH_SHORT).show();
                        }
                    }

                    // Check if current time is free to reserve a trip
                    if (isTimeFree) {

                        // getvailableSeats object
                        int availableSeats = mTrip.getAvailableSeats();

                        //Check if there is atleast one available seat to be reserve
                        if (availableSeats != 0) {

                            // Update trip available seats and booked seats and insert new values to the firebse database
                            mTrip.setAvailableSeats(mTrip.getAvailableSeats() - 1);
                            mTrip.setBookedSeats(mTrip.getBookedSeats() + 1);

                            System.out.println("Trip ID " + mTrip.getId());

                            databaseReference.child(Constants.TRIP_REF_PATH)
                                    .child(mTrip.getId())
                                    .setValue(mTrip)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mAvailableSeatsTextView.setText(String.valueOf(mTrip.getAvailableSeats()));
                                            trips.put(mTrip.getId(), String.valueOf(mTrip.getFormattedDate()));

                                            // Update Users table in firebase and insert trips child to save erversed trips by the current user
                                            databaseReference.child(Constants.USER_REF_PATH)
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("trips")
                                                    .setValue(trips)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //Change reserve button status

                                                            mBookButton.setVisibility(View.GONE);
                                                            mCancelButton.setVisibility(View.VISIBLE);

                                                            Toast.makeText(TripDetailsActivity.this, R.string.seat_is_reserved, Toast.LENGTH_SHORT).show();

                                                        }
                                                    });
                                        }
                                    });

                        } else {
                            Toast.makeText(TripDetailsActivity.this, R.string.book_trip, Toast.LENGTH_SHORT).show();
                        }

                    }

                } else {
                    //Trip is already reserved
                    Toast.makeText(TripDetailsActivity.this, R.string.trip_is_reserved, Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void cancelTrip() {

        databaseReference.child(Constants.USER_REF_PATH).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                // Check if current user has another trip at the same time of current trip
                assert user != null;
                HashMap<String, Object> trips = user.getTrips();
                if (trips.get(mTrip.getId()) != null) {
                    trips.remove(mTrip.getId());
                    Toast.makeText(TripDetailsActivity.this, mTrip.getId(), Toast.LENGTH_SHORT).show();
                    databaseReference.child(Constants.USER_REF_PATH).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("trips").setValue(trips).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            mTrip.setAvailableSeats(mTrip.getAvailableSeats() + 1);
                            mTrip.setBookedSeats(mTrip.getBookedSeats() - 1);

                            databaseReference.child(Constants.TRIP_REF_PATH)
                                    .child(mTrip.getId())
                                    .setValue(mTrip)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            mAvailableSeatsTextView.setText(String.valueOf(mTrip.getAvailableSeats()));
                                            mBookButton.setVisibility(View.VISIBLE);
                                            mCancelButton.setVisibility(View.GONE);
                                            Toast.makeText(TripDetailsActivity.this, "Canceled!", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TripDetailsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}

