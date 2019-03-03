package com.example.vsvll.splashscreen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    DatabaseReference dr;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser user;
    RecyclerView recyclerView;
    NewsFeed_Adapter newsFeed_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        user = mAuth.getCurrentUser();
        assert user != null;

        FirebaseDatabase fd ;
        dr = FirebaseDatabase.getInstance().getReference("post");

        dr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dataSnapshot.getKey();

                dr.child(dataSnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                       String t = dataSnapshot.getKey();
                       if(t.contains(user.getUid())){
                           String id = dataSnapshot.getKey();
                           String username = dataSnapshot.child("detail").child("username").getValue(String.class);
                           String time = dataSnapshot.child("detail").child("time").getValue(String.class);
                           String name = dataSnapshot.child("detail").child("name").getValue(String.class);
                           String place = dataSnapshot.child("detail").child("place").getValue(String.class);
                           String city = dataSnapshot.child("detail").child("city").getValue(String.class);
                           String img = dataSnapshot.child("img").child("1").getValue(String.class);

                           Post_details p = new Post_details(id,username,name,img,place,city,time+"");
                           newsFeed_adapter.update(p);
                       }
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


        recyclerView = findViewById(R.id.history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        newsFeed_adapter = new NewsFeed_Adapter(getApplicationContext(),recyclerView,new ArrayList<Post_details>());
        recyclerView.setAdapter(newsFeed_adapter);
    }
}
