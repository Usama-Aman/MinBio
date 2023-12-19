package com.vic.vicwsp.Views.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Controllers.Helpers.ItemThreePurchaseAdapter.cartTotal;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Utils.Constants.mLastClickTime;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryPickupDate;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryType;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingAddress;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingDeliveryCharges;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingLatlng;

public class CoverageStripe extends AppCompatActivity {

    private Stripe stripe;
    private CardMultilineWidget cardMultilineWidget;
    private Card card;
    private ConstraintLayout btnConfirmCard;
    private ImageView back;
    private ArrayList<CartModel> carts = new ArrayList<>();
    private AlertDialog alertDialog;
    private ProgressDialog dialog;
    private String fromWhere = "";
    private int orderId = 0;
    private String overDraftLogs = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);
        initViews();


        fromWhere = getIntent().getStringExtra("fromWhere");
        if (fromWhere.equals("OrderDetail") || fromWhere.equals("Splash"))
            orderId = getIntent().getIntExtra("orderId", 0);

        if (fromWhere.equalsIgnoreCase("Cart")) {
            overDraftLogs = getIntent().getStringExtra("overdraft_logs");
            if (overDraftLogs == null)
                overDraftLogs = "";
        }

    }

    private void initViews() {

        ImageView headerLogo = findViewById(R.id.headerLogo);
        headerLogo.setOnClickListener(v -> {
            Common.hideKeyboard(this);
            Common.goToMain(this);
        });
        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(view -> finish());
//        stripe = new Stripe(CoverageStripe.this, getResources().getString(R.string.stripe__key));
        stripe = new Stripe(CoverageStripe.this, getResources().getString(R.string.test_stripe__key));
        cardMultilineWidget = findViewById(R.id.card_multiline_widget);
        btnConfirmCard = findViewById(R.id.btnOkStripe);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getResources().getString(R.string.loading));
        dialog.setMessage(getResources().getString(R.string.loading_please_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        btnConfirmCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                final Card cardToSave = cardMultilineWidget.getCard();

                if (cardToSave == null) {
                    showToast(CoverageStripe.this, getResources().getString(R.string.invalid_card), false);
                    return;
                }
                Common.hideKeyboard(CoverageStripe.this);
                dialog.show();

                stripe.createToken(cardToSave, new ApiResultCallback<Token>() {
                    public void onSuccess(Token token) {
                        Log.d("", "onSuccess: " + token);
                        Intent tokenIntent = new Intent("TokenFromStripe");
                        tokenIntent.putExtra("tokenKey", token.getId());


                        if (fromWhere.equals("OrderDetail") || fromWhere.equals("Splash")) {

                            callCoveragePaymentApi(token.getId());

                        } else if (fromWhere.equals("Cart")) {
                            try {
                                checkCart(token.getId(), CoverageStripe.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        token = null;
                    }

                    public void onError(Exception error) {
                        Log.d("Stripe Activity", "onError: " + error.getLocalizedMessage());
                        showToast(CoverageStripe.this, error.getLocalizedMessage(),false);
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private void callCoveragePaymentApi(String token) {

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.coveragePayment("Bearer " +
                SharedPreference.getSimpleString(CoverageStripe.this, Constants.accessToken), orderId, token, 0, overDraftLogs);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject = null;

                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());

                        alertDialog = new AlertDialog.Builder(CoverageStripe.this)
                                .setCancelable(false)
                                .setMessage(getResources().getString(R.string.payment_ok))
                                .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();
                                    sendToOrders();
                                })
                                .show();


                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(CoverageStripe.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private void sendToOrders() {

        SharedPreference.saveSimpleString(CoverageStripe.this, Constants.afterPayementSendToOrders, Constants.afterPayementSendToOrders);

        Intent intent = new Intent(CoverageStripe.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void checkCart(String token, Context context) throws Exception {

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

        JSONArray jsonArray = new JSONArray(json);

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
//                        if (response.code() == 422) {
//                            showToast(CoverageStripe.this, response.body().getMessage(), false);
//                        } else {

                        JSONObject jsonObject = new JSONObject(response.errorBody().string());

                        alertDialog = new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();
//                                        sendToOrders();
                                    finish();
                                })
                                .show();

//                            DatabaseHelper databaseHelper = new DatabaseHelper(context);
//                            databaseHelper.dropCart();
//                            databaseHelper.close();

//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


//                    shippingAddress = "";
//                    shippingDeliveryCharges = "0.00";
//                    shippingLatlng = new LatLng(0, 0);
//                    deliveryType = "";
                }
            }

            @Override
            public void onFailure(Call<CartOrderModel> call, Throwable t) {
                Log.d(Constraints.TAG, "onFailure: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

}
