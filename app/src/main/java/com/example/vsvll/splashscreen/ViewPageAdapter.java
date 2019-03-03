package com.example.vsvll.splashscreen;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class ViewPageAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> imageUrls;

    ViewPageAdapter(Context context, ArrayList<String> imageUrls) {
        this.imageUrls = new ArrayList<>();
        this.context = context;
        this.imageUrls = imageUrls;

          }

    void update(String img){
        imageUrls.add(img);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        Glide.with(context)
                .load(imageUrls.get(position))
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
