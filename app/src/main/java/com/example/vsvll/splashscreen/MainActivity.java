package com.example.vsvll.splashscreen;

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
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener, NavigationView.OnNavigationItemSelectedListener {


    ImageView DisplayImage;
    TextView Name, Email;
    FirebaseAuth mAuth;
    FirebaseDatabase fb;
    DatabaseReference dr;
    GoogleSignInClient mGoogleSignInClient;

    RecyclerView recyclerView;
    NewsFeed_Adapter newsFeed_adapter;

    LocationManager locationManager;
    private ProgressDialog pd;

    String CityName="";

    ArrayList<String> Cityarray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        if(pref.getString("city","Z123").equals("Z123")){
            getLocation();
        }else{
            CityName=pref.getString("city","Z123").toLowerCase();
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        Name = header.findViewById(R.id.Name);
        Email = header.findViewById(R.id.Email);
        DisplayImage = header.findViewById(R.id.DisplayPic);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        Glide.with(this).load(user.getPhotoUrl()).into(DisplayImage);
        Name.setText(user.getDisplayName());
        Email.setText(user.getEmail());


        recyclerView = findViewById(R.id.post_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        newsFeed_adapter = new NewsFeed_Adapter(getApplicationContext(),recyclerView,new ArrayList<Post_details>());
        recyclerView.setAdapter(newsFeed_adapter);


        getData();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item= menu.findItem(R.id.upload_main);

      ImageButton imageButton = (ImageButton) item.getActionView();
      imageButton.setBackgroundResource(R.drawable.ic_add_white_24dp);
      imageButton.setMinimumWidth(120);
      imageButton.setMaxWidth(120);
      imageButton.setImageResource(R.drawable.ic_add_white_24dp);
      imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this,Upload_post.class);
                startActivity(in);
                finish();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_blood_req) {
            Intent intent = new Intent(MainActivity.this, Blood_Donation.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
           Intent intent = new Intent(MainActivity.this, Event.class);
            startActivity(intent);
        } else if (id == R.id.nav_donation) {
            Intent intent = new Intent(MainActivity.this, Donation.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, Profile.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_contact) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    void getData(){

        fb = FirebaseDatabase.getInstance();
        dr = fb.getReference("post");




            dr.child(CityName).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    try {
                        wait(100);
                    } catch (Exception e) {
                    }

                    String id = dataSnapshot.getKey();
                    String username = dataSnapshot.child("detail").child("username").getValue(String.class);
                    String time = dataSnapshot.child("detail").child("time").getValue(String.class);
                    String name = dataSnapshot.child("detail").child("name").getValue(String.class);
                    String place = dataSnapshot.child("detail").child("place").getValue(String.class);
                    String city = dataSnapshot.child("detail").child("city").getValue(String.class);
                    String img = dataSnapshot.child("img").child("1").getValue(String.class);

                    Post_details p = new Post_details(id, username, name, img, place, city, time + "");
                    newsFeed_adapter.update(p);
                    newsFeed_adapter.notifyDataSetChanged();


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    newsFeed_adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



    }



    void getLocation() {

        checkLocation();

    }


    private void checkLocation() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

                        pd = new ProgressDialog(MainActivity.this);
                        pd.setMessage("loading...");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

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

        CityName=cityName.toLowerCase();
        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
        editor.putString("city", cityName);
        editor.commit();
        pd.dismiss();
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

    @Override
    protected  void onRestart() {

        super.onRestart();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        if(pref.getString("city","Z123").equals("Z123")){
            getLocation();
        }else{

            if( !pref.getString("city","Z123").equals(CityName)) {
                CityName = pref.getString("city", "Z123").toLowerCase();
                newsFeed_adapter.posts.clear();
                newsFeed_adapter.notifyDataSetChanged();
                getData();
            }
        }
    }

    @Override
    protected  void onStart() {

        super.onStart();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        if(pref.getString("city","Z123").equals("Z123")){
            getLocation();
        }else{

            if( !pref.getString("city","Z123").equals(CityName)) {
                CityName = pref.getString("city", "Z123").toLowerCase();
                newsFeed_adapter.posts.clear();
                newsFeed_adapter.notifyDataSetChanged();


                fb = FirebaseDatabase.getInstance();
                dr = fb.getReference("post");


                dr.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Cityarray.add(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(Cityarray.contains(CityName))
                getData();
                else
                    Toast.makeText(getApplicationContext(),"No Data Avaliable",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
