package com.vic.vicwsp.Views.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.FromTickerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.Controllers.Interfaces.UpdateProductDetailPrice;
import com.vic.vicwsp.Controllers.Interfaces.UpdateTheProductPrice;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.ProductDetailModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

//import com.elementary.minbio.Models.Response.ProductDetail.ProductDetailModel;

public class FromTicker extends Fragment implements SocketCallback , UpdateProductDetailPrice {

    private View v;
    private RecyclerView mainRecycler;
    private LinearLayoutManager layoutManager;
    private FromTickerAdapter fromTickerAdapter;
    private TextView tvMarchandDetail;
    private int position;
    private ConstraintLayout btnStock, btnBuy;
    private int productId = 0;
    private ImageView ivToolbarCart;
    private TextView tvToolbarCart;
    private UpdateTheProductPrice updateTheProductPrice;
    private boolean fromfav = false;
    private ConstraintLayout cartConstraint;
    private ProductDetailModel productDetailModel;
    private UpdateProductDetailPrice updateProductDetailPrice;


    // Recierve to update the user when he adds his product to sale
    private void registerBroadCastReceiver() {

        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String productId = intent.getStringExtra("productId");
                String stock = intent.getStringExtra("stock");
                String isBio = intent.getStringExtra("isBio");
                String isNego = intent.getStringExtra("isNego");
                String price = intent.getStringExtra("price");
                String saleCase = intent.getStringExtra("saleCase");


                if (stock != null && isBio != null && isNego != null && price != null && saleCase != null) {
                    Intent updateSellers = new Intent("UpdateProductsMerchantList");
                    updateSellers.putExtra("merchantsNumber", String.valueOf(productDetailModel.getData().getSellers().size() + 1));
                    updateSellers.putExtra("productId", String.valueOf(productDetailModel.getData().getId()));
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(updateSellers);
                    callApi(context, Integer.parseInt(productId));
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter("UpdateSellers"));

    }

    // Receive to update the user when he adds his product to sale
    private void registerCallProductDetail() {

        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String productId = intent.getStringExtra("productId");

                if (!productId.equals(null) && intent.getAction().equals("CallProductDetail")) {
                    callApi(context, Integer.parseInt(productId));

                    Intent updateSellers = new Intent("UpdateProductsMerchantList");
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(updateSellers);
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter("CallProductDetail"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_from_ticker, container, false);
        registerBroadCastReceiver();
        registerCallProductDetail();
        initViews();

        if (productDetailModel != null) {
            if (productDetailModel.getData().getSellers().size() <= 1) {
                tvMarchandDetail.setText(productDetailModel.getData().getSellers().size() + " " + getContext().getResources().getString(R.string.marchant));
            } else {
                tvMarchandDetail.setText(productDetailModel.getData().getSellers().size() + " " + getContext().getResources().getString(R.string.marchants));
            }
            initRecyclerView();
        }
        if (productDetailModel == null) {
            Common.showDialog(getContext());
            callApi(getContext(), productId);
        }
        sockets.initializeCallback(this);
        updateProductDetailPrice = this;

        return v;
    }

    private void initViews() {

        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.GONE);

        mainRecycler = v.findViewById(R.id.mainRecyclerCharts);
        ImageView back = getActivity().findViewById(R.id.ivToolbarBack);
        back.setVisibility(View.VISIBLE);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        tvMarchandDetail = v.findViewById(R.id.tvMarchandDetail);
        btnBuy = v.findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(view -> {

            if (productDetailModel != null)
                if (productDetailModel.getData().getSellers().size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("productId", productId);
                    bundle.putParcelable("updateProductPrice", updateTheProductPrice);
                    bundle.putParcelable("updateProductDetailPrice", updateProductDetailPrice);
                    PurchaseForm purchaseForm = new PurchaseForm();
                    purchaseForm.setArguments(bundle);
                    getFragmentManager().
                            beginTransaction().
                            replace(R.id.mainFragment, purchaseForm, "Purchase Form")
                            .addToBackStack("Purchase Form ")
                            .commit();
                } else
                    showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.product_has_no_seller), false);
            else
                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.something_is_not_right), false);

        });
        btnStock = v.findViewById(R.id.btnStock);
        btnStock.setOnClickListener(view -> {

            if (productDetailModel != null)
                if (SharedPreference.getSimpleString(getContext(), Constants.isMerchant).equals("1")) {
                    SalesProducts salesProducts = new SalesProducts();
                    Bundle bundle = new Bundle();
                    bundle.putInt("productId", productDetailModel.getData().getId());
                    bundle.putString("productName", productDetailModel.getData().getName());
                    bundle.putString("unit", productDetailModel.getData().getUnit());
                    salesProducts.setArguments(bundle);

                    getFragmentManager().
                            beginTransaction()
                            .replace(R.id.mainFragment, salesProducts, "Sales Products")
                            .addToBackStack("Sales Products")
                            .commit();
                } else
                    showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.you_are_not_marchant), false);
            else
                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.something_is_not_right), false);
        });
        productId = getArguments().getInt("productId");


        if (!MainActivity.radiationStatus) {
            btnBuy.setBackground(getContext().getResources().getDrawable(R.drawable.round_button));
            btnStock.setBackground(getContext().getResources().getDrawable(R.drawable.round_button_green));
            btnBuy.setEnabled(true);
            btnStock.setEnabled(true);
        } else {
            disableBuyStockButtons(getContext());
        }

    }

    private void disableBuyStockButtons(Context context) {
        btnBuy.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
        btnStock.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
        btnBuy.setEnabled(false);
        btnStock.setEnabled(false);
    }

    //Initializing the recycler view
    private void initRecyclerView() {
        fromTickerAdapter = new FromTickerAdapter(getContext(), productDetailModel.getData());
        layoutManager = new LinearLayoutManager(getContext());
        mainRecycler.setLayoutManager(layoutManager);
        mainRecycler.setAdapter(fromTickerAdapter);

    }

    //Calling the api to get the data to display on the products detail screen
    private void callApi(Context context, int productId) {
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ProductDetailModel> getProductDetail = api.getProductDetail("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), productId);

        getProductDetail.enqueue(new Callback<ProductDetailModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ProductDetailModel> call, Response<ProductDetailModel> response) {
                productDetailModel = null;
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        Log.d(Constraints.TAG, "onResponse: " + response);

                        productDetailModel = new ProductDetailModel(response.body().getData(), response.body().getStatus(),
                                response.body().getMessage());

                        if (productDetailModel.getData().getSellers().size() <= 1) {
                            tvMarchandDetail.setText(productDetailModel.getData().getSellers().size() + " " +
                                    getContext().getResources().getString(R.string.marchant));
                        } else {
                            tvMarchandDetail.setText(productDetailModel.getData().getSellers().size() + " " +
                                    context.getResources().getString(R.string.marchants));
                        }
                        initRecyclerView();

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ProductDetailModel> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(Constraints.TAG, "onFailure: " + t.getMessage());

            }
        });

    }

    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {
        if (tag.equals(update_product)) {
            updateTheProductPrice(jsonObject);
        }
    }

    @Override
    public void onFailure(String message, String tag) {
        Log.d("Tag", "onFailure: " + tag);
    }

    private void updateTheProductPrice(JSONObject jsonObject) {
        try {
            String merchantId = jsonObject.getString("merchant_id");
            String productId = jsonObject.getString("product_id");
            String subProductId = jsonObject.getString("subproduct_id");
            String price = jsonObject.getString("price");
            String minPrice = jsonObject.getString("minprice");
            String subprominprice = jsonObject.getString("subprominprice");

            getActivity().runOnUiThread(() -> {

                try {
                    productDetailModel.getData().getDetails().setPrice(minPrice);

                    for (int i = 0; i < productDetailModel.getData().getSellers().size(); i++) {
                        if (productDetailModel.getData().getSellers().get(i).getId() == Integer.parseInt(merchantId)) {
                            productDetailModel.getData().getSellers().get(i).setPrice(subprominprice);
                        }

                        for (int j = 0; j < productDetailModel.getData().getSellers().get(i).getSubProducts().size(); j++) {
                            if (productDetailModel.getData().getSellers().get(i).getId() == Integer.parseInt(merchantId)
                                    && productDetailModel.getData().getSellers().get(i).getSubProducts().get(j).getId() == Integer.parseInt(subProductId)) {
                                productDetailModel.getData().getSellers().get(i).getSubProducts().get(j).setPrice(price);
                            }
                        }

                    }

                    fromTickerAdapter.notifyItemChanged(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            if (updateTheProductPrice != null)
                updateTheProductPrice.updatePrice(Integer.parseInt(productId), minPrice);

            ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                    (price), (minPrice));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callBroad(String p) {
        Intent f = new Intent("UpdateFavoriteProducts");
        f.putExtra("position", (p));
        f.putExtra("fav", "0");
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(f);
    }

    @Override
    public void updateProductDetailPrice(int productId, String price) {
        if (productDetailModel != null) {
            if (productDetailModel.getData().getId() == productId) {
                productDetailModel.getData().getDetails().setPrice(price);
                fromTickerAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
