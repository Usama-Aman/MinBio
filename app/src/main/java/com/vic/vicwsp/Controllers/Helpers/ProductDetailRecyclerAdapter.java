package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.elementary.minbio.Models.Response.ProductDetail.ProductDetailModel;
//import com.elementary.minbio.Models.Response.ProductDetail.Seller;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.ProductDetailModel;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Seller;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ProductDetailRecyclerAdapter extends RecyclerView.Adapter<ProductDetailRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Seller> sellers;
    private ProductDetailModel productDetailModel;

    public ProductDetailRecyclerAdapter(Context context, ArrayList<Seller> sellers, ProductDetailModel productDetailModel) {

        this.context = context;
        this.productDetailModel = productDetailModel;
        this.sellers = sellers;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_product_recycler, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.seller.setText(sellers.get(position).getName());
        holder.price.setText(Common.convertInKFormat((sellers.get(position).getPrice())) + SharedPreference.getSimpleString
                (context, Constants.currency));

        holder.ratingBar.setRating(AppUtils.convertStringToFloat(sellers.get(position).getRating().toString()));

        holder.ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.performClick();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return sellers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView seller, price, noMoreStock;
        MaterialRatingBar ratingBar;
        ImageView nego, bio, origin;
        private View viewLine;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            seller = itemView.findViewById(R.id.tvVendeurDetails);
            price = itemView.findViewById(R.id.tvPriceDetails);
            nego = itemView.findViewById(R.id.ivNegoDetails);
            bio = itemView.findViewById(R.id.ivBioDetails);
            origin = itemView.findViewById(R.id.ivOriginDetails);
            ratingBar = itemView.findViewById(R.id.rattingBarDetails);
            viewLine= itemView.findViewById(R.id.view);
        }
    }
}
