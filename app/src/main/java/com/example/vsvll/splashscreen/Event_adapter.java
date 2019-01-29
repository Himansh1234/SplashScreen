package com.example.vsvll.splashscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Event_adapter extends RecyclerView.Adapter< Event_adapter.ViewHolder>  {

    ArrayList<Event_details> events ;
    Context context;
    RecyclerView recyclerView;
    String date;
    public Event_adapter(Context context,RecyclerView  recyclerView,String date, ArrayList<Event_details> events){
        this.context=context;
        this.events=events;
        this.recyclerView=recyclerView;
        this.date=date;
    }

    void update(Event_details bitmap){
       events.add(bitmap);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Event_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event, viewGroup, false);
        return new Event_adapter.ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull Event_adapter.ViewHolder viewHolder, int i) {

        Glide.with(context).load(events.get(i).getUri()) .into(viewHolder.imageView);

        viewHolder.title.setText(events.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.event_img);
            title=itemView.findViewById(R.id.event_title);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*int i = recyclerView.getChildAdapterPosition(itemView);
                    bitmaps.remove(i);
                    notifyDataSetChanged();*/
                }
            });
        }
    }


}
