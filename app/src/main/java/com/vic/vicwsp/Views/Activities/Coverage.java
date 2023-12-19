package com.vic.vicwsp.Views.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Models.Request.Cart.Product;
import com.vic.vicwsp.Models.Request.Cart.Product_;
import com.vic.vicwsp.Models.Response.CartOrder.CartOrderModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Controllers.Helpers.ItemThreePurchaseAdapter.cartTotal;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryPickupDate;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryType;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingAddress;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingDeliveryCharges;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingLatlng;

public class Coverage extends AppCompatActivity {

    private ImageView coverageback;
    private double amount = 0.0;
    private String message = "";
    private TextView tvCoverageText;
    private ConstraintLayout credit, token, sepa;
    private ConstraintLayout btnPlaceOrder;
    private ArrayList<CartModel> carts = new ArrayList<>();
    private ProgressDialog dialog;
    private AlertDialog alertDialog;
    private String fromWhere = "";
    private int orderId = 0;

    private String overDraftLogs = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coverage);
        initViews();

    }

    private void initViews() {
        coverageback = findViewById(R.id.coverageback);
        coverageback.setOnClickListener(view -> finish());

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(view -> {
            dialog.show();
            checkCart("", this, makeJSONArray());
        });

        credit = findViewById(R.id.credit);
        credit.setOnClickListener(view -> {

            Intent intent = new Intent(Coverage.this, CoverageSaveCards.class);
            intent.putExtra("fromWhere", fromWhere);
            intent.putExtra("overdraft_logs", overDraftLogs);
            if (fromWhere.equals("OrderDetail") || fromWhere.equals("Splash"))
                intent.putExtra("orderId", orderId);
            startActivity(intent);
        });

        tvCoverageText = findViewById(R.id.tvCoverageText);
        token = findViewById(R.id.token);
        sepa = findViewById(R.id.sepa);

        getDataFromBundleAndPopulate();

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getResources().getString(R.string.loading));
        dialog.setMessage(getResources().getString(R.string.loading_please_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private void getDataFromBundleAndPopulate() {

        amount = AppUtils.convertStringToDouble(getIntent().getStringExtra("amount"));
        message = getIntent().getStringExtra("message");
        fromWhere = getIntent().getStringExtra("fromWhere");

        if (fromWhere.equalsIgnoreCase("Cart")) {
            overDraftLogs = getIntent().getStringExtra("overdraft_logs");
            if (overDraftLogs == null)
                overDraftLogs = "";
        }


        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        carts = databaseHelper.getAllCartData();
        double total = 0.0;
        double discount = 0.0;
        double vat = 0.0;

        for (int i = 0; i < carts.size(); i++) {
            total += AppUtils.convertStringToDouble(carts.get(i).getPrice()) * carts.get(i).getQuantity();
            discount += (AppUtils.convertStringToDouble(carts.get(i).getPrice()) * carts.get(i).getQuantity() / 100) * AppUtils.convertStringToDouble(carts.get(i).getDiscount());
            vat += (AppUtils.convertStringToDouble(carts.get(i).getPrice()) * carts.get(i).getQuantity() / 100) * AppUtils.convertStringToDouble(carts.get(i).getVat());
        }

        total = total + vat - discount;

        databaseHelper.close();

        if (fromWhere.equals("Cart")) {
            if (total + AppUtils.convertStringToDouble(shippingDeliveryCharges) <= amount) {
                tvCoverageText.setText(getResources().getString(R.string.amountLessThanCoverage, String.valueOf(Common.round(amount, 2)) + SharedPreference.getSimpleString(this, Constants.currency)));

                credit.setVisibility(View.GONE);
                token.setVisibility(View.GONE);
                sepa.setVisibility(View.GONE);
                btnPlaceOrder.setVisibility(View.VISIBLE);

            } else {

                String diff = ((String.valueOf(total + AppUtils.convertStringToDouble(shippingDeliveryCharges) - amount)));

                tvCoverageText.setText(getResources().getString(R.string.amountGreatorThanCoverage,
                        SharedPreference.getSimpleString(this, Constants.currency) + String.valueOf(Common.round(amount, 2)),
                        SharedPreference.getSimpleString(this, Constants.currency) + String.valueOf(Common.round(AppUtils.convertStringToDouble(diff), 2))));

                credit.setVisibility(View.VISIBLE);
                token.setVisibility(View.VISIBLE);
                sepa.setVisibility(View.VISIBLE);

                btnPlaceOrder.setVisibility(View.GONE);
            }
        } else if (fromWhere.equals("OrderDetail") || fromWhere.equals("Splash")) {

            orderId = getIntent().getIntExtra("orderId", 0);

            tvCoverageText.setText(message);

            credit.setVisibility(View.VISIBLE);
            token.setVisibility(View.VISIBLE);
            sepa.setVisibility(View.VISIBLE);

            btnPlaceOrder.setVisibility(View.GONE);
        }


    }

    private JSONArray makeJSONArray() {

        ArrayList<Integer> merchants = new ArrayList<>();
        merchants.clear();

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
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

        Log.d("Tag", "checkCart json Strong" + json);

        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    // Making the request data on payment check out and calling the api for payment
    private void checkCart(String token, Context context, JSONArray jsonArray) {

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<CartOrderModel> checkCart = api.checkCart("Bearer " +
                        SharedPreference.getSimpleString(context, Constants.accessToken), jsonArray,
                shippingLatlng.latitude, shippingLatlng.longitude, shippingAddress, token, deliveryType,
                "", 1, "card", deliveryPickupDate, 0, overDraftLogs);

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
                                .setNegativeButton(getResources().getString(R.string.cancel), (dialogInterface, i) -> {
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
                                    finish();
                                })
                                .show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    shippingAddress = "";
                    shippingDeliveryCharges = "0.00";
                    shippingLatlng = new LatLng(0, 0);
                    deliveryType = "";
                    deliveryPickupDate = "";
                }
            }

            @Override
            public void onFailure(Call<CartOrderModel> call, Throwable t) {
                Log.d(Constraints.TAG, "onFailure: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

    private void sendToOrders() {

        SharedPreference.saveSimpleString(Coverage.this, Constants.afterPayementSendToOrders, Constants.afterPayementSendToOrders);

        Intent intent = new Intent(Coverage.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
