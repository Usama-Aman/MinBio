package com.vic.vicwsp.Controllers.Helpers.CredtiNotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Models.Response.SaveCards.Datum;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Views.Activities.CreditNotes.CreditSavedCards;

import java.util.ArrayList;

public class CreditSavedCardsAdapter extends RecyclerView.Adapter<CreditSavedCardsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Datum> saveCardsArrayList;
    private CreditSavedCards saveCards;
    private AlertDialog alertDialog;

    public CreditSavedCardsAdapter(ArrayList<Datum> saveCardsArrayList, CreditSavedCards saveCards) {
        this.saveCardsArrayList = saveCardsArrayList;
        this.saveCards = saveCards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_cards, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.cardHolderName.setText(saveCardsArrayList.get(position).getName());
        holder.cardLastNo.setText(String.valueOf(saveCardsArrayList.get(position).getLast4()));
        holder.tvExpiryNo.setText(saveCardsArrayList.get(position).getExpMonth() + "/" + saveCardsArrayList.get(position).getExpYear());

        checkForCardType(saveCardsArrayList.get(position).getCard_type(), holder);

        holder.cardsConstraint.setOnClickListener(v -> {
            saveCards.doPayment(saveCardsArrayList.get(position).getCardId());
        });

        holder.imgDeleteCard.setOnClickListener(v -> {

            alertDialog = new AlertDialog.Builder(context)
                    .setMessage(context.getResources().getString(R.string.deleteCard))
                    .setPositiveButton(context.getResources()
                            .getString(R.string.yes), (dialog, which) -> {
                        saveCards.deleteCard(saveCardsArrayList.get(position).getId(),position);
                    })
                    .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    }).show();

        });
    }

    private void checkForCardType(String card_type, ViewHolder holder) {
        if (card_type.equalsIgnoreCase("Visa")) {
            Glide.with(context).
                    load(context.getResources().getDrawable(R.drawable.ic_visaa, null))
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder, null))
                    .into(holder.imgCard);
        } else if (card_type.equalsIgnoreCase("American Express")) {
            Glide.with(context).
                    load(context.getResources().getDrawable(R.drawable.ic_american_express, null))
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder, null))
                    .into(holder.imgCard);
        } else if (card_type.equalsIgnoreCase("Diners Club")) {
            Glide.with(context).
                    load(context.getResources().getDrawable(R.drawable.ic_diners_club, null))
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder, null))
                    .into(holder.imgCard);
        } else if (card_type.equalsIgnoreCase("Discover")) {
            Glide.with(context).
                    load(context.getResources().getDrawable(R.drawable.ic_discover_card, null))
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder, null))
                    .into(holder.imgCard);
        } else if (card_type.equalsIgnoreCase("JCB")) {
            Glide.with(context).
                    load(context.getResources().getDrawable(R.drawable.ic_jcb_card, null))
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder, null))
                    .into(holder.imgCard);
        } else if (card_type.equalsIgnoreCase("MasterCard")) {
            Glide.with(context).
                    load(context.getResources().getDrawable(R.drawable.ic_master, null))
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder, null))
                    .into(holder.imgCard);
        } else if (card_type.equalsIgnoreCase("UnionPay")) {
            Glide.with(context).
                    load(context.getResources().getDrawable(R.drawable.ic_unionpay_card, null))
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder, null))
                    .into(holder.imgCard);
        } else if (card_type.equalsIgnoreCase("Unknown")) {
            Glide.with(context).
                    load(context.getResources().getDrawable(R.drawable.ic_unknown_card, null))
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder, null))
                    .into(holder.imgCard);
        }

    }


    @Override
    public int getItemCount() {
        return saveCardsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cardHolderName, cardLastNo, tvExpiryNo;
        ConstraintLayout cardsConstraint;
        ImageView imgCard, imgDeleteCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardHolderName = itemView.findViewById(R.id.cardHolderName);
            cardLastNo = itemView.findViewById(R.id.cardLastNo);
            tvExpiryNo = itemView.findViewById(R.id.tvExpiryNo);
            cardsConstraint = itemView.findViewById(R.id.cardsConstraint);
            imgCard = itemView.findViewById(R.id.imgCard);
            imgDeleteCard = itemView.findViewById(R.id.imgDeleteCard);

        }
    }
}
