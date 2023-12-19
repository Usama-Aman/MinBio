package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Controllers.Interfaces.UpdateTheProductPrice;
import com.vic.vicwsp.Models.Response.ProductsPagination.Datum;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Fragments.PurchaseForm;

import java.util.ArrayList;

import static com.vic.vicwsp.Utils.Constants.mLastClickTime;

public class ProductsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private View v;
    private Context context;
    private ArrayList<Datum> productsData;
    private ArrayList<Datum> unFilteredProductsData;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Items h;
    private UpdateTheProductPrice updateTheProductPrice;

    public ProductsRecyclerAdapter(Context context, ArrayList<Datum> productsData, UpdateTheProductPrice updateTheProductPrice) {
        this.context = context;
        this.productsData = productsData;
        this.unFilteredProductsData = productsData;
        this.updateTheProductPrice = updateTheProductPrice;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = inflater.inflate(R.layout.item_catalogue_products, parent, false);
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
                ((Items) holder).productName.setText(productsData.get(position).getName());
                ((Items) holder).productRetail.setText(productsData.get(position).getProductDescription());

                if (productsData.get(position).getDetails().getMerchants() <= 1) {
                    ((Items) holder).marchants.setText(productsData.get(position).getDetails().getMerchants().toString() + " " +
                            context.getResources().getString(R.string.productListMerchant));
                } else {
                    ((Items) holder).marchants.setText(productsData.get(position).getDetails().getMerchants().toString() + " " +
                            context.getResources().getString(R.string.productListMerchants));
                }

                ((Items) holder).price.setText(Common.convertInKFormat(productsData.get(position).getDetails().getPrice()) + SharedPreference.getSimpleString
                        (context, Constants.currency));


                Glide.with(context)
                        .load(productsData.get(position).getImagePath())
                        .into(((Items) holder).productImage);


                ((Items) holder).productConstraint.setOnClickListener(view -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    goToProductDetails(new PurchaseForm(), "Product Details", view, position);
                });

            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            Log.d("In Products", "onBindViewHolder: ");
        }

    }

    private void showLoadingView(Progress holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
    }

    private void goToProductDetails(Fragment fragment, String key, View view, int position) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromFav", false);
            bundle.putInt("productId", productsData.get(position).getId());
            bundle.putParcelable("updateProductPrice", updateTheProductPrice);
            bundle.putInt("position", position);
            fragment.setArguments(bundle);

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragment, fragment)
                    .addToBackStack(key)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productsData == null ? 0 : productsData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return productsData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                ArrayList<Datum> filteredList = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList.addAll(unFilteredProductsData);
                } else {
                    for (Datum row : unFilteredProductsData) {
                        if (row.getName().toLowerCase().startsWith(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                }
                try {
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredList;
                    return filterResults;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null) {
//                    productsData.clear();
                    productsData = (ArrayList<Datum>) filterResults.values;
                    notifyDataSetChanged();
                }
            }
        };
    }


    public class Progress extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public Progress(View v) {
            super(v);
            progressBar = itemView.findViewById(R.id.progressBarPagination);
        }
    }

    protected class Items extends RecyclerView.ViewHolder {
        private TextView price, from, productName, productRetail, marchants;

        private ImageView productImage, fav, delete, go;
        private ConstraintLayout productConstraint;

        public Items(View v) {

            super(v);
            productImage = itemView.findViewById(R.id.ivProductImage);
            price = itemView.findViewById(R.id.tvPrice);
            from = itemView.findViewById(R.id.tvFrom);
            productName = itemView.findViewById(R.id.tvProductName);
            productRetail = itemView.findViewById(R.id.tvProductRetail);
            marchants = itemView.findViewById(R.id.tvMarchants);
            delete = itemView.findViewById(R.id.ivDeleteProduct);
            go = itemView.findViewById(R.id.ivIconGo);
            productConstraint = itemView.findViewById(R.id.productsConstraint);
            delete.setVisibility(View.GONE);
        }
    }

}
