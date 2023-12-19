package com.vic.vicwsp.Controllers.Helpers.CredtiNotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.CreditNotesDetails.Item;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import java.util.List;

public class CreditDetailCenterAdapter extends RecyclerView.Adapter<CreditDetailCenterAdapter.ViewHolder> {

    private Context context;
    private List<Item> items;

    public CreditDetailCenterAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_recycler, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position % 2 == 1) {
            holder.orderDetailConstraint.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.detailLinear.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.transparent_edittext));
        } else {
            holder.orderDetailConstraint.setBackgroundColor(context.getResources().getColor(R.color.catalogueBackground));
            holder.detailLinear.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.transparent_edittext));
        }

        holder.tvOrderDetailPrice.setText(Common.convertInKFormat((items.get(position).getTotal())) +
                SharedPreference.getSimpleString(context, Constants.currency));

        holder.tvOrderDetailCategoryName.setText(items.get(position).getProduct());
        holder.tvOrderDetailName.setText(items.get(position).getProductVariety());
        holder.tvDetailQuanity.setText(items.get(position).getQuantity());
        holder.orderDetailUnit.setText(items.get(position).getUnit());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDetailProductDescription, tvOrderDetailCategoryName, tvDetailQuanity, tvOrderDetailPrice, tvOrderDetailName, orderDetailUnit;
        ConstraintLayout detailLinear;
        ConstraintLayout orderDetailConstraint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            detailLinear = itemView.findViewById(R.id.detailLinear);
            tvDetailQuanity = itemView.findViewById(R.id.tvDetailQuanity);
            orderDetailConstraint = itemView.findViewById(R.id.orderDetailConstraint);
            tvOrderDetailPrice = itemView.findViewById(R.id.tvOrderDetailPrice);
            orderDetailUnit = itemView.findViewById(R.id.orderDetailUnit);

            tvOrderDetailCategoryName = itemView.findViewById(R.id.tvOrderDetailCategoryName);
            tvOrderDetailName = itemView.findViewById(R.id.tvOrderDetailProductName);

            tvDetailProductDescription = itemView.findViewById(R.id.tvDetailProductDescription);
            tvDetailProductDescription.setVisibility(View.GONE);

        }
    }
}