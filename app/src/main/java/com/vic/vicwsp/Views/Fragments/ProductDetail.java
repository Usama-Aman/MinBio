package com.vic.vicwsp.Views.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import com.vic.vicwsp.Controllers.Helpers.UpdatedProductDetailRecyclerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.Controllers.Interfaces.UpdateProductDetail;
import com.vic.vicwsp.Controllers.Interfaces.UpdateProductDetailPrice;
import com.vic.vicwsp.Controllers.Interfaces.UpdateTheProductPrice;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.ProductDetailModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.kodmap.library.kmrecyclerviewstickyheader.KmHeaderItemDecoration;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

//import com.elementary.minbio.Models.Response.ProductDetail.ProductDetailModel;

@SuppressLint("ParcelCreator")
public class ProductDetail extends Fragment implements SocketCallback, UpdateProductDetail, Parcelable, UpdateProductDetailPrice {

    private View v;
    private LinearLayoutManager layoutManager;
    private ProductDetailModel productDetailModel;
    private UpdatedProductDetailRecyclerAdapter updatedProductDetailRecyclerAdapter;
    private RecyclerView recyclerView;
    private KmHeaderItemDecoration kmHeaderItemDecoration;
    private TextView tvMarchandDetail;
    private int position;
    private ConstraintLayout btnStock, btnBuy;
    private int productId;
    private ImageView ivToolbarCart;
    private TextView tvToolbarCart;
    private UpdateTheProductPrice updateTheProductPrice;
    private boolean fromfav = false;
    private ConstraintLayout cartConstraint;
    private UpdateProductDetailPrice updateProductDetailPrice;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals("CallProductDetail")) {
                String productId = intent.getStringExtra("productId");
                if (productId != null) {
                    Log.d("Product Detail Called", "onReceive: ");
                    callApi(context, Integer.parseInt(productId), true);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_updated_product_detail, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initViews();

        if (productDetailModel != null) {
            if (productDetailModel.getData().getSellers().size() <= 1) {
                tvMarchandDetail.setText(productDetailModel.getData().getSellers().size() + " " +
                        getContext().getResources().getString(R.string.marchant));
            } else {
                tvMarchandDetail.setText(productDetailModel.getData().getSellers().size() + " " +
                        getContext().getResources().getString(R.string.marchants));
            }
            initRecyclerView();
        }

        if (productDetailModel == null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter("CallProductDetail"));
            Common.showDialog(getContext());
            callApi(getContext(), productId, false);
        }

        sockets.initializeCallback(this);
        updateProductDetailPrice = this;
        return v;
    }

    //Initializing the views
    private void initViews() {

        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.VISIBLE);

        ConstraintLayout supportConstraint = getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.GONE);

        fromfav = getArguments().getBoolean("fromFav");
        updateTheProductPrice = getArguments().getParcelable("updateProductPrice");
        productId = getArguments().getInt("productId");
        Log.d("ProductId", "initViews: " + String.valueOf(productId));
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        tvMarchandDetail = v.findViewById(R.id.tvMarchandDetail);
        position = getArguments().getInt("position");
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
                            beginTransaction()
                            .replace(R.id.mainFragment, purchaseForm, "Purchase Form")
                            .addToBackStack("Purchase Form")
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

    }

    //Calling the api to get the data to display on the products detail screen
    private void callApi(Context context, int productId, boolean fromToSell) {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ProductDetailModel> getProductDetail = null;

        if (SharedPreference.getSimpleString(context, Constants.isBioOn).equals("true"))
            getProductDetail = api.getBioProductDetail("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken), productId);
        else
            getProductDetail = api.getProductDetail("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken), productId);

        getProductDetail.enqueue(new Callback<ProductDetailModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ProductDetailModel> call, Response<ProductDetailModel> response) {
                productDetailModel = null;
                try {
                    if (response.isSuccessful()) {
                        productDetailModel = new ProductDetailModel(response.body().getData(), response.body().getStatus(),
                                response.body().getMessage());

                        if (productDetailModel.getData().getSellers().size() <= 1) {
                            tvMarchandDetail.setText(productDetailModel.getData().getSellers().size() + " " +
                                    context.getResources().getString(R.string.marchant));
                        } else {
                            tvMarchandDetail.setText(productDetailModel.getData().getSellers().size() + " " +
                                    context.getResources().getString(R.string.marchants));
                        }

                        initRecyclerView();
                        callRadiationApi(context);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) context), jsonObject.getString("message"), false);
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

    private void callRadiationApi(Context context) {

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> call = api.getRadiationStatus("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    JSONObject jsonObject;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());

                        JSONObject data = jsonObject.getJSONObject("data");
                        boolean radiation_status = data.getBoolean("radiation_status");

                        if (!radiation_status) {
                            btnBuy.setBackground(context.getResources().getDrawable(R.drawable.round_button));
                            btnStock.setBackground(context.getResources().getDrawable(R.drawable.round_button_green));
                            btnBuy.setEnabled(true);
                            btnStock.setEnabled(true);
                        } else {
                            disableBuyStockButtons(context);
                        }
                    } else {
                        jsonObject = new JSONObject(response.errorBody().toString());
                        showToast(getActivity(), jsonObject.getString("message"), false);
                        disableBuyStockButtons(context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    disableBuyStockButtons(context);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                disableBuyStockButtons(context);
                Log.d(Constraints.TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private void disableBuyStockButtons(Context context) {
        btnBuy.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
        btnStock.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
        btnBuy.setEnabled(false);
        btnStock.setEnabled(false);
    }


    //Initializing the recycler view
    private void initRecyclerView() {
        updatedProductDetailRecyclerAdapter = new UpdatedProductDetailRecyclerAdapter(getContext(), productDetailModel.getData(), fromfav);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = v.findViewById(R.id.productDetailMainRecycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(updatedProductDetailRecyclerAdapter);
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

                    if (productDetailModel != null) {
                        if (productDetailModel.getData().getId() == Integer.parseInt(productId)) {
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
                            updatedProductDetailRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
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

    @Override
    public void updateProductDetailFragment(int subProductId) {

        callApi(getContext(), productId, true);

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(productDetailModel, i);
        parcel.writeInt(position);
        parcel.writeInt(productId);
        parcel.writeParcelable(updateTheProductPrice, i);
        parcel.writeByte((byte) (fromfav ? 1 : 0));
    }

    @Override
    public void updateProductDetailPrice(int productId, String price) {

        if (productDetailModel != null) {
            if (productDetailModel.getData().getId() == productId) {
                productDetailModel.getData().getDetails().setPrice(price);
                updatedProductDetailRecyclerAdapter.notifyDataSetChanged();
            }
        }

    }
}
