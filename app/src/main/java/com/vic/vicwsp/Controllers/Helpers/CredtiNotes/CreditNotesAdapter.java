package com.vic.vicwsp.Controllers.Helpers.CredtiNotes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.CreditNotes.Datum;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.CreditNotes.CreditOrderDetail;

import java.util.ArrayList;

public class CreditNotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ArrayList<Datum> creditNotesList = new ArrayList<>();

    public CreditNotesAdapter(ArrayList<Datum> creditNotesList) {
        this.creditNotesList = creditNotesList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = inflater.inflate(R.layout.item_credit_notes, parent, false);
            viewHolder = new Items(v);
        } else {
            v = inflater.inflate(R.layout.item_loader_pagination_products, parent, false);
            viewHolder = new Progress(v);
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof Items) {

            if (position % 2 == 1)
                ((Items) holder).constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            else
                ((Items) holder).constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.catalogueBackground));

            ((Items) holder).hash.setText(String.valueOf(creditNotesList.get(position).getId()));
            ((Items) holder).tvTicket.setText(String.valueOf(creditNotesList.get(position).getComplaintNo()));
            ((Items) holder).tvAmount.setText(Common.convertInKFormat(creditNotesList.get(position).getAmount()) +
                    SharedPreference.getSimpleString(context, Constants.currency));

            switch (creditNotesList.get(position).getStatus()) {
                case "Pending":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.PendingStatus));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                    ((Items) holder).tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    ((Items) holder).tvStatus.setCompoundDrawablePadding(8);
                    break;
                case "Completed":
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.CompleteStatus));
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
                default:
                    break;
            }

            ((Items) holder).constraintLayout.setOnClickListener(v -> {
                Intent intent = new Intent(context, CreditOrderDetail.class);
                intent.putExtra("id", creditNotesList.get(position).getId());
                intent.putExtra("status", creditNotesList.get(position).getStatus());
                context.startActivity(intent);
            });

        }
    }

    @Override
    public int getItemCount() {
        return creditNotesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return creditNotesList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class Items extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        ImageView btnDetails;
        TextView hash, tvTicket, tvAmount, tvStatus;

        public Items(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.prConstraint);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            hash = itemView.findViewById(R.id.hash);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTicket = itemView.findViewById(R.id.tvTicketNo);

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
