package com.barmej.bluesea.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class LoginActivity extends AppCompatActivity {

    /*
     Reference of varailbels required in this activity
     */
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        /*
          find views by id and assigned them to the variables
         */
        emailTextInputLayout = findViewById(R.id.text_input_email);
        passwordTextInputLayout = findViewById(R.id.text_input_password);
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);
        progressBar = findViewById(R.id.progressBar);

        /*
         loginButton.setOnClickListener that handle logInClicke
         method to allow user access firebase account
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInClicked();

            }
        });

        /*
         Get an instance of FirebaseAuth and check if the user is exist
         */
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            hideForm(true);
            fetchUserProfileAndLogin(firebaseUser.getUid());
        }
    }

    /*
     logInClicked method: user login to firebase with email and password
     */
    private void logInClicked() {
        if (!isValidEmail(emailEditText.getText())) {
            emailTextInputLayout.setError(getString(R.string.invalid_email));
            return;
        }

        if (passwordEditText.getText().length() < 6) {
            passwordTextInputLayout.setError(getString(R.string.invalid_password_length));
            return;
        }
        hideForm(true);
        System.out.println("Try to login");
        //Signin firebase with email and password
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Response: Successful");
                            String userId = task.getResult().getUser().getUid();
                            fetchUserProfileAndLogin(userId);
                        } else {
                            System.out.println("Response: Not Successful");
                            hideForm(false);
                            Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());

    }

    /*
     fetchUserProfileAndLOgin method: if login successful user witll move
      to main Activity that shwos "TripListFragment" list of trips available
     */
    private void fetchUserProfileAndLogin(String userId) {
        System.out.println("Response: fetch profile");
        TripManager.getInstance().getUserProfile(userId, new CallBack() {
            @Override
            public void onComplete(boolean isSuccessful) {
                if (isSuccessful) {
                    System.out.println("Response: Successfull");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.no_user, Toast.LENGTH_LONG).show();
                    hideForm(false);
                }

            }
        });
    }

    private void hideForm(boolean hide){
        if(hide){
            progressBar.setVisibility(View.VISIBLE);
            emailTextInputLayout.setVisibility(View.GONE);
            emailEditText.setVisibility(View.GONE);
            passwordTextInputLayout.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);

        }else{
            progressBar.setVisibility(View.GONE);
            emailTextInputLayout.setVisibility(View.VISIBLE);
            emailEditText.setVisibility(View.VISIBLE);
            passwordTextInputLayout.setVisibility(View.VISIBLE);
            passwordEditText.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);

        }
    }

}

