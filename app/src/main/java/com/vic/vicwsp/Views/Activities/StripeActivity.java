package com.vic.vicwsp.Views.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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

public class StripeActivity extends AppCompatActivity {

    private Stripe stripe;
    private CardMultilineWidget cardMultilineWidget;
    private Card card;
    private ConstraintLayout btnConfirmCard;
    boolean fromCart, fromOrderDetails;
    private ImageView back;
    private ArrayList<CartModel> carts = new ArrayList<>();
    private AlertDialog alertDialog;
    private int orderId = 0;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);

        fromCart = getIntent().getBooleanExtra("fromCart", false);
        fromOrderDetails = getIntent().getBooleanExtra("fromOrderDetails", false);
        orderId = getIntent().getIntExtra("orderId", 0);

        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(view -> finish());
//        stripe = new Stripe(StripeActivity.this, getResources().getString(R.string.stripe__key));
        stripe = new Stripe(StripeActivity.this, getResources().getString(R.string.test_stripe__key));
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
                    showToast(StripeActivity.this, getResources().getString(R.string.invalid_card), false);
                    return;
                }
                Common.hideKeyboard(StripeActivity.this);
                dialog.show();

                stripe.createToken(cardToSave, new ApiResultCallback<Token>() {
                    public void onSuccess(Token token) {
                        Intent tokenIntent = new Intent("TokenFromStripe");
                        tokenIntent.putExtra("tokenKey", token.getId());

                        if (fromCart && !fromOrderDetails) {
                            try {
                                checkCart(token.getId(), StripeActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (fromOrderDetails && !fromCart) {
                            try {
                                callProposalPaymentApi(token.getId(), getApplicationContext());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        token = null;
                    }

                    public void onError(Exception error) {
                        Log.d("Stripe Activity", "onError: " + error.getLocalizedMessage());
                        showToast(StripeActivity.this, error.getLocalizedMessage(),false);
                        dialog.dismiss();
                    }
                });
            }
        });

        ImageView headerLogo = findViewById(R.id.headerLogo);
        headerLogo.setOnClickListener(v -> {
            Common.hideKeyboard(this);
            Common.goToMain(this);
        });
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
                shippingLatlng.latitude, shippingLatlng.longitude, shippingAddress, token, deliveryType,
                "", 0, "card", deliveryPickupDate, 0,"");

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
//                                    sendToOrders();
                                    finish();
                                })
                                .show();

//                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
//                        databaseHelper.dropCart();
//                        databaseHelper.close();


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
                SharedPreference.getSimpleString(context, Constants.accessToken), orderId, token, "", "card", 0);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        alertDialog = new AlertDialog.Builder(StripeActivity.this)
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
                        alertDialog = new AlertDialog.Builder(StripeActivity.this)
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

        SharedPreference.saveSimpleString(StripeActivity.this, Constants.afterPayementSendToOrders, Constants.afterPayementSendToOrders);

        Intent intent = new Intent(StripeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


}
