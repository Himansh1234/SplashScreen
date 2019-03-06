package com.example.vsvll.splashscreen;

import android.Manifest;
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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vsvll.splashscreen.Data.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Profile extends AppCompatActivity implements LocationListener {
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    LocationManager locationManager;
    TextView place;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        TextView username = findViewById(R.id.profile_username);
        TextView email = findViewById(R.id.profile_email);
        place = findViewById(R.id.profile_place);
        ImageView pic = findViewById(R.id.profile_pic);

        Button history = findViewById(R.id.profile_history);
        Button logout = findViewById(R.id.profile_logout);
        Button  changeCity = findViewById(R.id.profile_changeCity);

        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        Glide.with(this).load(user.getPhotoUrl()).into(pic);
        username.setText(user.getDisplayName());
        email.setText(user.getEmail());

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        place.setText(pref.getString("city","ABC").toUpperCase());



        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, History.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });

        changeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Profile.this)
                        .setMessage("CHANGE CITY")
                        .setCancelable(false)
                        .setPositiveButton("Current City", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getLocation();
                            }
                        })
                        .setNegativeButton("Enter Manually", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LayoutInflater layoutInflater = Profile.this.getLayoutInflater();
                                final View view1 = layoutInflater.inflate(R.layout.location_dialog,null);
                                    new AlertDialog.Builder(Profile.this)
                                            .setView(view1)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            EditText city1 = view1.findViewById(R.id.dialog_city);
                                            if(city1.getText().equals(""))
                                            {
                                                Toast.makeText(getApplicationContext(),"Enter Valid City",Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            place.setText(city1.getText().toString().toUpperCase());
                                            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                                            editor.putString("city",city1.getText().toString().toLowerCase());
                                            editor.commit();
                                        }
                                    }).show();
                            }
                        })
                        .show();
            }
        });

    }

    void Logout(){
        mAuth.signOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(Profile.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                        finish();
                    }
                });
    }

    void getLocation(){

        checkLocation();

    }


    private void checkLocation() {
        if ( ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);
            if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

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

        }
        else {
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

                    if( loc_fine)
                    {

                    } else {
                        Toast.makeText(this,"Please Give Permission",Toast.LENGTH_SHORT).show();

                    }
                }

                break;
        }
    }
    @Override
    public void onLocationChanged(Location location) {

        TextView city = findViewById(R.id.profile_place);

        String cityName=null;
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
        place.setText(cityName);
        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
        editor.putString("city", cityName);
        editor.commit();

        locationManager.removeUpdates(this);

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
}
