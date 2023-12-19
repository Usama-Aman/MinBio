package com.vic.vicwsp.Views.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.vic.vicwsp.Controllers.Helpers.CustomSearchableSpinner;
import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Helpers.ProductViewSlider;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Models.Response.NegoUpdated.Data;
import com.vic.vicwsp.Models.Response.NegoUpdated.NegotiationList;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.File;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.SubProduct;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.getTotal;
import static com.vic.vicwsp.Utils.Common.showToast;


public class ProductView extends AppCompatActivity {

    private ViewPager mPager;
    private SubProduct subProduct = null;
    private ArrayList<File> files = new ArrayList<>();
    private TextView tvProductName, tvPriceDetails, amountPerBox, productDetailDescription, tvPricePerKgPV, tvSizePV, tvClassPV, tvMinimumBuyAmount;
    private ImageView cross;
    private ConstraintLayout buy;
    private float amount = 0;
    private Dialog dialog;
    private int merchantId = 0;
    private String merchantName = "", productNameStr = "";
    private double cartTotal = 0.0;
    private ConstraintLayout productViewBuyConstraint;
    private CustomSearchableSpinner customSearchableSpinner;
    private ArrayList<String> saleCases = new ArrayList<>();
    private TextView totalPrice;
    private boolean addedToCart = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        getDataFromBundle();
    }

    private void getDataFromBundle() {

        Bundle bundle = getIntent().getExtras();
        subProduct = bundle.getParcelable("SubProduct");
        files = bundle.getParcelableArrayList("Files");
        merchantId = bundle.getInt("merchantId");
        productNameStr = bundle.getString("productName");
        merchantName = bundle.getString("merchantName");
        if (subProduct != null)
            init();
    }


    private void init() {

        initViews();

        mPager = findViewById(R.id.imageSlider);
        mPager.setAdapter(new ProductViewSlider(ProductView.this, files));

        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.createIndicators(files.size(), 0);

        final int[] currentPage = {0};

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicator.animatePageSelected(position);
                currentPage[0] = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int NUM_PAGES = files.size();

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage[0] == NUM_PAGES) {
                    currentPage[0] = 0;
                }
                mPager.setCurrentItem(currentPage[0]++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2000, 2000);

    }

    private void initViews() {

        ImageView headerLogo = findViewById(R.id.headerLogo);
        headerLogo.setOnClickListener(v -> {
            Common.hideKeyboard(this);
            Common.goToMain(this);
        });
        customSearchableSpinner = findViewById(R.id.pvSpinner);
        productViewBuyConstraint = findViewById(R.id.productViewBuyConstraint);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductName.setText(subProduct.getProductVariety());
        tvPriceDetails = findViewById(R.id.tvPriceDetails);
        productDetailDescription = findViewById(R.id.productDetailDescription);
        tvPricePerKgPV = findViewById(R.id.tvPricePerKgPV);
        tvClassPV = findViewById(R.id.tClassPV);
        tvSizePV = findViewById(R.id.tvSizePV);
        tvMinimumBuyAmount = findViewById(R.id.tvMinimumBuyAmount);
        cross = findViewById(R.id.cross);
        buy = findViewById(R.id.productViewBuy);
        amountPerBox = findViewById(R.id.amountPerBox);
        totalPrice = findViewById(R.id.totalPricePV);

        productDetailDescription.setText(subProduct.getPro_description());
        tvPriceDetails.setText(Common.convertInKFormat((subProduct.getPrice())) + SharedPreference.getSimpleString(ProductView.this, Constants.currency) + " " + getResources().getString(R.string.buyingPerUnit, subProduct.getUnit()));
        tvPricePerKgPV.setText(getResources().getString(R.string.subProductPrice, subProduct.getUnit()) + " " + subProduct.getPrice() + SharedPreference.getSimpleString(ProductView.this, Constants.currency));
        tvSizePV.setText(getResources().getString(R.string.subProductSize, subProduct.getSize()));
        tvClassPV.setText(getResources().getString(R.string.subProductClass, subProduct.getSubProductClass()));

        amountPerBox.setVisibility(View.GONE);
        tvMinimumBuyAmount.setVisibility(View.VISIBLE);
        tvMinimumBuyAmount.setText(subProduct.getSaleCase() + subProduct.getUnit() + " " + getResources().getString(R.string.is_the_minimum_you_can_buy));

        cross.setOnClickListener(view -> {
            finish();
        });

        buy.setOnClickListener(view -> {

            if (!MainActivity.radiationStatus) {
                if (amount == 0) {
                    addedToCart = false;
                    showToast(ProductView.this, getResources().getString(R.string.not_in_stock), false);
                    return;
                }

                checkForStock();

                if (subProduct.getIsNego() == 1) {
                    if (subProduct.getHasStock() == 0)
                        showNegoDialog();
                    else {
                        amount = 0;
                        addedToCart = false;
                        showToast(this, getResources().getString(R.string.not_in_stock), false);
                    }
                } else {
                    if (subProduct.getHasStock() == 0) {
                        addToCart();
                        UpdateCart();
                        finish();
                    } else {
                        amount = 0;
                        addedToCart = false;
                        showToast(this, getResources().getString(R.string.not_in_stock), false);
                    }
                }
            } else {
                showToast(this, getResources().getString(R.string.radiation_status_is_not_good), false);
            }

        });


        if (merchantId == Integer.parseInt(SharedPreference.getSimpleString(ProductView.this, Constants.userId))) {
            buy.setVisibility(View.GONE);
            productViewBuyConstraint.setVisibility(View.GONE);
        } else {
            buy.setVisibility(View.VISIBLE);
            productViewBuyConstraint.setVisibility(View.VISIBLE);
        }

        getUpdatedStockAndSaleCase();
    }

    private void setUpSpinner() {

        saleCases.clear();

        if (subProduct.getSub_unit().equals("")) {
            for (float i = subProduct.getSaleCase(); i <= subProduct.getStock(); i = i + subProduct.getSaleCase())
                saleCases.add(String.format(Locale.ENGLISH, "%.2f", i));
        } else {
            for (int i = 1; i <= subProduct.getStock(); i++)
                saleCases.add(String.format(Locale.ENGLISH, "%.2f", i));
        }

        customSearchableSpinner.setPositiveButton(getResources().getString(R.string.close_spinner));
        customSearchableSpinner.setTitle(getResources().getString(R.string.select_quantity));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProductView.this, android.R.layout.simple_spinner_item, saleCases);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customSearchableSpinner.setAdapter(adapter);
        customSearchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                amount = AppUtils.convertStringToFloat(saleCases.get(adapterView.getSelectedItemPosition()));


                totalPrice.setText(getResources().getString(R.string.buyingTotalPrice) + " : " +
                        Common.convertInKFormat(String.valueOf(AppUtils.convertStringToDouble(saleCases.get(adapterView.getSelectedItemPosition())) *
                                AppUtils.convertStringToDouble(subProduct.getPrice())))
                        + SharedPreference.getSimpleString(ProductView.this, Constants.currency));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showNegoDialog() {

        dialog = new Dialog(ProductView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_negotiation);


        ConstraintLayout yes = dialog.findViewById(R.id.yesButton);
        yes.setOnClickListener(v -> {
            dialog.dismiss();

            DatabaseHelper databaseHelper = new DatabaseHelper(ProductView.this);
            ArrayList<CartModel> list = new ArrayList<>();

            list = databaseHelper.getAllCartData();

            if (list.size() == 0) {
                sendNego();
            } else {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getProduct_id() == subProduct.getId()) {
                        if (list.get(j).getMarchant_id() == merchantId) {
                            if (list.get(j).getQuantity() + amount <= subProduct.getStock()) {
                                sendNego();
                                addedToCart = false;
                                return;
                            } else {
                                addedToCart = false;
                                showToast(ProductView.this, getResources().getString(R.string.this_seller_does_not_have_enough_stock), false);
                                return;
                            }
                        }
                    }
                }
                sendNego();
            }


        });

        ConstraintLayout no = dialog.findViewById(R.id.noButton);
        no.setOnClickListener(v -> {
            dialog.dismiss();
            if (subProduct.getHasStock() == 0) {
                addToCart();
                UpdateCart();
                finish();
            } else {
                amount = 0;
                addedToCart = false;
                showToast(this, getResources().getString(R.string.not_in_stock), false);
            }
        });

        dialog.show();

    }

    private void sendNego() {

        Bundle bundle = new Bundle();

        ArrayList<NegotiationList> negotiations =
                new ArrayList<>();

        NegotiationList negotiationModel =
                new NegotiationList(
                        subProduct.getProductVariety(), amount,
                        subProduct.getPrice(),
                        String.valueOf(AppUtils.convertStringToDouble(subProduct.getPrice()) * amount),
                        0, "Rejected", subProduct.getUnit());

        negotiations.add(negotiationModel);

        Data negoData = new Data(0, 0, "0.00", subProduct.getCompany_name(),
                AppUtils.convertStringToDouble(subProduct.getMinprice()), AppUtils.convertStringToDouble(subProduct.getMaxprice()),
                "", "", 4000,
                String.valueOf(merchantId), String.valueOf(subProduct.getId()), "Nego-rejected",
                negotiations);

        bundle.putParcelable("NegoData", negoData);

        Intent intent = new Intent("MoveToNego");
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


        finish();

    }

    //    Adding item to cart
    private void addToCart() {

        addedToCart = true;

        DatabaseHelper db = new DatabaseHelper(ProductView.this);

        ArrayList<CartModel> cartList;
        cartList = db.getAllCartData();

        if (cartList.size() == 0) {

            CartModel cart = new CartModel(subProduct.getId(), subProduct.getId(),
                    merchantId, amount, subProduct.getPrice(),
                    productNameStr, "",
                    merchantName, subProduct.getOriginFlag(),
                    subProduct.getStock(), subProduct.getUnit(),
                    String.valueOf(subProduct.getDiscount()), String.valueOf(subProduct.getVat()), String.valueOf(subProduct.getSaleCase()),
                    subProduct.getSub_unit(), "", subProduct.getCompany_name(), subProduct.getProductVariety());
            db.insertCart(cart);

            cartList.clear();
            cartList = db.getAllCartData();
            cartTotal = getTotal(cartList);

        } else {
            for (int j = 0; j < cartList.size(); j++) {
                if (cartList.get(j).getProduct_id() == subProduct.getId()) {
                    if (cartList.get(j).getMarchant_id() == merchantId) {
                        if (cartList.get(j).getQuantity() + amount <= subProduct.getStock()) {
                            CartModel cart;

                            boolean isQuantityPresent = false;

                            for (float i = subProduct.getSaleCase(); i <= subProduct.getStock(); i = i + subProduct.getSaleCase())
                                if (amount == AppUtils.convertStringToFloat(String.format(Locale.ENGLISH, "%.2f", i)))
                                    isQuantityPresent = true;

                            if (isQuantityPresent) {

                                cart = new CartModel(subProduct.getId(), subProduct.getId(),
                                        merchantId, cartList.get(j).getQuantity() + amount,
                                        subProduct.getPrice(), productNameStr,
                                        "", merchantName,
                                        subProduct.getOriginFlag(), subProduct.getStock(), subProduct.getUnit(),
                                        String.valueOf(subProduct.getDiscount()), String.valueOf(subProduct.getVat()),
                                        String.valueOf(subProduct.getSaleCase()), subProduct.getSub_unit()
                                        , "", subProduct.getCompany_name(), subProduct.getProductVariety());
                            } else {
                                cart = new CartModel(subProduct.getId(), subProduct.getId(),
                                        merchantId, amount,
                                        subProduct.getPrice(), productNameStr,
                                        "", merchantName,
                                        subProduct.getOriginFlag(), subProduct.getStock(), subProduct.getUnit(),
                                        String.valueOf(subProduct.getDiscount()), String.valueOf(subProduct.getVat()),
                                        String.valueOf(subProduct.getSaleCase()), subProduct.getSub_unit()
                                        , "", subProduct.getCompany_name(), subProduct.getProductVariety());
                            }


                            db.updateCart(cart);

                            cartList.clear();
                            cartList = db.getAllCartData();
                            cartTotal = getTotal(cartList);

                            return;
                        } else {
                            addedToCart = false;
                            showToast(ProductView.this, getResources().getString(R.string.this_seller_does_not_have_enough_stock), false);
                            return;
                        }
                    }
                }
            }
            CartModel cart = new CartModel(subProduct.getId(), subProduct.getId(),
                    merchantId, amount, subProduct.getPrice(),
                    productNameStr, "",
                    merchantName, subProduct.getOriginFlag(),
                    subProduct.getStock(), subProduct.getUnit(),
                    String.valueOf(subProduct.getDiscount()), String.valueOf(subProduct.getVat()),
                    String.valueOf(subProduct.getSaleCase()), subProduct.getSub_unit(), "", subProduct.getCompany_name(), subProduct.getProductVariety());

            db.insertCart(cart);

            cartList.clear();
            cartList = db.getAllCartData();
            cartTotal = getTotal(cartList);

        }
        db.close();

    }

    private void UpdateCart() {

        Intent intent = new Intent("UpdateCart");

        intent.putExtra("amount", String.valueOf(amount));
        intent.putExtra("unit", subProduct.getUnit());
        intent.putExtra("showToast", addedToCart);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
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


    private void checkForStock() {

        DatabaseHelper db = new DatabaseHelper(ProductView.this);
        ArrayList<CartModel> cartArrayList = new ArrayList<>();
        cartArrayList = db.getAllCartData();

        if (cartArrayList.size() > 0) {
            checkFromCartAsWell(subProduct, cartArrayList, amount);

        } else {

            if (subProduct.getStock() < amount) {
                subProduct.setHasStock(1); //To blur the layout
            } else {
                subProduct.setHasStock(0);
            }
        }

        db.close();
    }


    private void checkFromCartAsWell(SubProduct subProduct, ArrayList<CartModel> cartArrayList, double amount) {

        for (int k = 0; k < cartArrayList.size(); k++) {
            if (cartArrayList.get(k).getProduct_id() == subProduct.getId()) {
                if (cartArrayList.get(k).getMarchant_id() == merchantId) {
                    if (subProduct.getStock() < cartArrayList.get(k).getQuantity() + amount) {
                        subProduct.setHasStock(1);  //To blur the layout
                    } else {
                        subProduct.setHasStock(0);
                    }
                }
            }
        }
    }

    private void getUpdatedStockAndSaleCase() {

        Common.showDialog(this);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.getUpdateStockAndSaleCase("Bearer " +
                SharedPreference.getSimpleString(ProductView.this, Constants.accessToken), subProduct.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    JSONObject jsonObject;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());

                        JSONObject data = jsonObject.getJSONObject("data");
                        subProduct.setSaleCase((float) data.getDouble("sale_case"));
                        subProduct.setStock((float) data.getDouble("stock"));

                        tvMinimumBuyAmount.setText(subProduct.getSaleCase() + subProduct.getUnit() + " " + getResources().getString(R.string.is_the_minimum_you_can_buy));

                        setUpSpinner();
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        Log.d("Product View", "onResponse: " + jsonObject.getString("message"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("ProductView", "onFailure: " + t.getMessage());
            }
        });

    }


}
