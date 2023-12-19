package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.NegoUpdated.NegotiationList;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public class ReceivedNegoAdapter extends RecyclerView.Adapter<ReceivedNegoAdapter.ViewHolder> {

    private ArrayList<NegotiationList> negotiations;    //list of negotiations
    private Context context;

    public ReceivedNegoAdapter(ArrayList<NegotiationList> negotiations, Context context) {
        this.negotiations = negotiations;
        this.context = context;
    }

    @NonNull
    @Override
    public ReceivedNegoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recieved_nego, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReceivedNegoAdapter.ViewHolder holder, int position) {

        holder.productName.setText(negotiations.get(position).getProductName());
        holder.quantity.setText(valueOf(negotiations.get(position).getQuantity()) + negotiations.get(position).getUnit());

        if (position == negotiations.size() - 1)
            holder.pricePerKg.setText(context.getResources().getString(R.string.yourPrice) + "  " + negotiations.get(position).getProductPrice() + SharedPreference.getSimpleString(context, Constants.currency));
        else
            holder.pricePerKg.setText(context.getResources().getString(R.string.proposalPrice) + "  " + Common.round(AppUtils.convertStringToDouble(negotiations.get(position).getTotal()) / negotiations.get(position).getQuantity(), 2)
                    + SharedPreference.getSimpleString(context, Constants.currency));

        holder.totalPrice.setText(negotiations.get(position).getTotal() + SharedPreference.getSimpleString(context, Constants.currency));

//        if (negotiations.get(position).getStatus().equals("Accepted"))
//            holder.totalPrice.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
//        else if (negotiations.get(position).getStatus().equals("Rejected"))
//            holder.totalPrice.setTextColor(context.getResources().getColor(R.color.red));

        switch (negotiations.get(position).getStatus()) {
            case "Accepted":
                holder.totalPrice.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                break;
            case "Rejected":
            case "Pending":
                //                holder.totalPrice.setTextColor(context.getResources().getColor(R.color.red));
                highlightPrices(position, holder.totalPrice);
                break;
            default:
                break;
        }

    }

    private void highlightPrices(int position, TextView totalPrice) {

//        if (position < negotiations.size()) {
            if (negotiations.get(position).getIsMerchant() == 1) {
                totalPrice.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
            } else {
                totalPrice.setTextColor(context.getResources().getColor(R.color.red));
            }
//        }

    }

    @Override
    public int getItemCount() {
        return negotiations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName, quantity, pricePerKg, totalPrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.tvPaymentProductName);
            quantity = itemView.findViewById(R.id.tvPaymentQuantity);
            pricePerKg = itemView.findViewById(R.id.tvPaymentPricePerKg);
            totalPrice = itemView.findViewById(R.id.tvPaymentTotalPrice);

        }
    }
}
