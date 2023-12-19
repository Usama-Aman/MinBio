package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Models.Categories;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Fragments.Favorites;
import com.vic.vicwsp.Views.Fragments.Products;

import java.util.ArrayList;

public class CatalogueRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View v;
    private Context context;
    private ArrayList<Categories> categories; //Categories array list containing all the data about the categories
    private Horizontal horizontalItem;
    private Vertical verticalItem;

    public CatalogueRecyclerAdapter(Context context, ArrayList<Categories> categories) {
        this.context = context;
        this.categories = categories;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_full_catalogue_recycler, parent, false);
            return new Horizontal(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_half_catalogue_recycler, parent, false);
            return new Vertical(itemView);
        }
        return null;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        try {
            if (holder.getItemViewType() == 0) {
                horizontalItem = ((Horizontal) holder);
                horizontalItem.bind(position);
            } else if (holder.getItemViewType() == 1) {
                verticalItem = ((Vertical) holder);
                verticalItem.bind(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Loading next fragment function
    private void loadFragment(Fragment fragment, String key, View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, fragment)
                .addToBackStack(key)
                .commit();

    }

    // Horizontal class to show the horizontally full screen list items
    private class Horizontal extends RecyclerView.ViewHolder {
        private ConstraintLayout fullConstraint;
        private ImageView imageView;
        private TextView textView;

        private Horizontal(View v) {
            super(v);
            imageView = itemView.findViewById(R.id.ivItemImage);
            textView = itemView.findViewById(R.id.tvItemText);
            fullConstraint = itemView.findViewById(R.id.fullConstraint);
        }

        private void bind(int position) {
            try {
                textView.setText(categories.get(position).getName());
                Glide.with(context)
                        .load(categories.get(position).getImagePath())
                        .into(imageView);

                fullConstraint.setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromSearch", false);
                    bundle.putString("categoryId", String.valueOf(categories.get(getAdapterPosition()).getId()));

                    SharedPreference.saveSimpleString(context, Constants.isBioOn, "false");

                    Products products = new Products();
                    products.setArguments(bundle);
                    loadFragment(products, "Products", view);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("In Cata", "onBindViewHolder: ");

            }
        }
    }

    // Vertical class to show the vrtically divided screen list items
    private class Vertical extends RecyclerView.ViewHolder {

        private ConstraintLayout leftConstraint, rightConstraint;
        private ImageView leftImageView, rightImageView;
        private TextView leftTextView, rightTextView;

        public Vertical(View v) {
            super(v);
            leftImageView = itemView.findViewById(R.id.ivLeftItemImage);
            rightImageView = itemView.findViewById(R.id.ivRightItemImage);

            leftTextView = itemView.findViewById(R.id.tvLeftItemText);
            rightTextView = itemView.findViewById(R.id.tvRightItemText);

            leftConstraint = itemView.findViewById(R.id.leftConstraint);
            rightConstraint = itemView.findViewById(R.id.rightConstraint);
        }

        private void bind(int position) {

            try {
                leftConstraint.setOnClickListener(view -> {
                    SharedPreference.saveSimpleString(context, Constants.isBioOn, "true");

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromSearch", false);
                    bundle.putString("categoryId", "");

                    Products products = new Products();
                    products.setArguments(bundle);
                    loadFragment(products, "Products", view);
                });

                rightConstraint.setOnClickListener(view -> {
                    SharedPreference.saveSimpleString(context, Constants.isBioOn, "false");
                    loadFragment(new Favorites(), "Favorites", view);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (categories.size() == 1) {
            return position;
        } else {
            if (position == categories.size())
                return 1;
            else
                return 0;
        }
    }

    @Override
    public int getItemCount() {
        return categories.size() + 1;
    }

}