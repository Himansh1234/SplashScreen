package com.example.vsvll.splashscreen;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NewsFeed_Adapter extends RecyclerView.Adapter< NewsFeed_Adapter.ViewHolder>{

    ArrayList<Post_details> posts ;
    Context context;
    RecyclerView recyclerView;
    String date;
    public NewsFeed_Adapter(Context context,RecyclerView  recyclerView, ArrayList<Post_details> posts){
        this.context=context;
        this.posts=posts;
        this.recyclerView=recyclerView;
        this.date=date;
    }

    void update(Post_details pd){
        posts.add(pd);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public NewsFeed_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        return new NewsFeed_Adapter.ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsFeed_Adapter.ViewHolder viewHolder, int i) {

        Glide.with(context).load(posts.get(i).getUri()).into(viewHolder.imageView);
        viewHolder.place.setText(posts.get(i).getPlace());
        viewHolder.title.setText(posts.get(i).getName());
        viewHolder.username.setText(posts.get(i).getUsername());
        viewHolder.time.setText("Posted At  "+posts.get(i).getTime());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView username,title,place,time;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.post_img);
            username=itemView.findViewById(R.id.post_username);
            title=itemView.findViewById(R.id.post_title);
            place=itemView.findViewById(R.id.post_place);
            time=itemView.findViewById(R.id.post_time);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = recyclerView.getChildAdapterPosition(itemView);
                    Intent in = new Intent(itemView.getContext(),Posts.class);
                    in.putExtra("post",posts.get(i).getId());
                    in.putExtra("city",posts.get(i).getCity());
                    itemView.getContext().startActivity(in);

                }
            });
        }
    }
}
