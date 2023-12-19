package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.File;
import com.vic.vicwsp.R;

import java.util.ArrayList;

public class ProductViewSlider extends PagerAdapter {


    private LayoutInflater inflater;
    private Context context;
    private ArrayList<File> files = new ArrayList<>();


    public ProductViewSlider(Context context, ArrayList<File> files) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.files = files;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (files.size() == 0)
            return 1;
        else
            return files.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {

        View imageLayout = inflater.inflate(R.layout.item_product_view, view, false);

        assert imageLayout != null;
        final ImageView imageView = imageLayout
                .findViewById(R.id.image);


        if (files.size() == 0) {
            imageView.setBackground(context.getResources().getDrawable(R.drawable.place_holder));
        } else
            Glide.with(context)
                    .load(files.get(position).getImagePath())
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder))
                    .into(imageView);

        view.addView(imageLayout, position);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}