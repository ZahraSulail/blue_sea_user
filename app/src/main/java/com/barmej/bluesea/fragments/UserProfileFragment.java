package com.barmej.bluesea.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.barmej.bluesea.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class UserProfileFragment extends DialogFragment {

    private ImageView userPhotoImageView;
    private TextView userNameTextView;
    String userId;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference userReference;
    FirebaseAuth mAuth;
    FirebaseStorage storage;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_user_profile, container, false );
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        userPhotoImageView = view.findViewById( R.id.image_view_user_photo );
        userNameTextView = view.findViewById( R.id.text_view_user_name );

        database = FirebaseDatabase.getInstance();



    }


}










