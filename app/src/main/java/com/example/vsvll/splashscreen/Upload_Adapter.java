package com.example.vsvll.splashscreen;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;


public class Upload_Adapter extends RecyclerView.Adapter<Upload_Adapter.ViewHolder> {

    ArrayList<Bitmap> bitmaps ;
    Context context;
    RecyclerView recyclerView;
    public Upload_Adapter(Context context,RecyclerView  recyclerView, ArrayList<Bitmap> bitmaps){
        this.context=context;
        this.bitmaps=bitmaps;
        this.recyclerView=recyclerView;
    }
    //jjj

    void update(Bitmap bitmap){
        bitmaps.add(bitmap);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_items, viewGroup, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.imageView.setImageBitmap(bitmaps.get(i));
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton imageButton;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.grid_item_img);
            imageButton=itemView.findViewById(R.id.grid_item_cancle);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = recyclerView.getChildAdapterPosition(itemView);
                    bitmaps.remove(i);
                    notifyDataSetChanged();
                }
            });
        }
    }

}