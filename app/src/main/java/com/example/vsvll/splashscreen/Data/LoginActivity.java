package com.example.vsvll.splashscreen.Data;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vsvll.splashscreen.MainActivity;
import com.example.vsvll.splashscreen.R;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    ProgressBar progressBar;

    private static final int RC_SIGN_IN = 7;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        findViewById(R.id.email_sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.email_sign_up_button:
                        EsignUp();
                        break;
                }
            }
        });

        findViewById(R.id.email_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.email_sign_in_button:
                        EsignIn();
                        break;
                }
            }
        });

        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.google_sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void EsignIn() {

        String Email = email.getText().toString().trim();
        String Pass = password.getText().toString().trim();

        if (Email.isEmpty()) {
            email.setError("Enter Email address");
            email.setFocusable(true);
            return;
        }

        if (Pass.isEmpty()) {
            password.setError("Enter Password");
            password.setFocusable(true);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Enter valid Email address");
            email.setFocusable(true);
            return;

        }

        if (Pass.length() < 6) {
            password.setError("Enter a password of length at least 6 ");
            password.setFocusable(true);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Sign in Success", Toast.LENGTH_LONG).show();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Sign in failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });


    }

    private void EsignUp() {

        String Email = email.getText().toString().trim();
        String Pass = password.getText().toString().trim();

        if (Email.isEmpty()) {
            email.setError("Enter Email address");
            email.setFocusable(true);
            return;
        }

        if (Pass.isEmpty()) {
            password.setError("Enter Password");
            password.setFocusable(true);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Enter valid Email address");
            email.setFocusable(true);
            return;

        }

        if (Pass.length() < 6) {
            password.setError("Enter a password of length at least 6 ");
            password.setFocusable(true);
            return;

        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "SignUP Successful.",
                                    Toast.LENGTH_SHORT).show();
                                    EsignIn();


                        } else {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "SignUP failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getUid() != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        } else {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d("TAG", "signInWithCredential:success");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Sign in Success", Toast.LENGTH_LONG).show();


                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Sign in Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}