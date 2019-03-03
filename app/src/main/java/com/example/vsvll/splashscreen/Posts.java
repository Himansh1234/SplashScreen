package com.example.vsvll.splashscreen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vsvll.splashscreen.Data.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;

public class Posts extends AppCompatActivity {

    FirebaseDatabase fb;
    DatabaseReference dr;
    TextView title,username,time,desc,place;
    ViewPager viewPager;
    ViewPageAdapter adapter;
    private FirebaseListAdapter<ChatMessage> comment_adapter;
    String postid,city;
   public ArrayList<String>img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        img = new ArrayList<>();
         title = findViewById(R.id.postdetail_title);
        time = findViewById(R.id.postdetail_time);
        place = findViewById(R.id.postdetail_place);
        desc = findViewById(R.id.postdetail_desc);
        username = findViewById(R.id.postdetail_username);

        Intent in = getIntent();
        postid = in.getStringExtra("post");
        city = in.getStringExtra("city");


        getData(postid);

        viewPager = findViewById(R.id.view_pager);



        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference("post").child(city).child(postid).child("comment")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                input.setText("");
            }
        });

        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        comment_adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.comment, FirebaseDatabase.getInstance().getReference("post").child(city).child(postid).child("comment").orderByChild("messageTime")) {

            @Override
            protected void populateView(View v, ChatMessage model, final int position) {
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);



                // Set their text
                messageText.setText(getItem(position).getMessageText());
                messageUser.setText(getItem(position).getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                       getItem(position).getMessageTime()));

                ImageButton delete = v.findViewById(R.id.message_delete);

                if(model.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference("post").child("pune").child(postid).child("comment").
                                    child(comment_adapter.getRef(position).getKey()).removeValue();
                        }
                    });
                }
                else {
                    delete.setVisibility(View.INVISIBLE);
                }
            }
        };

        listOfMessages.setAdapter(comment_adapter);
        listOfMessages.setStackFromBottom(true);





    }


    void getData(String id){

        fb = FirebaseDatabase.getInstance();
        dr = fb.getReference("post");



        dr.child(city).child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.getKey().equals("detail")) {
                    String username1 = dataSnapshot.child("username").getValue(String.class);
                    String time1 = dataSnapshot.child("time").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String place1 = dataSnapshot.child("place").getValue(String.class);
                    String desc1 = dataSnapshot.child("desc").getValue(String.class);

                    title.setText(name);
                    username.setText(username1);
                    time.setText(time1);
                    place.setText(place1);
                    desc.setText(desc1);
                }

                else if(dataSnapshot.getKey().equals("img")){

                    if(dataSnapshot.child("1").exists())
                        img.add(dataSnapshot.child("1").getValue(String.class));
                    if(dataSnapshot.child("2").exists())
                        img.add(dataSnapshot.child("2").getValue(String.class));
                    if(dataSnapshot.child("3").exists())
                        img.add(dataSnapshot.child("3").getValue(String.class));
                    if(dataSnapshot.child("4").exists())
                        img.add(dataSnapshot.child("4").getValue(String.class));
                    if(dataSnapshot.child("5").exists())
                        img.add(dataSnapshot.child("5").getValue(String.class));

                    if(!img.isEmpty()) {
                        adapter = new ViewPageAdapter(Posts.this, img);
                        viewPager.setAdapter(adapter);
                    }

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

}
