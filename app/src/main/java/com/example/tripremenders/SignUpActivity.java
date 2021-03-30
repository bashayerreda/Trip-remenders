package com.example.tripremenders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
public class SignUpActivity extends AppCompatActivity {

    private final int GOOGLE_SIGN_IN = 123;

    private static final String googleActivityTAG = "GoogleActivity";
    private static final String facebookActivityTAG = "FacebookActivity";

    private CallbackManager mCallbackManager;

    private Button signUp;
    private Button login;
    private TextInputLayout inputLayoutUsername;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutConfirmPassword;
    private FirebaseAuth firebaseAuth;
    private ImageView googleSignUp, facebookSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        login = findViewById(R.id.btn_sign_in);
        googleSignUp = findViewById(R.id.gmail_login);
        facebookSignUp = findViewById(R.id.facebook_login);
        inputLayoutUsername = findViewById(R.id.inputLayout_username);
        inputLayoutEmail = findViewById(R.id.inputLayout_email);
        inputLayoutPassword = findViewById(R.id.inputLayout_password);
        inputLayoutConfirmPassword = findViewById(R.id.inputLayout_confirmPassword);
        signUp = findViewById(R.id.btn_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        googleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpGoogle();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUsingEmailPassword();

            }

        });

        facebookSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookSignUp();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void facebookSignUp() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(googleActivityTAG, "facebook:onSuccess:" + loginResult);
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

    private void signUpUsingEmailPassword() {

        final String username =String.valueOf(inputLayoutUsername.getEditText().getText());
        final String email =String.valueOf(inputLayoutEmail.getEditText().getText());
        final String password = String.valueOf(inputLayoutPassword.getEditText().getText());
        final String confirmPassword = String.valueOf(inputLayoutConfirmPassword.getEditText().getText());

        if (username.isEmpty()) {
            inputLayoutUsername.setError("enter username");
            inputLayoutUsername.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            inputLayoutEmail.setError("enter email address");
            inputLayoutEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputLayoutEmail.setError("please match address");
            inputLayoutEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            inputLayoutPassword.setError("enter password");
            inputLayoutPassword.requestFocus();
            return;
        }
        if (!(confirmPassword.equals(password))) {
            inputLayoutConfirmPassword.setError("match password");
            inputLayoutConfirmPassword.requestFocus();
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    openHomeActivity();
                } else {
                    inputLayoutUsername.getEditText().setText("");
                    inputLayoutEmail.getEditText().setText("");
                    inputLayoutPassword.getEditText().setText("");
                    inputLayoutConfirmPassword.getEditText().setText("");

                }
            }
        });


    }


        private void signUpGoogle() {
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
                                    SignUpActivity.this,
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
                                    SignUpActivity.this,
                                    "Authentication failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void openHomeActivity() {

        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        intent.putExtra("getDataFromFirebase", true);
        startActivity(intent);
        finishAffinity();
    }


}