package com.barmej.bluesea.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.barmej.bluesea.R;
import com.barmej.bluesea.fragments.TripListFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    /*
      TripListFragment Object
     */
    TripListFragment mTripListFragment;

    /*
     FirebaseAuth object
     */
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
        FragmentManager to find Fragment by id
        */
        FragmentManager manager = getSupportFragmentManager();
        mTripListFragment = (TripListFragment) manager.findFragmentById(R.id.trip_list_container);

        /*
         Get an instance of FirebaseAuth
         */
        mAuth = FirebaseAuth.getInstance();

    }

    /*
     Menu allows user to access profile and logout from firebase
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_user_profile) {
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
            return true;
        } else {
            if (id == R.id.action_log_out) {
                if (mAuth.getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);
                    return true;
                }
            }
            return super.onOptionsItemSelected(item);
        }
    }
}




