package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.OrdersHistory.Datum;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Fragments.Negotiation;
import com.vic.vicwsp.Views.Fragments.OrderDetail;
import com.vic.vicwsp.Views.Fragments.Payment;

import java.util.ArrayList;

import static com.vic.vicwsp.Utils.Constants.mLastClickTime;

public class PastOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ArrayList<Datum> orderHistoryData;

    public PastOrdersAdapter(Context context, ArrayList<Datum> orderHistoryData) {
        this.context = context;
        this.orderHistoryData = orderHistoryData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = inflater.inflate(R.layout.item_pr_recycler, parent, false);
            viewHolder = new Items(v);
        } else {
            v = inflater.inflate(R.layout.item_loader_pagination_products, parent, false);
            viewHolder = new Progress(v);
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof Progress) {

        } else {

            if (position % 2 == 1)
                ((Items) holder).constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            else
                ((Items) holder).constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.catalogueBackground));

            ((Items) holder).hash.setText(String.valueOf(orderHistoryData.get(position).getId()));
            ((Items) holder).tvDate.setText(orderHistoryData.get(position).getDate());

            ((Items) holder).tvAmount.setText(Common.convertInKFormat(String.valueOf(AppUtils.convertStringToDouble(orderHistoryData.get(position).getSubtotal())
                    - AppUtils.convertStringToDouble(orderHistoryData.get(position).getDetails().getDiscount()))) + SharedPreference.getSimpleString(context, Constants.currency));


            ((Items) holder).tvStatus.setVisibility(View.GONE);

            switch (orderHistoryData.get(position).getStatus()) {
                case "Completed":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.CompleteStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Pre-order":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.PreOrderStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Pending-payment":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.Pending_payment));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Paid":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.PaidStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Nego-approved":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.NegoapprovedStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Declined":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.DeclineStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Expired":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.ExpiredStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Unpaid":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.UnpaidStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Pending":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.PendingStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.grey));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Negotiation":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.negotitiationStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.splashColor));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_nego), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Nego-rejected":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.NegoRejectedStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                default:
                    break;
            }

            ((Items) holder).tvStatus.setVisibility(View.VISIBLE);


            ((Items) holder).btnDetails.setOnClickListener(view -> {

                Datum datum = new Datum(orderHistoryData.get(position).getId(), orderHistoryData.get(position).getDate(),
                        orderHistoryData.get(position).getGrandtotal(), orderHistoryData.get(position).getSubtotal(),
                        orderHistoryData.get(position).getStatus(), orderHistoryData.get(position).getDetails());

                if (orderHistoryData.get(position).getStatus().equals("Negotiation") || orderHistoryData.get(position).getStatus().equals("Nego-rejected")
                        || orderHistoryData.get(position).getStatus().equals("Expired")) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromBuy", false);
                    bundle.putBoolean("fromOrderDetails", true);
                    bundle.putInt("orderId", orderHistoryData.get(position).getId());
                    Negotiation negotiation = new Negotiation();
                    negotiation.setArguments(bundle);

                    sendFragment(negotiation, "Negotiation");
                } else if (orderHistoryData.get(position).getStatus().equals("Nego-approved")) {

                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromOrderDetails", true);
                    bundle.putInt("orderId", orderHistoryData.get(position).getId());
                    Payment payment = new Payment();
                    payment.setArguments(bundle);
                    ((AppCompatActivity) context)
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFragment, payment, "Payment")
                            .addToBackStack("Payment").commit();
                } else {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromReceived", false);
                    bundle.putParcelable("OrderHistoryDetail", datum);
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setArguments(bundle);

                    sendFragment(orderDetail, "OrderDetail");
                }
            });


        }
    }

    private void sendFragment(Fragment fragment, String key) {
        ((AppCompatActivity) context)
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, fragment, "Order Detail")
                .addToBackStack(key).commit();
    }

    @Override
    public int getItemCount() {
        return orderHistoryData == null ? 0 : orderHistoryData.size();
    }


    @Override
    public int getItemViewType(int position) {
        return orderHistoryData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class Items extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        ImageView btnDetails;
        TextView hash, tvDate, tvAmount, tvStatus;

        public Items(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.prConstraint);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            hash = itemView.findViewById(R.id.hash);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);

        }
    }


    public class Progress extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public Progress(View v) {
            super(v);
            progressBar = itemView.findViewById(R.id.progressBarPagination);
        }
    }
}
