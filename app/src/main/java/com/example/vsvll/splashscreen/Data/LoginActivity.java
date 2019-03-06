package com.example.vsvll.splashscreen.Data;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vsvll.splashscreen.MainActivity;
import com.example.vsvll.splashscreen.Profile;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements LocationListener {

    EditText email;
    EditText password;
    ProgressBar progressBar;

    private static final int RC_SIGN_IN = 7;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    LocationManager locationManager;

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
                            new AsyncAction().execute(null, null, null);

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

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Sign in Success", Toast.LENGTH_LONG).show();

                            new AsyncAction().execute(null, null, null);

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


    void getLocation() {

        checkLocation();

    }


    private void checkLocation() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
              locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                new AlertDialog.Builder(this)
                        .setMessage("Please activate your GPS Location!")
                        .setCancelable(false)
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    boolean loc_fine = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (loc_fine) {

                    } else {
                        Toast.makeText(this, "Please Give Permission", Toast.LENGTH_SHORT).show();

                    }
                }

                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        String cityName = null;
        Geocoder gcd = new Geocoder(getApplicationContext(),
                Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location
                    .getLongitude(), 1);
            if (addresses.size() > 0) {
                cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
        editor.putString("city", cityName);
        editor.commit();

        locationManager.removeUpdates(this);

        //startActivity(new Intent(LoginActivity.this, MainActivity.class));


    }

    @Override
    public void onProviderDisabled(String provider) {
        //  Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        //  Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.d("Latitude","status");
    }


    private class AsyncAction extends AsyncTask<String, Void, String> {
        public boolean status = false;
        private ProgressDialog pd;
        LocationListener locationListener;

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            try {
                getLocation();
                status = true;
            } catch (Exception e) {
                // TODO: handle exception
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            pd.dismiss();

            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("loading...");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }

    }

}