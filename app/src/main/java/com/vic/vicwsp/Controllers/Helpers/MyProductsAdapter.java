package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Models.Response.ProductsPagination.Datum;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Fragments.MyProducts;
import com.vic.vicwsp.Views.Fragments.SalesProducts;

import java.util.ArrayList;

public class MyProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View v;
    private Context context;
    private ArrayList<Datum> productsData;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Items h;

    private MyProducts myProducts;

    public MyProductsAdapter(Context context, ArrayList<Datum> productsData, MyProducts myProducts) {
        this.context = context;
        this.productsData = productsData;
        this.myProducts = myProducts;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = inflater.inflate(R.layout.item_my_products, parent, false);
            viewHolder = new Items(v);
        } else {
            v = inflater.inflate(R.layout.item_loader_pagination_products, parent, false);
            viewHolder = new Progress(v);
        }
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                 final int position) {

        try {
            if (holder instanceof Progress) {
                showLoadingView((Progress) holder, position);
            } else {
                ((Items) holder).bind(position);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    private void showLoadingView(Progress holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public int getItemCount() {
        return productsData == null ? 0 : productsData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return productsData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class Progress extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public Progress(View v) {
            super(v);
            progressBar = itemView.findViewById(R.id.progressBarPagination);
        }
    }

    protected class Items extends RecyclerView.ViewHolder {
        private TextView productName;
        private ImageView productImage;
        private ConstraintLayout productConstraint;

        public Items(View v) {

            super(v);
            productImage = itemView.findViewById(R.id.ivProductImage);
            productName = itemView.findViewById(R.id.tvProductName);
            productConstraint = itemView.findViewById(R.id.productsConstraint);

        }

        private void bind(int position) {

            try {
                productName.setText(productsData.get(position).getName());

                Glide.with(context)
                        .load(productsData.get(position).getImagePath())
                        .into(productImage);

                productConstraint.setOnClickListener(view -> {

                    if (!MainActivity.radiationStatus) {
                        SalesProducts salesProducts = new SalesProducts();
                        Bundle bundle = new Bundle();
                        bundle.putInt("productId", productsData.get(position).getId());
                        bundle.putString("productName", productsData.get(position).getName());
                        salesProducts.setArguments(bundle);
                        ((AppCompatActivity) context).getSupportFragmentManager().
                                beginTransaction()
                                .replace(R.id.mainFragment, salesProducts, "Sales Products")
                                .addToBackStack("Sales Products")
                                .commit();
                    } else {
                        Common.showToast((AppCompatActivity) context, context.getResources().getString(R.string.radiation_status_is_not_good), false);
                    }

                });
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }


        }
    }

}
