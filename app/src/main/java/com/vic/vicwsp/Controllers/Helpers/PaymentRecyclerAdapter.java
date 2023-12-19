package com.vic.vicwsp.Controllers.Helpers;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Models.Request.Cart.Product;
import com.vic.vicwsp.Models.Request.Cart.Product_;
import com.vic.vicwsp.Models.Response.CartOrder.CartOrderModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.SaveCards;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Controllers.Helpers.ItemThreePurchaseAdapter.cartTotal;
import static com.vic.vicwsp.Utils.Constants.mLastClickTime;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryPickupDate;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryType;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingAddress;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingDeliveryCharges;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingLatlng;

public class PaymentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public Context context;
    private ArrayList<CartModel> carts;
    private ItemFooter itemFooter;
    private ItemHeader itemHeader;
    private AlertDialog alertDialog;
    private int orderId;
    private boolean fromOrderDetails;
    private String total;
    private ProgressDialog dialog;


    public PaymentRecyclerAdapter(Context context, ArrayList<CartModel> carts, int orderId, boolean fromOrderDetails, String total) {
        this.context = context;
        this.carts = carts;
        this.orderId = orderId;
        this.fromOrderDetails = fromOrderDetails;
        this.total = total;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
            return new ItemHeader(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_footer, parent, false);
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
                itemFooter = ((ItemFooter) holder);
                itemFooter.bind(position);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (carts.size() == 1) {
            return position;
        } else {
            if (position == carts.size())
                return 1;
            else
                return 0;
        }
    }


    @Override
    public int getItemCount() {
        return carts.size() + 1;
    }

    public class ItemHeader extends RecyclerView.ViewHolder {

        TextView productName, quantity, pricePerKg, totalPrice, companyName, tvPaymentProductVariety;

        public ItemHeader(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tvPaymentProductName);
            quantity = itemView.findViewById(R.id.tvPaymentQuantity);
            pricePerKg = itemView.findViewById(R.id.tvPaymentPricePerKg);
            totalPrice = itemView.findViewById(R.id.tvPaymentTotalPrice);
            companyName = itemView.findViewById(R.id.tvPaymentProductCompanyName);
            tvPaymentProductVariety = itemView.findViewById(R.id.tvPaymentProductVariety);

        }

        @SuppressLint("SetTextI18n")
        public void bind(final int position) {
            productName.setText(carts.get(position).getProduct_name());
            tvPaymentProductVariety.setText(carts.get(position).getProductVariety());

            companyName.setVisibility(View.GONE);
            companyName.setText(carts.get(position).getCompanyName());
            companyName.setVisibility(View.VISIBLE);

            quantity.setText(String.valueOf(carts.get(position).getQuantity()) + " " + carts.get(position).getUnit());

            pricePerKg.setText(carts.get(position).getPrice() + SharedPreference.getSimpleString
                    (context, Constants.currency));

            if (fromOrderDetails)
                totalPrice.setText(AppUtils.convertStringToDouble(total)
                        + SharedPreference.getSimpleString(context, Constants.currency));
            else {

                double discount = (AppUtils.convertStringToDouble(carts.get(position).getPrice()) * carts.get(position).getQuantity() / 100) * AppUtils.convertStringToDouble(carts.get(position).getDiscount());
                double vat = (AppUtils.convertStringToDouble(carts.get(position).getPrice()) * carts.get(position).getQuantity() / 100) * AppUtils.convertStringToDouble(carts.get(position).getVat());


                double v = AppUtils.convertStringToDouble(carts.get(position).getPrice()) * carts.get(position).getQuantity();
                v = (v + vat) - discount;

                totalPrice.setText(String.valueOf(BigDecimal.valueOf(Math.floor(v * 100) / 100))
                        + SharedPreference.getSimpleString(context, Constants.currency));
            }
        }
    }

    public class ItemFooter extends RecyclerView.ViewHolder {

        private TextView footerText;
        private ConstraintLayout cash, credit, token, sepa, cheque, paymentFooterConstraint;

        ItemFooter(@NonNull View itemView) {
            super(itemView);
            footerText = itemView.findViewById(R.id.tvFooterText);
            sepa = itemView.findViewById(R.id.sepa);
            token = itemView.findViewById(R.id.token);
            credit = itemView.findViewById(R.id.credit);
            cash = itemView.findViewById(R.id.cash);
            paymentFooterConstraint = itemView.findViewById(R.id.paymentFooterConstraint);
        }

        @SuppressLint("SetTextI18n")
        void bind(int position) {

            if (fromOrderDetails) {

                if (carts.get(carts.size() - 1).getDelivery_type().equals("pickup_delivery")) {
                    cash.setVisibility(View.VISIBLE);
                    cash.setEnabled(true);
                } else {
                    cash.setVisibility(View.GONE);
                    cash.setEnabled(false);
                }

            } else {

                if (deliveryType.equals(Constants.pickupDelivery)) {
                    cash.setVisibility(View.VISIBLE);
                    cash.setEnabled(true);
                } else {
                    cash.setVisibility(View.GONE);
                    cash.setEnabled(false);
                }
            }
            credit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (fromOrderDetails) {
                        goToSaveCards(false, true);
                    } else
                        goToSaveCards(true, false);
                }
            });

            cash.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                try {

                    if (fromOrderDetails)
                        cashOnDeliveryApi(orderId);
                    else
                        checkCart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        }
    }


    // Making the request data on payment check out and calling the api for payment

    private void checkCart() throws Exception {

        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(context.getResources().getString(R.string.loading));
        dialog.setMessage(context.getResources().getString(R.string.loading_please_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        ArrayList<Integer> merchants = new ArrayList<>();
        merchants.clear();

        DatabaseHelper db = new DatabaseHelper(context);
        carts = db.getAllCartData();
        db.close();


        for (int i = 0; i < carts.size(); i++) {
            merchants.add(carts.get(i).getMarchant_id());
        }

        Set<Integer> set = new HashSet<>(merchants);
        merchants.clear();
        merchants.addAll(set);

        ArrayList<Product> products = new ArrayList<>();
        for (int m = 0; m < merchants.size(); m++) {
            Product product = new Product();
            product.setMerchantId(merchants.get(m));
            ArrayList<Product_> product_s = new ArrayList<>();
            for (int c = 0; c < carts.size(); c++) {
                if (merchants.get(m) == carts.get(c).getMarchant_id()) {
                    product_s.add(new Product_(String.valueOf(carts.get(c).getProduct_id()), String.valueOf(carts.get(c).getQuantity()),
                            carts.get(c).getDiscount(), carts.get(c).getVat()));
                }
            }
            product.setProducts(product_s);
            products.add(product);
        }

        Gson gson = new Gson();
        String json = gson.toJson(products);


        JSONArray jsonArray = new JSONArray(json);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<CartOrderModel> checkCart = api.checkCart("Bearer " +
                        SharedPreference.getSimpleString(context, Constants.accessToken), jsonArray,
                shippingLatlng.latitude, shippingLatlng.longitude, shippingAddress, "", deliveryType, "pre_order",
                0, "cod", deliveryPickupDate, 0,"");

        checkCart.enqueue(new Callback<CartOrderModel>() {
            @Override
            public void onResponse(Call<CartOrderModel> call, Response<CartOrderModel> response) {

                dialog.dismiss();
                boolean isSuccess = false;
                if (response.isSuccessful()) {
                    CartOrderModel cartOrderModel = new CartOrderModel(response.body().getStatus(),
                            response.body().getMessage(), response.body().getData());

                    if (cartOrderModel.getData().getFailed().size() == 0) {
                        isSuccess = true;
                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        databaseHelper.dropCart();
                        databaseHelper.close();

                        cartTotal = 0;
                        carts.clear();

                    } else {
                        isSuccess = false;

                        DatabaseHelper databaseHelper = new DatabaseHelper(context);

                        boolean isFailed = false;

                        for (int c = 0; c < carts.size(); c++) {
                            for (int f = 0; f < cartOrderModel.getData().getFailed().size(); f++) {
                                if (carts.get(c).getMarchant_id() == cartOrderModel.getData().getFailed().get(f).getMerchantId()) {
                                    for (int p = 0; p < cartOrderModel.getData().getFailed().get(f).getProducts().size(); p++) {
                                        if (carts.get(c).getProduct_id() ==
                                                Integer.parseInt(cartOrderModel.getData().getFailed().get(f).getProducts().get(p).getId())) {

                                            isFailed = true;

                                        }
                                    }

                                }
                            }

                            if (!isFailed) {
                                cartTotal = cartTotal - (carts.get(c).getQuantity() * AppUtils.convertStringToDouble(carts.get(c).getPrice()));
                                ((MainActivity) context).updateCart(String.valueOf(cartTotal));
                                databaseHelper.deleteCart(carts.get(c));
                                carts.remove(c);
                                c--;
                            }
                            isFailed = false;
                        }

                    }

                    if (!isSuccess) {

                        alertDialog = new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage(context.getResources().getString(R.string.stockIsNotAvailable))
                                .setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();
                                    sendToOrders();
                                })
                                .setNegativeButton(context.getResources().getString(R.string.cancel), (dialogInterface, i) -> {
                                    alertDialog.dismiss();
                                    sendToOrders();
                                })
                                .show();

                    } else {

                        alertDialog = new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage(context.getResources().getString(R.string.payment_ok))
                                .setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();
                                    sendToOrders();
                                })
                                .show();
                    }

                    DatabaseHelper databaseHelper = new DatabaseHelper(context);
                    databaseHelper.dropCart();
                    databaseHelper.close();

                    shippingAddress = "";
                    shippingDeliveryCharges = "0.00";
                    shippingLatlng = new LatLng(0, 0);
                    deliveryType = "";
                    deliveryPickupDate = "";


                } else {

                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        alertDialog = new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();
                                })
                                .show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<CartOrderModel> call, Throwable t) {
                Log.d(Constraints.TAG, "onFailure: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }


    //Sending to the stripe activity for card data collection
    private void goToSaveCards(boolean fromCart, boolean fromOrderDetails) {
        Intent intent = new Intent(context, SaveCards.class);
        intent.putExtra("fromCart", fromCart);
        intent.putExtra("fromOrderDetails", fromOrderDetails);
        intent.putExtra("orderId", orderId);
        context.startActivity(intent);
    }

    private void sendToOrders() {

        ((MainActivity) context).updateCart("0.00");
        ((MainActivity) context).goToOrders();
    }


    private void cashOnDeliveryApi(int orderId) {

        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(context.getResources().getString(R.string.loading));
        dialog.setMessage(context.getResources().getString(R.string.loading_please_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.postPaymentProposal("Bearer " +
                        SharedPreference.getSimpleString(context, Constants.accessToken), orderId, "",
                "pre_order", "cod", 0);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        alertDialog = new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage(context.getResources().getString(R.string.payment_ok))
                                .setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();
                                    sendToOrders();

                                })
                                .show();


                    } else {
                        dialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        alertDialog = new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();
                                    sendToOrders();

                                })
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Log.d(Constraints.TAG, "onFailure: " + t.getMessage());
            }
        });

    }


}

