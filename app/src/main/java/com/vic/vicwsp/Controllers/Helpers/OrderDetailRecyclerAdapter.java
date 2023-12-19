package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.OrdersHistory.Datum;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.Coverage;
import com.vic.vicwsp.Views.Activities.MerchantRating;
import com.vic.vicwsp.Views.Activities.RatingActivity;
import com.vic.vicwsp.Views.Activities.Seller2Driver;
import com.vic.vicwsp.Views.Fragments.OrdersTrackingList;

import static android.view.View.GONE;

public class OrderDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
        private ItemHeader itemHeader;
        private ItemFooter itemFooter;
        private ItemCenter itemCenter;
    private Datum datum;
    private boolean fromReceived;

    public void receiveReviewBroadcast() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int is_reviewd = intent.getIntExtra("is_reviewed", 0);
                datum.getDetails().setIs_reviewed(is_reviewd);
                notifyDataSetChanged();
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter("UpdateOrderReviewButton"));
    }

    public OrderDetailRecyclerAdapter(Context context, Datum datum, boolean fromReceived) {
        this.context = context;
        this.datum = datum;
        this.fromReceived = fromReceived;
        receiveReviewBroadcast();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_order_details, parent, false);
            return new ItemHeader(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_details, parent, false);
            return new ItemCenter(itemView);
        } else if (viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_orderdetail, parent, false);
            return new ItemFooter(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (getItemViewType(position) == 0) {
                itemHeader = ((ItemHeader) holder);
                itemHeader.bind(position);
            } else if (getItemViewType(position) == 1) {
                itemCenter = ((ItemCenter) holder);
                itemCenter.bind(position);
            } else if (getItemViewType(position) == 2) {
                itemFooter = ((ItemFooter) holder);
                itemFooter.bind(position);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ItemHeader extends RecyclerView.ViewHolder {

        TextView orderNumber, orderDate, name, address, email, phone, province, tvDeatilStatusAnswer, companyNameAnswer;

        public ItemHeader(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.tvOrderDetailNumber);
            orderDate = itemView.findViewById(R.id.tvDetailDateAnswer);
            email = itemView.findViewById(R.id.tvOrderDetailEmail);
            phone = itemView.findViewById(R.id.tvOrderDetailPhoneNumber);
            province = itemView.findViewById(R.id.tvOrderDetailProvince);
            address = itemView.findViewById(R.id.tvOrderDetailAddress);
            name = itemView.findViewById(R.id.tvOrderDetailName);
            tvDeatilStatusAnswer = itemView.findViewById(R.id.tvDeatilStatusAnswer);
            companyNameAnswer = itemView.findViewById(R.id.tvOrderDetailCompanyNameAnswer);

        }

        private void bind(int position) {

            name.setText(datum.getDetails().getUser());
            email.setText(datum.getDetails().getEmail());
            phone.setText(datum.getDetails().getPhone());
            address.setText(datum.getDetails().getAddress());
            companyNameAnswer.setText(datum.getDetails().getCompany_name());
            orderNumber.setText(String.valueOf(datum.getId()));
            orderDate.setText(datum.getDate());

            // checking the statuses of the orders
            switch (datum.getStatus()) {
                case "Completed":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.CompleteStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Pre-order":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.PreOrderStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Pending-payment":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.Pending_payment));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Nego-approved":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.NegoapprovedStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Paid":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.PaidStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Declined":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.DeclineStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.red));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Expired":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.ExpiredStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.red));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Unpaid":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.UnpaidStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.red));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Pending":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.PendingStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.red));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Negotiation":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.negotitiationStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.splashColor));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_nego), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                default:
                    break;

            }
        }
    }


    public class ItemCenter extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        OrderDetailCenterAdapter orderDetailCenterAdapter;
        LinearLayoutManager linearLayoutManager;

        public ItemCenter(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.orderDetailItemRecycler);
        }

        public void bind(int position) {
            orderDetailCenterAdapter = new OrderDetailCenterAdapter(context, datum.getDetails().getProducts(), fromReceived);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(orderDetailCenterAdapter);
        }
    }

    public class ItemFooter extends RecyclerView.ViewHolder {

        ConstraintLayout footer;
        TextView tvTotaltHtAnswer, tvTVAAnswer, tvTVA, tvTotalTTCAnswer, tvDeliveryCharges,
                tvDeliveryChargesAnswer, tvTotalDiscountAnswer, tvTotalDiscount, coverageTextOrderDetail;
        ConstraintLayout btnFeedback, btnOrderNavigation, btnChatWithDriver, payCoverageConstraint, btnPayCoverage;

        public ItemFooter(@NonNull View itemView) {
            super(itemView);

            footer = itemView.findViewById(R.id.cartFooter);
            btnFeedback = itemView.findViewById(R.id.btnRateTheOrder);
            tvTotaltHtAnswer = itemView.findViewById(R.id.tvTotaltHtAnswer);
            tvTVAAnswer = itemView.findViewById(R.id.tvTVAAnswer);
            tvTVA = itemView.findViewById(R.id.tvTVA);
            tvTotalTTCAnswer = itemView.findViewById(R.id.tvTotalTTCAnswer);
            tvDeliveryCharges = itemView.findViewById(R.id.tvDeliveryCharges);
            tvDeliveryChargesAnswer = itemView.findViewById(R.id.tvDeliveryChargesAndwer);
            tvTotalDiscountAnswer = itemView.findViewById(R.id.tvTotalDiscountAnswer);
            tvTotalDiscount = itemView.findViewById(R.id.tvTotalDiscount);
            btnOrderNavigation = itemView.findViewById(R.id.btnOrderNavigation);
            btnPayCoverage = itemView.findViewById(R.id.btnPayCoverage);
            btnChatWithDriver = itemView.findViewById(R.id.btnChatWithDriver);
            coverageTextOrderDetail = itemView.findViewById(R.id.coverageTextOrderDetail);
            payCoverageConstraint = itemView.findViewById(R.id.coverageConstraint);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {

            if (fromReceived) {
                tvDeliveryCharges.setVisibility(GONE);
                tvDeliveryChargesAnswer.setVisibility(GONE);
                btnFeedback.setVisibility(GONE);
                tvTVA.setVisibility(GONE);
                tvTVAAnswer.setVisibility(GONE);
                if (datum.getDetails().getDriverName() != null && !datum.getDetails().getDriverName().equals(""))
                    btnChatWithDriver.setVisibility(View.VISIBLE);
                else {
                    btnChatWithDriver.setVisibility(GONE);
                }

                btnOrderNavigation.setVisibility(GONE);
                btnOrderNavigation.setEnabled(false);

            } else {

                btnOrderNavigation.setVisibility(View.VISIBLE);
                btnOrderNavigation.setEnabled(true);

                btnChatWithDriver.setVisibility(GONE);

                if (datum.getDetails().getIs_pickup() == 0) {
                    tvDeliveryCharges.setVisibility(View.VISIBLE);
                    tvDeliveryChargesAnswer.setVisibility(View.VISIBLE);
                    tvDeliveryChargesAnswer.setText(datum.getDetails().getDelivery_charges() +
                            SharedPreference.getSimpleString(context, Constants.currency));
                } else {
                    tvDeliveryCharges.setVisibility(GONE);
                    tvDeliveryChargesAnswer.setVisibility(GONE);
                }

                if (datum.getStatus().equals("Expired")) {
                    btnFeedback.setVisibility(GONE);
                } else {
                    if (datum.getDetails().getIs_reviewed() == 0)
                        btnFeedback.setVisibility(View.VISIBLE);
                    else
                        btnFeedback.setVisibility(GONE);
                }

                tvTVA.setVisibility(View.VISIBLE);
                tvTVAAnswer.setVisibility(View.VISIBLE);

//                if (datum.getStatus().equals("Paid")) {

                if (datum.getDetails().getIs_pickup() == 0) {
                    btnOrderNavigation.setVisibility(View.VISIBLE);
                    btnOrderNavigation.setEnabled(true);
                } else {
                    btnOrderNavigation.setVisibility(GONE);
                    btnOrderNavigation.setEnabled(false);
                }
//                } else {
//                    btnOrderNavigation.setVisibility(GONE);
//                    btnOrderNavigation.setEnabled(false);
//                }

                if (datum.getDetails().getCoverage_status().equals("Unpaid")) {
                    coverageTextOrderDetail.setText(context.getResources().getString(R.string.overdraft_message_order_detail,
                            datum.getDetails().getCoverage_amount() + SharedPreference.getSimpleString(context, Constants.currency),
                            datum.getDetails().getCoverage_due_date()));
                    payCoverageConstraint.setVisibility(View.VISIBLE);

                } else {
                    payCoverageConstraint.setVisibility(GONE);
                }
            }

            btnPayCoverage.setOnClickListener(view -> {
                Intent intent = new Intent(context, Coverage.class);
                intent.putExtra("message", coverageTextOrderDetail.getText().toString());
                intent.putExtra("amount", datum.getDetails().getCoverage_amount());
                intent.putExtra("fromWhere", "OrderDetail");
                intent.putExtra("orderId", datum.getId());
                context.startActivity(intent);
            });

            btnOrderNavigation.setOnClickListener(view -> {

                OrdersTrackingList ordersTrackingList = new OrdersTrackingList();
                Bundle bundle = new Bundle();
                bundle.putInt("orderId", datum.getId());
                ordersTrackingList.setArguments(bundle);

                ((AppCompatActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, ordersTrackingList)
                        .addToBackStack("OrderTrackingList")
                        .commit();

            });

            btnChatWithDriver.setOnClickListener(v -> {
                Intent intent = new Intent(context, Seller2Driver.class);
                intent.putExtra("orderId", datum.getId());
                intent.putExtra("orderNumber", String.valueOf(datum.getId()));
                intent.putExtra("userName", datum.getDetails().getDriverName());
                context.startActivity(intent);
            });


            btnFeedback.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putInt("orderId", datum.getId());
                Intent intent = null;
                if (fromReceived) {
                    intent = new Intent(context, MerchantRating.class);
                    intent.putExtras(bundle);

                } else {
                    intent = new Intent(context, RatingActivity.class);
                    intent.putExtras(bundle);
                }
                context.startActivity(intent);
            });

            tvTotalDiscount.setVisibility(View.VISIBLE);
            tvTotalDiscountAnswer.setVisibility(View.VISIBLE);
            tvTotalDiscountAnswer.setText(Common.round(AppUtils.convertStringToDouble(datum.getDetails().getDiscount()), 2) + SharedPreference.getSimpleString(context, Constants.currency));
            tvTotaltHtAnswer.setText(Common.round(AppUtils.convertStringToDouble(datum.getSubtotal()), 2) + SharedPreference.getSimpleString(context, Constants.currency));
            tvTVAAnswer.setText(Common.round(AppUtils.convertStringToDouble(datum.getDetails().getVat()), 2) + SharedPreference.getSimpleString(context, Constants.currency));
            if (fromReceived)
                tvTotalTTCAnswer.setText(Common.round(AppUtils.convertStringToDouble(datum.getSubtotal()) - AppUtils.convertStringToDouble(datum.getDetails().getDiscount())
                        , 2) + SharedPreference.getSimpleString(context, Constants.currency));
            else
                tvTotalTTCAnswer.setText(Common.round(AppUtils.convertStringToDouble(datum.getGrandtotal()), 2) + SharedPreference.getSimpleString(context, Constants.currency));
        }
    }


}
