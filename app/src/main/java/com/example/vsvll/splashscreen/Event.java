package com.example.vsvll.splashscreen;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Event extends AppCompatActivity {

    int mYear,mMonth,mDay;
    TextView date;
    String DATE;
    FirebaseDatabase fd;
    DatabaseReference dataref;
    RecyclerView recyclerView;
    Event_adapter event_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ImageView calender= findViewById(R.id.event_calender);
        date = findViewById(R.id.event_date);

        fd = FirebaseDatabase.getInstance();
        dataref = fd.getReference("event");

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date_ = "EVENT ON "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                event_adapter = new Event_adapter(getApplicationContext(),recyclerView,date_,new ArrayList<Event_details>());
                                recyclerView.setAdapter(event_adapter);
                                date.setText(date_);
                                DATE = dayOfMonth+"_"+(monthOfYear+1)+"_"+year;
                                getData(DATE);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });


        recyclerView = findViewById(R.id.event_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }


    void getData(final String event_date){
        dataref.child(event_date).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

               String name = dataSnapshot.child("name").getValue(String.class);
               String uri = dataSnapshot.child("img").getValue(String.class);

               Event_details event_details = new Event_details(name,uri);
                event_adapter.update(event_details);
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
