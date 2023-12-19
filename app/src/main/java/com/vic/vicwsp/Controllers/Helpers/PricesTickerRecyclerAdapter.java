package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.TickerModel;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Fragments.FromTicker;

import java.util.ArrayList;

public abstract class PricesTickerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ViewHolder viewHolder;
    private ArrayList<TickerModel> tickerModelArrayList;

    public PricesTickerRecyclerAdapter(Context context, ArrayList<TickerModel> tickerModelArrayList) {
        this.context = context;
        this.tickerModelArrayList = tickerModelArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prices_ticker, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolder = (ViewHolder) holder;
        viewHolder.bind(position);
    }

    public abstract void load();

    @Override
    public int getItemCount() {
        return tickerModelArrayList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView productName, productPrice;
        private ImageView status;
        private ConstraintLayout tickerItem;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.tickerProductName);
            productPrice = itemView.findViewById(R.id.tickerProductPrice);
            status = itemView.findViewById(R.id.tickerImage);
            tickerItem = itemView.findViewById(R.id.tickerItem);
        }

        @SuppressLint("SetTextI18n")
        private void bind(int position) {

            productPrice.setText(Common.convertInKFormat( (String.valueOf(tickerModelArrayList.get(position).getMinprice()))) +
                    SharedPreference.getSimpleString(context, Constants.currency));

            if (SharedPreference.getSimpleString(context, Constants.language).equals(Constants.french))
                productName.setText(tickerModelArrayList.get(position).getProductNameFr());
            else if (SharedPreference.getSimpleString(context, Constants.language).equals(Constants.english))
                productName.setText(tickerModelArrayList.get(position).getProductName());

            status.setVisibility(View.GONE);

            if (tickerModelArrayList.get(position).getStatus().equals("UP")) {
                status.setVisibility(View.VISIBLE);
                status.setBackground(context.getResources().getDrawable(R.drawable.green));
                productPrice.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
            } else if (tickerModelArrayList.get(position).getStatus().equals("DOWN")) {
                status.setVisibility(View.VISIBLE);
                status.setBackground(context.getResources().getDrawable(R.drawable.red));
                productPrice.setTextColor(context.getResources().getColor(R.color.red));
            }

            tickerItem.setOnClickListener(view -> {

                if (tickerModelArrayList.get(position).getProductId() > 0) {

                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) ((AppCompatActivity) context).getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    SharedPreference.saveSimpleString(context, Constants.isBioOn, "false");

                    Bundle bundle = new Bundle();
                    bundle.putInt("productId", tickerModelArrayList.get(position).getProductId());
                    FromTicker fromTicker = new FromTicker();
                    fromTicker.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFragment, fromTicker, "From Ticker")
                            .addToBackStack("From Ticker")
                            .commit();
                }
            });

        }
    }
}
