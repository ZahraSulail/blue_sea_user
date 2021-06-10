package com.barmej.bluesea.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.barmej.bluesea.Constants;
import com.barmej.bluesea.R;
import com.barmej.bluesea.domain.entity.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "FirebaseImageLoader";

    /*
     Define variabkes required in this activity
     */
    private ImageView userPhotoImageView;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneNoTextView;
    private Button updateButton;

    /*
     FirebaseAuth object
     */
    private FirebaseAuth mAuth;


    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_profile );

        /*
         Get an instance of FirebaseAuth
         */
        mAuth = FirebaseAuth.getInstance();

        /*
         Find views by ids and assigned to variables
         */
        userPhotoImageView = findViewById( R.id.image_view_user_photo );
        userNameTextView = findViewById( R.id.text_view_user_name );
        userEmailTextView = findViewById( R.id.text_view_user_email );
        userPhoneNoTextView = findViewById( R.id.text_view_user_phone );
        updateButton = findViewById(R.id.button_update);

        /*
         This method implemented directly when current uuser access his/her/ profile
         */
        loadUserInformation();

        /*
        click on updateButton to move to update user profile screen
         */

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /*
     This method get the current user profile information stored in the user table in FirebaseDatabse
     */
    private void loadUserInformation() {

        FirebaseDatabase.getInstance().getReference().child(Constants.USER_REF_PATH).child( mAuth.getCurrentUser().getUid() ).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue( User.class );
                Glide.with( userPhotoImageView ).load( user.getPhoto() ).into( userPhotoImageView );
                userNameTextView.setText( "" + " " + user.getName() );
                userEmailTextView.setText( "" + " " + user.getEmail() );
                userPhoneNoTextView.setText( "" + " " + user.getUserPhoneNo() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );


    }


}


