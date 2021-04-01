package com.example.tripremenders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class RegistrationActivity extends AppCompatActivity {
    private final int GOOGLE_SIGN_IN = 123;
    private final String googleActivityTAG = "GoogleActivity";
    private final String facebookActivityTAG = "FacebookActivity";
    private static final String TAGEMAILSIGNIN = "TAGEMAILSIGNIN";
    private static final String TAGPASSWORDSIGNIN = "TAGPASSWORDSIGNIN";
    String email, password;
    private Button buttonSignUp, buttonSignIn;
    private TextInputLayout editTextEmail;
    private TextInputLayout editTextPassword;
    private ImageView googleLogin;
    private ImageView facebookLogin;
    private CallbackManager mCallbackManager;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            openHomeActivityWithoutExtra();
        } else {
            setContentView(R.layout.activity_registration);
            editTextEmail = findViewById(R.id.inputLayout_email);
            editTextPassword = findViewById(R.id.inputLayout_password);
            buttonSignIn = findViewById(R.id.btn_log_in);
            googleLogin = findViewById(R.id.gmail_login);
            facebookLogin = findViewById(R.id.facebook_login);
            buttonSignUp = findViewById(R.id.btn_sign_up);
            if (savedInstanceState != null) {
                editTextEmail.getEditText().setText(savedInstanceState.getString(TAGEMAILSIGNIN));
                editTextPassword.getEditText().setText(savedInstanceState.getString(TAGPASSWORDSIGNIN));
            }
            buttonSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =
                            new Intent(
                                    RegistrationActivity.this,
                                    SignUpActivity.class
                            );
                    startActivity(intent);
                }
            });

            // login using name and password
            buttonSignIn.setOnClickListener(v -> {

                email = String.valueOf(editTextEmail.getEditText().getText());
                password = String.valueOf(editTextPassword.getEditText().getText());

                if (email.isEmpty()) {
                    editTextEmail.setError("Enter your email");
                    editTextEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editTextPassword.setError("Enter your password");
                    editTextPassword.requestFocus();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    openHomeActivity();
                                } else {
                                    Toast.makeText(
                                            RegistrationActivity.this,
                                            task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                    editTextEmail.getEditText().setText("");
                                    editTextPassword.getEditText().setText("");
                                }
                            }
                        });
            });


            googleLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInGoogle();
                }
            });

            facebookLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    facebookSignIn();
                }
            });
        }
    }

    private void facebookSignIn() {
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance()
                .logInWithReadPermissions(
                        RegistrationActivity.this,
                        Arrays.asList("email", "public_profile")
                );

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(facebookActivityTAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(facebookActivityTAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(facebookActivityTAG, "facebook:onError", error);

            }
        });
    }

    private void signInGoogle() {
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i(googleActivityTAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.i(googleActivityTAG, "Google sign in failed", e);
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Google Authentication
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(facebookActivityTAG, "signInWithCredential:success");
                            openHomeActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i(googleActivityTAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(
                                    RegistrationActivity.this,
                                    "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Facebook Authentication
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(facebookActivityTAG, "signInWithCredential:success");
                            openHomeActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i(facebookActivityTAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(
                                    RegistrationActivity.this,
                                    "Authentication failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openHomeActivity() {

        Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
        intent.putExtra("getDataFromFirebase", true);
        startActivity(intent);
        finishAffinity();
    }

    private void openHomeActivityWithoutExtra() {
        Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        email = editTextEmail.getEditText().getText().toString();
        password = editTextPassword.getEditText().getText().toString();


        outState.putString(TAGEMAILSIGNIN, email);
        outState.putString(TAGPASSWORDSIGNIN, password);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);


    }

}



