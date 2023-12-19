package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

//import com.elementary.minbio.Models.Response.ProductDetail.ProductDetailModel;
//import com.elementary.minbio.Models.Response.ProductDetail.Seller;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.ProductDetailModel;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Seller;
import com.vic.vicwsp.R;

import java.util.ArrayList;

public class ItemThreePurchaseAdapter extends RecyclerView.Adapter<ItemThreePurchaseAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Seller> sellers;
    private ProductDetailModel productDetailModel;
    public static double cartTotal = 0;
    private Dialog dialog;
    private ViewHolder vholder;

    public ItemThreePurchaseAdapter(Context context, ArrayList<Seller> sellers, ProductDetailModel productDetailModel) {
        this.context = context;
        this.sellers = sellers;
        this.productDetailModel = productDetailModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_purchase_recycler, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "WrongConstant"})
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        vholder = holder;
//        holder.seller.setText(sellers.get(position).getName());
//        holder.price.setText(Common.convertInKFormat((sellers.get(position).getPrice())) +
//                SharedPreference.getSimpleString(context, Constants.currency));
//
//        holder.description.setText(context.getResources().getString(R.string.sale_by_case_of) + " " +
//                sellers.get(position).getSale_case().toString() + " " + productDetailModel.getData().getUnit());
//
//        holder.bio.setVisibility(View.GONE);
//        holder.nego.setVisibility(View.GONE);
//
//        if (sellers.get(position).getIsNego() == 1) {
//            holder.nego.setVisibility(View.VISIBLE);
//            Glide.with(context)
//                    .load(context.getResources().getDrawable(R.drawable.pr))
//                    .into(holder.nego);
//
//        }
//        if (sellers.get(position).getIsBio() == 1) {
//            holder.bio.setVisibility(View.VISIBLE);
//            Glide.with(context)
//                    .load(context.getResources().getDrawable(R.drawable.ab))
//                    .into(holder.bio);
//        }
//        Glide.with(context)
//                .load(sellers.get(position).getFlagImagePath())
//                .into(holder.origin);
//
//        if (sellers.get(position).getHasStock() == 1) {
//            holder.blurry.setVisibility(View.VISIBLE);
//            holder.noMoreStock.setVisibility(View.VISIBLE);
//            holder.description.setVisibility(View.GONE);
//            holder.buy.setEnabled(false);
//            holder.buy.setBackgroundColor(context.getResources().getColor(R.color.blurry));
//            holder.noMoreStock.setText(context.getResources().getString(R.string.this_seller_does_not_have_enough_stock));
//
//        } else if (sellers.get(position).getHasStock() == 0) {
//            holder.blurry.setVisibility(View.GONE);
//            holder.noMoreStock.setVisibility(View.GONE);
//            holder.description.setVisibility(View.VISIBLE);
//            holder.buy.setBackgroundColor(context.getResources().getColor(R.color.splashColor));
//            holder.buy.setEnabled(true);
//        }
//
//        if (Integer.parseInt(sellers.get(position).getId()) == Integer.parseInt(SharedPreference.getSimpleString(context, Constants.userId)))
//            holder.buy.setVisibility(View.GONE);
//        else
//            holder.buy.setVisibility(View.VISIBLE);
//
//
//        if (position == sellers.size() - 1)
//            holder.viewLine.setVisibility(View.GONE);
//        else
//            holder.viewLine.setVisibility(View.VISIBLE);
//
//
//        holder.buy.setOnClickListener(view -> {
//            if (purchaseAmount == 0) {
//
//                showToast((AppCompatActivity) context, context.getResources().getString(R.string.please_enter_desired_quantity), false);
//                return;
//            }
//
//            if (sellers.get(position).getIsNego() == 1) {
//                showNegoDialog(context, position);
//            } else {
//                addToCart(holder, position);
//            }
//        });

    }

    //Showing dialog to ask the user for negotiation or not
//    private void showNegoDialog(Context context, int position) {
//
//        dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.dialog_negotiation);
//
//
//        Button yes = dialog.findViewById(R.id.yesButton);
//        yes.setOnClickListener(v -> {
//            dialog.dismiss();
//
//
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("fromBuy", true);
//            bundle.putBoolean("fromOrderDetails", false);
//
//            ArrayList<NegotiationList> negotiations =
//                    new ArrayList<>();
//
//            NegotiationList negotiationModel =
//                    new NegotiationList(
//                            productDetailModel.getData().getName(), (int) purchaseAmount,
//                            productDetailModel.getData().getSellers().get(position).getPrice(),
//                            String.valueOf(AppUtils.convertStringToDouble(productDetailModel.getData().getSellers().get(position).getPrice()) * purchaseAmount),
//                            0, "", productDetailModel.getData().getUnit());
//
//            negotiations.add(negotiationModel);
//
////            Merchant Id and product Id constructor is added to send data in the negotiation
////            and used in the apis... 3 index -> merchantId 4 index -> productId
//            Data negoData = new Data("", productDetailModel.getData().getSellers().get(position).getName(),
//                    productDetailModel.getData().getSellers().get(position).getMinprice(), productDetailModel.getData().getSellers().get(position).getMaxprice(),
//                    productDetailModel.getData().getSellers().get(position).getId(), String.valueOf(productDetailModel.getData().getId()),
//                    negotiations);
//
//            bundle.putParcelable("NegoData", negoData);
//            Negotiation negotiation = new Negotiation();
//            negotiation.setArguments(bundle);
//            ((AppCompatActivity) context).getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.mainFragment, negotiation, "NegotiationList")
//                    .addToBackStack("NegotiationList")
//                    .commit();
//        });
//
//        Button no = dialog.findViewById(R.id.noButton);
//        no.setOnClickListener(v -> {
//            dialog.dismiss();
//            addToCart(vholder, position);
//        });
//
//        dialog.show();
//
//    }

    //    Adding item to cart
//    private void addToCart(ViewHolder holder, int position) {
//        DatabaseHelper db = new DatabaseHelper(context.getApplicationContext());
//
//        ArrayList<CartModel> cartList;
//        cartList = db.getAllCartData();
//
//        if (cartList.size() == 0) {
//            CartModel cart = new CartModel(productDetailModel.getData().getId(), productDetailModel.getData().getId(),
//                    Integer.parseInt(sellers.get(position).getId()), purchaseAmount, sellers.get(position).getPrice(),
//                    productDetailModel.getData().getName(), productDetailModel.getData().getProductDescription(),
//                    sellers.get(position).getName(), productDetailModel.getData().getImagePath(),
//                    sellers.get(position).getStock(), productDetailModel.getData().getUnit(),
//                    String.valueOf(sellers.get(position).getDiscount()), String.valueOf(sellers.get(position).getVat()));
//            db.insertCart(cart);
//
//            cartList.clear();
//            cartList = db.getAllCartData();
//            cartTotal = getTotal(cartList);
//
//            String msg = context.getResources().getString(R.string.added_to_cart, String.valueOf(purchaseAmount) + productDetailModel.getData().getUnit());
//
//            showToast((AppCompatActivity) context, msg, true);
//
////            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//        } else {
//            for (int j = 0; j < cartList.size(); j++) {
//                if (cartList.get(j).getProduct_id() == productDetailModel.getData().getId()) {
//                    if (cartList.get(j).getMarchant_id() == Integer.parseInt(sellers.get(position).getId())) {
//                        if (cartList.get(j).getQuantity() + purchaseAmount <= sellers.get(position).getStock()) {
//                            CartModel cart = new CartModel(productDetailModel.getData().getId(), productDetailModel.getData().getId(),
//                                    Integer.parseInt(sellers.get(position).getId()), cartList.get(j).getQuantity() + purchaseAmount,
//                                    sellers.get(position).getPrice(), productDetailModel.getData().getName(),
//                                    productDetailModel.getData().getProductDescription(),
//                                    sellers.get(position).getName(), productDetailModel.getData().getImagePath(),
//                                    sellers.get(position).getStock(), productDetailModel.getData().getUnit(),
//                                    String.valueOf(sellers.get(position).getDiscount()), String.valueOf(sellers.get(position).getVat()));
//
//                            db.updateCart(cart);
//
//                            cartList.clear();
//                            cartList = db.getAllCartData();
//                            cartTotal = getTotal(cartList);
//
//                            ((MainActivity) context).updateCart(String.valueOf(round(cartTotal, 2)));
//
//                            String msg = context.getResources().getString(R.string.added_to_cart, String.valueOf(purchaseAmount) + productDetailModel.getData().getUnit());
//
//                            showToast((AppCompatActivity) context, msg, true);
//                            return;
//                        } else {
//                            showToast((AppCompatActivity) context, context.getResources().getString(R.string.this_seller_does_not_have_enough_stock), false);
//                            holder.blurry.setVisibility(View.VISIBLE);
//                            holder.noMoreStock.setVisibility(View.VISIBLE);
//                            holder.description.setVisibility(View.GONE);
//                            holder.buy.setEnabled(false);
//                            return;
//                        }
//                    }
//                }
//            }
//
//
//            CartModel cart = new CartModel(productDetailModel.getData().getId(), productDetailModel.getData().getId(),
//                    Integer.parseInt(sellers.get(position).getId()), purchaseAmount, sellers.get(position).getPrice(),
//                    productDetailModel.getData().getName(), productDetailModel.getData().getProductDescription(),
//                    sellers.get(position).getName(), productDetailModel.getData().getImagePath(),
//                    sellers.get(position).getStock(), productDetailModel.getData().getUnit(),
//                    String.valueOf(sellers.get(position).getDiscount()), String.valueOf(sellers.get(position).getVat()));
//
//            db.insertCart(cart);
//
//            cartList.clear();
//            cartList = db.getAllCartData();
//            cartTotal = getTotal(cartList);
//
//            String msg = context.getResources().getString(R.string.added_to_cart, String.valueOf(purchaseAmount) + productDetailModel.getData().getUnit());
//
//            showToast((AppCompatActivity) context, msg, true);
//        }
//        db.close();
//        ((MainActivity) context).updateCart(String.valueOf(round(cartTotal, 2)));
//    }


    @Override
    public int getItemCount() {
        return sellers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView seller, description, price, noMoreStock;
        ImageView nego, bio, origin;
        Button buy;
        private ConstraintLayout blurry;
        private View viewLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            seller = itemView.findViewById(R.id.tvVendeurRecycler);
            description = itemView.findViewById(R.id.tvVendeurDescriptionRecycler);
            price = itemView.findViewById(R.id.tvPriceRecycler);
            nego = itemView.findViewById(R.id.ivNegoRecycler);
            bio = itemView.findViewById(R.id.ivBioRecycler);
            origin = itemView.findViewById(R.id.ivOriginRecycler);
            buy = itemView.findViewById(R.id.btnPurchaseRecycler);
            noMoreStock = itemView.findViewById(R.id.noMoreStock);
            blurry = itemView.findViewById(R.id.blurryConstraint);

            viewLine = itemView.findViewById(R.id.view);
        }
    }
}
