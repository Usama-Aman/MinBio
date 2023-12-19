package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.elementary.minbio.Models.Response.ProductDetail.Seller;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Seller;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import java.util.ArrayList;

public class FromTickerItemBottomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Seller> sellers;

    public FromTickerItemBottomAdapter(Context context, ArrayList<Seller> sellers) {

        this.context = context;
        this.sellers = sellers;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recycler, parent, false);
            return new Items(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_from_ticker, parent, false);
            return new Footer(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        try {
            if (getItemViewType(position) == 0) {
                ((Items) holder).bind(position);
            } else if (getItemViewType(position) == 1) {
                ((Footer) holder).bind(position);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return sellers.size() + 1;
    }

    //Overriding method to get the position of the items of the recycler view
    @Override
    public int getItemViewType(int position) {

        if (sellers.size() == 1) {
            return position;
        } else {
            if (position == sellers.size())
                return 1;
            else
                return 0;
        }
    }

    public class Items extends RecyclerView.ViewHolder {

        TextView seller, price, noMoreStock;
        RatingBar ratingBar;
        ImageView nego, bio, origin;


        public Items(@NonNull View itemView) {
            super(itemView);

            seller = itemView.findViewById(R.id.tvVendeurDetails);
            price = itemView.findViewById(R.id.tvPriceDetails);
            nego = itemView.findViewById(R.id.ivNegoDetails);
            bio = itemView.findViewById(R.id.ivBioDetails);
            origin = itemView.findViewById(R.id.ivOriginDetails);
            ratingBar = itemView.findViewById(R.id.rattingBarDetails);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {

            seller.setText(sellers.get(position).getName());
            price.setText(Common.convertInKFormat((sellers.get(position).getPrice())) + SharedPreference.getSimpleString
                    (context, Constants.currency));

            ratingBar.setRating(AppUtils.convertStringToFloat(sellers.get(position).getRating().toString()));

            ratingBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.performClick();
                    return false;
                }
            });


            nego.setVisibility(View.GONE);
            bio.setVisibility(View.GONE);

        }
    }

    public class Footer extends RecyclerView.ViewHolder {

        public Footer(@NonNull View itemView) {
            super(itemView);

        }

        public void bind(int position) {
        }
    }
}

