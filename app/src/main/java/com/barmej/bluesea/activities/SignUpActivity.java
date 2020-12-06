package com.barmej.bluesea.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.barmej.bluesea.R;
import com.barmej.bluesea.domain.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private static final int PERMISSION_REQUEST_READ_STORAGE = 1;
    private static final int REQUEST_GET_PHOTO = 2;
    private boolean mReadStoragePermissionGranted;

    private ConstraintLayout mConstraintLayout;

    private ImageView userPhotoImageView;
    private ImageView uploadImageView;
    private TextInputLayout userNameTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText userNameTextInputEditText;
    private TextInputEditText emailTextInputEditText;
    private TextInputEditText passwordTextInputEditText;
    private MaterialButton createAccountButton;
    private MaterialButton haveAccountButton;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private Uri mUserPhotoUri;

    private DatabaseReference databaseReference;
    private static final String USERS = "users";
    String userName;
    String email;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );
        userPhotoImageView = findViewById( R.id.image_view_user_photo );
        uploadImageView = findViewById( R.id.image_view_upload );
        userNameTextInputLayout = findViewById( R.id.text_input_user_name );
        emailTextInputLayout = findViewById( R.id.text_input_email );
        passwordTextInputLayout = findViewById( R.id.text_input_password );
        userNameTextInputEditText = findViewById( R.id.edit_text_user_name );
        emailTextInputEditText = findViewById( R.id.edit_text_email );
        passwordTextInputEditText = findViewById( R.id.edit_text_password );
        createAccountButton = findViewById( R.id.button_create_account );
        haveAccountButton = findViewById( R.id.button_have_account );

        mAuth = FirebaseAuth.getInstance();
        requestExternalStoragePermission();

        uploadImageView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanchGalleryIntent();
            }
        } );


        if (mAuth.getCurrentUser() != null) {
            startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
            finish();
        }

        createAccountButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userNameTextInputEditText.getText().toString();
                email = emailTextInputEditText.getText().toString();
                password = passwordTextInputEditText.getText().toString();


                if (!isValidEmail( emailTextInputEditText.getText() )) {
                    emailTextInputEditText.setText( R.string.invalid_email );
                    return;
                }

                if (TextUtils.isEmpty( userName )) {
                    userNameTextInputLayout.setError( getString( R.string.user_name_is_required ) );
                    return;
                }

                if (TextUtils.isEmpty( password )) {
                    passwordTextInputLayout.setError( getString( R.string.password_is_required ) );
                    return;
                }

                if (passwordTextInputEditText.getText().length() < 6) {
                    passwordTextInputLayout.setError( getString( R.string.password_must_be_6_digits_or_more ) );
                    return;
                }

                if(mUserPhotoUri != null ){
                    createAccount();
                }else{
                    Toast.makeText( SignUpActivity.this, R.string.image_is_required, Toast.LENGTH_SHORT ).show();
                }

            }
        } );

        haveAccountButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                finish();
            }
        } );
    }

    private void createAccount() {


        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference photoStorageReference = storageReference.child( UUID.randomUUID().toString());
        photoStorageReference.putFile( mUserPhotoUri ).addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
               if(task.isSuccessful()){
                   photoStorageReference.getDownloadUrl().addOnCompleteListener( new OnCompleteListener<Uri>() {
                       @Override
                       public void onComplete(@NonNull Task<Uri> task) {
                           if(task.isSuccessful()){
                               final User user = new User();
                               user.setId( user.getId() );
                               user.setUserName( userNameTextInputEditText.getText().toString()) ;
                               user.setEmail( emailTextInputEditText.getText().toString());
                               user.setPassword( passwordTextInputEditText.getText().toString());
                               user.setAssignedTrip( user.getAssignedTrip());

                               mAuth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                   @Override
                                   public void onComplete(@NonNull Task<AuthResult> task) {
                                       if (task.isSuccessful()) {
                                           Toast.makeText( SignUpActivity.this, R.string.acconte_is_created, Toast.LENGTH_SHORT ).show();
                                           startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                                           finish();
                                       } else {
                                           Toast.makeText( SignUpActivity.this, R.string.error, Toast.LENGTH_SHORT ).show();
                                       }
                                   }
                               } );
                           }
                       }
                   } );
               }
            }
        } );
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty( target ) && Patterns.EMAIL_ADDRESS.matcher( target ).matches());

    }


    private void requestExternalStoragePermission() {
        mReadStoragePermissionGranted = false;
        if (ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED) {
            mReadStoragePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE );
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
            mReadStoragePermissionGranted = false;
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                mReadStoragePermissionGranted = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == REQUEST_GET_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    mUserPhotoUri = data.getData();
                    userPhotoImageView.setImageURI( mUserPhotoUri );
                } catch (Exception e) {
                    Snackbar.make( mConstraintLayout, R.string.photo_Selection_error, Snackbar.LENGTH_LONG ).show();
                }
            }
        }
    }

    private void lanchGalleryIntent() {
        Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
        intent.addCategory( Intent.CATEGORY_OPENABLE );
        intent.setType( "image/*" );
        startActivityForResult( Intent.createChooser( intent, getString( R.string.choose_photo ) ), REQUEST_GET_PHOTO );
    }
}