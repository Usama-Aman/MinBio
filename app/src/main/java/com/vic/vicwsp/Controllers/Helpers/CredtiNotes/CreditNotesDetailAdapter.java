package com.vic.vicwsp.Controllers.Helpers.CredtiNotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.CreditNotesDetails.Data;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.CreditNotes.CreditSavedCards;

import static com.vic.vicwsp.Utils.Constants.mLastClickTime;

public class CreditNotesDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ItemHeader itemHeader;
    private ItemFooter itemFooter;
    private ItemCenter itemCenter;
    private Context context;
    private Data creditDetailData;
    private String creditNoteStatus;

    public CreditNotesDetailAdapter(Data creditDetailData, String creditNoteStatus) {
        this.creditNoteStatus = creditNoteStatus;
        this.creditDetailData = creditDetailData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        context = parent.getContext();

        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_order_details, parent, false);
            return new ItemHeader(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_details, parent, false);
            return new ItemCenter(itemView);
        } else if (viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_credit_notes_detail, parent, false);
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

            name.setText(creditDetailData.getName());
            email.setText(creditDetailData.getEmail());
            phone.setText(creditDetailData.getPhone());
            address.setText(creditDetailData.getAddress());
            companyNameAnswer.setText(creditDetailData.getCompanyName());
            orderNumber.setText(String.valueOf(creditDetailData.getId()));
            orderDate.setText(creditDetailData.getOrderCreatedAt());

            // checking the statuses of the orders
            switch (creditDetailData.getStatus()) {
                case "Pending":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.PendingStatus));
                    tvDeatilStatusAnswer.setTextColor(context.getResources().getColor(R.color.red));
                    tvDeatilStatusAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    tvDeatilStatusAnswer.setCompoundDrawablePadding(8);
                    break;
                case "Completed":
                    tvDeatilStatusAnswer.setText(context.getResources().getString(R.string.CompleteStatus));
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
                default:
                    break;

            }
        }
    }

    public class ItemCenter extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        CreditDetailCenterAdapter orderDetailCenterAdapter;
        LinearLayoutManager linearLayoutManager;

        public ItemCenter(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.orderDetailItemRecycler);
        }

        public void bind(int position) {
            orderDetailCenterAdapter = new CreditDetailCenterAdapter(context, creditDetailData.getItems());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(orderDetailCenterAdapter);
        }
    }


    public class ItemFooter extends RecyclerView.ViewHolder {

        private TextView tvTotalTTCAnswer;
        private ConstraintLayout cash, credit, token, sepa, cheque, creditNoteFooterConstraint;

        ItemFooter(@NonNull View itemView) {
            super(itemView);
            tvTotalTTCAnswer = itemView.findViewById(R.id.tvTotalTTCAnswer);
            sepa = itemView.findViewById(R.id.sepa);
            token = itemView.findViewById(R.id.token);
            credit = itemView.findViewById(R.id.credit);
            cash = itemView.findViewById(R.id.cash);
            creditNoteFooterConstraint = itemView.findViewById(R.id.creditNoteFooterConstraint);
        }

        @SuppressLint("SetTextI18n")
        void bind(int position) {

            if (creditNoteStatus.equalsIgnoreCase("Pending"))
                creditNoteFooterConstraint.setVisibility(View.VISIBLE);
            else
                creditNoteFooterConstraint.setVisibility(View.GONE);

            tvTotalTTCAnswer.setText(Common.convertInKFormat(creditDetailData.getAmount()) +
                    SharedPreference.getSimpleString(context, Constants.currency));

            credit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    Intent intent = new Intent(context, CreditSavedCards.class);
                    intent.putExtra("creditNoteId", creditDetailData.getId());
                    context.startActivity(intent);

                }
            });


        }
    }


}
