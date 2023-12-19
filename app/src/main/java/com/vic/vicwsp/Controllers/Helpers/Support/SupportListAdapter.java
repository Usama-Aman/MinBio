package com.vic.vicwsp.Controllers.Helpers.Support;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.Support.SupportList.SupportList;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.CustomTypefaceSpan;
import com.vic.vicwsp.Views.Activities.Support.SupportChat;

import java.util.ArrayList;


public class SupportListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ArrayList<SupportList> compliantsListData;

    public SupportListAdapter(Context context, ArrayList<SupportList> compliantsListData) {
        this.context = context;
        this.compliantsListData = compliantsListData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = inflater.inflate(R.layout.item_complain, parent, false);
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

            // ((Items) holder).complainAssignUserName.setVisibility(View.GONE);
            ((Items) holder).complainAssignUserName.setText("");

            if (compliantsListData.get(position).getSupportAssignedTo() != null) {
                if (AppUtils.isSet(compliantsListData.get(position).getSupportAssignedTo().getName())) {
                    ((Items) holder).complainAssignUserName.setText(context.getResources().getString(R.string.tvAssigned) + " " + compliantsListData.get(position).getSupportAssignedTo().getName());
                    //  ((Items) holder).complainAssignUserName.setVisibility(View.VISIBLE);
                }
            }

            ((Items) holder).complainCode.setText("");
            if (AppUtils.isSet(compliantsListData.get(position).getSupportNo())) {
                ((Items) holder).complainCode.setText(compliantsListData.get(position).getSupportNo());
            }

            ((Items) holder).complainDate.setText("");
            if (AppUtils.isSet(compliantsListData.get(position).getCreatedAt())) {
                ((Items) holder).complainDate.setText(AppUtils.formatDateFromDateTime(compliantsListData.get(position).getCreatedAt()));
            }

            ((Items) holder).complainStatus.setText("");
            if (AppUtils.isSet(compliantsListData.get(position).getStatus())) {
                setCustomStatus(context.getResources().getString(R.string.order_status) + ": ", compliantsListData.get(position).getStatus(),
                        ((Items) holder).complainStatus);
            }


            ((Items) holder).itemView.setOnClickListener(view -> {

/*
                CompliantList datum = new CompliantList(compliantsListData.get(position).getId(), compliantsListData.get(position).getOrderId(),
                        compliantsListData.get(position).getOrderDetailId(), compliantsListData.get(position).getComplaintTypeId(), compliantsListData.get(position).getUserId(),
                        compliantsListData.get(position).getComment(), compliantsListData.get(position).getIsRead(), compliantsListData.get(position).getAssignedTo(), compliantsListData.get(position).getStatus(), compliantsListData.get(position).getComplaintNo(),
                        compliantsListData.get(position).getCreatedAt(), compliantsListData.get(position).getUpdatedAt());*/

                Intent i = new Intent(context, SupportChat.class);
                i.putExtra("isComingFromNotification", false);
                i.putExtra("supportId", String.valueOf(compliantsListData.get(position).getId()));
                context.startActivity(i);
            });


        }
    }

    public void setCustomStatus(String statusTitle, String status, TextView tvComplainStatus) {
        try {

            Typeface regularTypeFace = Typeface.createFromAsset(context.getAssets(), "font/roboto_bold.ttf");


            final SpannableStringBuilder str = new SpannableStringBuilder(statusTitle + status);

            str.setSpan(new CustomTypefaceSpan("", regularTypeFace), 0, statusTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new CustomTypefaceSpan("", regularTypeFace), statusTitle.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            str.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.complaintItemTextColor)), 0, statusTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (status.equalsIgnoreCase("Open"))
                str.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.signUpButtonColor)), statusTitle.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            else
                str.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.complaintItemTextColor)), statusTitle.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            tvComplainStatus.setText(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return compliantsListData == null ? 0 : compliantsListData.size();
    }


    @Override
    public int getItemViewType(int position) {
        return compliantsListData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class Items extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView complainAssignUserName, complainStatus, complainCode, complainDate;

        public Items(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.compConstraint);
            complainAssignUserName = itemView.findViewById(R.id.complainAssignUserName);
            complainStatus = itemView.findViewById(R.id.complainStatus);
            complainCode = itemView.findViewById(R.id.complainCode);
            complainDate = itemView.findViewById(R.id.complainDate);
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