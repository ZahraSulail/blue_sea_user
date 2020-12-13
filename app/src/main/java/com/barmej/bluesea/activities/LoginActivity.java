package com.barmej.bluesea.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.barmej.bluesea.R;
import com.barmej.bluesea.callback.CallBack;
import com.barmej.bluesea.domain.TripManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_log_in);

        emailTextInputLayout = findViewById( R.id.text_input_email );
        passwordTextInputLayout = findViewById( R.id.text_input_password );
        emailEditText = findViewById( R.id.edit_text_email );
        passwordEditText = findViewById( R.id.edit_text_password );
        loginButton = findViewById( R.id.button_login );



        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInClicked();
                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                finish();
            }
        } );

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            fetchUserProfileAndLogin( firebaseUser.getUid());
        }
    }


    private void logInClicked() {
        if(!isValidEmail(emailEditText.getText())){
            emailTextInputLayout.setError( getString( R.string.invalid_email));
            return;
        }

        if(passwordEditText.getText().length()< 6){
            passwordTextInputLayout.setError(getString( R.string.invalid_password_length ) );

        }else {
           FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword( emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()) {
                               Toast.makeText( LoginActivity.this, R.string.log_in_successfull, Toast.LENGTH_SHORT ).show();
                               String userId = task.getResult().getUser().getUid();
                               fetchUserProfileAndLogin( userId );
                           }else{
                               Toast.makeText( LoginActivity.this, R.string.login_error, Toast.LENGTH_LONG).show();
                           }
                        }
                    } );
        }
    }

    public static boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher( target ).matches());

    }

    private void fetchUserProfileAndLogin(String userId){
        TripManager.getInstance().getUserProfile( userId, new CallBack() {
            @Override
            public void onComplete(boolean isSuccessful) {
                if(isSuccessful){
                    startActivity(new Intent( getApplicationContext(), MainActivity.class) );
                    finish();

                }else{
                    Toast.makeText( LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT ).show();
                }

            }
        } );
    }

    }

