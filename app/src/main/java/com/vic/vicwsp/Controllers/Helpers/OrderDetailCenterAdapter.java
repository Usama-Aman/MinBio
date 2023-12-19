package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.OrdersHistory.Product;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import java.util.List;

public class OrderDetailCenterAdapter extends RecyclerView.Adapter<OrderDetailCenterAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;
    private boolean fromReceived;

    public OrderDetailCenterAdapter(Context context, List<Product> products, boolean fromReceived) {
        this.context = context;
        this.products = products;
        this.fromReceived = fromReceived;
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

        holder.tvOrderDetailPrice.setText(Common.convertInKFormat((products.get(position).getSubtotal())) +
                SharedPreference.getSimpleString(context, Constants.currency));

        holder.tvOrderDetailCategoryName.setText(products.get(position).getProductCategory());
        holder.tvDetailQuanity.setText(products.get(position).getQuantity());
        holder.tvOrderDetailName.setText(products.get(position).getProduct());
        holder.orderDetailUnit.setText(products.get(position).getUnit());

        if (!fromReceived)
            holder.tvDetailProductDescription.setText(context.getResources().getString(R.string.soldBy) + " " + products.get(position).getMerchant());
        else
            holder.tvDetailProductDescription.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDetailProductDescription, tvOrderDetailCategoryName, tvDetailQuanity, tvOrderDetailPrice, tvOrderDetailName, orderDetailUnit;
        ConstraintLayout detailLinear;
        ConstraintLayout orderDetailConstraint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDetailProductDescription = itemView.findViewById(R.id.tvDetailProductDescription);
            detailLinear = itemView.findViewById(R.id.detailLinear);
            tvDetailQuanity = itemView.findViewById(R.id.tvDetailQuanity);
            orderDetailConstraint = itemView.findViewById(R.id.orderDetailConstraint);
            tvOrderDetailPrice = itemView.findViewById(R.id.tvOrderDetailPrice);
            tvOrderDetailName = itemView.findViewById(R.id.tvOrderDetailProductName);
            tvOrderDetailCategoryName = itemView.findViewById(R.id.tvOrderDetailCategoryName);
            orderDetailUnit = itemView.findViewById(R.id.orderDetailUnit);
        }
    }
}
