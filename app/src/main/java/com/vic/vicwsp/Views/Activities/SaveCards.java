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
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Helpers.SaveCardAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Models.Request.Cart.Product;
import com.vic.vicwsp.Models.Request.Cart.Product_;
import com.vic.vicwsp.Models.Response.CartOrder.CartOrderModel;
import com.vic.vicwsp.Models.Response.SaveCards.Datum;
import com.vic.vicwsp.Models.Response.SaveCards.SaveCardsModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

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
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryPickupDate;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryType;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingAddress;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingDeliveryCharges;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingLatlng;

public class SaveCards extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SaveCardAdapter saveCardAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean fromCart, fromOrderDetails;
    private int orderId = 0;
    private ImageView back, btnAddCard;
    private ArrayList<CartModel> carts = new ArrayList<>();
    private ProgressDialog dialog;
    private ImageView ivListNull;
    private TextView tvListNull;

    private ArrayList<Datum> saveCardsModelArrayList;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cards);

        getDataFromBundle();
        initViews();
        setUpAdapter();
        getCardsFromApi();
    }

    private void getDataFromBundle() {
        fromCart = getIntent().getBooleanExtra("fromCart", false);
        fromOrderDetails = getIntent().getBooleanExtra("fromOrderDetails", false);
        orderId = getIntent().getIntExtra("orderId", 0);
    }

    private void initViews() {
        saveCardsModelArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerSaveCards);

        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(view -> finish());

        btnAddCard = findViewById(R.id.ivAddCard);
        btnAddCard.setOnClickListener(view -> {

            Intent intent = new Intent(SaveCards.this, StripeActivity.class);
            intent.putExtra("fromCart", fromCart);
            intent.putExtra("fromOrderDetails", fromOrderDetails);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });

        ivListNull = findViewById(R.id.ivListNull);
        tvListNull = findViewById(R.id.tvListNull);
    }

    private void setUpAdapter() {
        saveCardAdapter = new SaveCardAdapter(saveCardsModelArrayList, this);
        linearLayoutManager = new LinearLayoutManager(SaveCards.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(saveCardAdapter);
    }

    private void getCardsFromApi() {

        Common.showDialog(SaveCards.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<SaveCardsModel> call = api.getUserCards("Bearer " +
                SharedPreference.getSimpleString(SaveCards.this, Constants.accessToken));
        call.enqueue(new Callback<SaveCardsModel>() {
            @Override
            public void onResponse(Call<SaveCardsModel> call, Response<SaveCardsModel> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {

                        saveCardsModelArrayList.clear();

                        SaveCardsModel saveCardsModel = new SaveCardsModel(response.body().getData(), response.body().getStatus(), response.body().getMessage());
                        saveCardsModelArrayList.addAll(saveCardsModel.getData());

                        if (saveCardsModelArrayList.size() <= 0) {
                            ivListNull.setVisibility(View.VISIBLE);
                            tvListNull.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            ivListNull.setVisibility(View.GONE);
                            tvListNull.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            saveCardAdapter.notifyDataSetChanged();
                        }

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SaveCards.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SaveCardsModel> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });
    }

    public void doPayment(String cardId) {

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getResources().getString(R.string.loading));
        dialog.setMessage(getResources().getString(R.string.loading_please_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        if (fromCart && !fromOrderDetails) {
            try {
                checkCart(cardId, SaveCards.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (fromOrderDetails && !fromCart) {
            try {
                callProposalPaymentApi(cardId, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Making the request data on payment check out and calling the api for payment
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
                shippingLatlng.latitude, shippingLatlng.longitude, shippingAddress, token, deliveryType, "", 0,
                "card", deliveryPickupDate, 1,"");

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
                }
            }

            @Override
            public void onFailure(Call<CartOrderModel> call, Throwable t) {
                Log.d(Constraints.TAG, "onFailure: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

    //Calling payment proposal api when the merchant accepts the user negotiation '
    private void callProposalPaymentApi(String token, Context context) throws Exception {

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.postPaymentProposal("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), orderId, token, "", "card", 1);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        alertDialog = new AlertDialog.Builder(SaveCards.this)
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
                        alertDialog = new AlertDialog.Builder(SaveCards.this)
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

    private void sendToOrders() {

        SharedPreference.saveSimpleString(SaveCards.this, Constants.afterPayementSendToOrders, Constants.afterPayementSendToOrders);

        Intent intent = new Intent(SaveCards.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void deleteCard(int id, int position) {

        Common.showDialog(SaveCards.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> call = api.deleteUserCard("Bearer " +
                SharedPreference.getSimpleString(SaveCards.this, Constants.accessToken), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        saveCardsModelArrayList.remove(position);

                        if (saveCardsModelArrayList.size() <= 0) {
                            ivListNull.setVisibility(View.VISIBLE);
                            tvListNull.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            ivListNull.setVisibility(View.GONE);
                            tvListNull.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            saveCardAdapter.notifyDataSetChanged();
                        }
                        showToast(SaveCards.this, jsonObject.getString("message"), true);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SaveCards.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });

    }

}
