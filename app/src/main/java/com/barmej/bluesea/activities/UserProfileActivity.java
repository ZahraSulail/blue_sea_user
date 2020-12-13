package com.barmej.bluesea.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.barmej.bluesea.R;
import com.barmej.bluesea.domain.entity.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseImageLoader";
    private ImageView userPhotoImageView;
    private TextView userNameTextView;
    private FirebaseAuth mAuth;



    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_profile );


        mAuth = FirebaseAuth.getInstance();
        userPhotoImageView = findViewById( R.id.image_view_user_photo );
        userNameTextView = findViewById( R.id.text_view_user_name );
        loadUserInformation();




    }


    private void loadUserInformation(){

       FirebaseDatabase.getInstance().getReference().child( "Users" ).child( mAuth.getCurrentUser().getUid() ).addValueEventListener( new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
              User user = snapshot.getValue(User.class);
               Glide.with(userPhotoImageView).load( user.getPhoto()).into( userPhotoImageView ) ;
               userNameTextView.setText( user.getName());
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });






}}


