package com.vic.vicwsp.Views.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.UpdatePurchaseFormRecyclerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.ScrollUpListView;
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

public class PurchaseForm extends Fragment implements SocketCallback, ScrollUpListView {

    private View v;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ProductDetailModel productDetailModel;
    private ImageView ivToolbarCart;
    private TextView tvToolbarCart;
    private UpdateTheProductPrice updateTheProductPrice;
    private UpdateProductDetailPrice updateProductDetailPrice;
    private ConstraintLayout cartConstraint;
    private UpdatePurchaseFormRecyclerAdapter updatePurchaseFormRecyclerAdapter;
    private SwipeRefreshLayout updatedPurchaseFormPullToRefresh;
    private int productId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_updated_purchase_form, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initViews();


        sockets.initializeCallback(this);
        return v;
    }

    //Initializing the views
    private void initViews() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.VISIBLE);

        updatedPurchaseFormPullToRefresh = v.findViewById(R.id.updatedPurchaseFormPullToRefresh);
        updatedPurchaseFormPullToRefresh.setOnRefreshListener(() -> {
            callApi(getContext(), productId);
        });

        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);

      updateTheProductPrice = getArguments().getParcelable("updateProductPrice");
//        updateProductDetailPrice = getArguments().getParcelable("updateProductDetailPrice");
//        productDetailModel = getArguments().getParcelable("productDetail");
        productId = getArguments().getInt("productId");

        Common.showDialog(getContext());
        callApi(getContext(), productId);
    }

    //Calling the api to get the data to display on the products detail screen
    private void callApi(Context context, int productId) {
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
                Common.dissmissDialog();
                updatedPurchaseFormPullToRefresh.setRefreshing(false);
                try {
                    if (response.isSuccessful()) {
                        productDetailModel = new ProductDetailModel(response.body().getData(), response.body().getStatus(),
                                response.body().getMessage());


                        for (int i = 0; i < productDetailModel.getData().getSellers().size(); i++)
                            for (int j = 0; j < productDetailModel.getData().getSellers().get(i).getSubProducts().size(); j++)
                                productDetailModel.getData().getSellers().get(i).getSubProducts().get(j).setHasStock(0);

                        initRecyclerView();

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

    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }

    //Initializing the recycler view
    private void initRecyclerView() {
        updatePurchaseFormRecyclerAdapter = new UpdatePurchaseFormRecyclerAdapter(getContext(), productDetailModel.getData(), this);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = v.findViewById(R.id.PurchaseMainRecycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(updatePurchaseFormRecyclerAdapter);
        recyclerView.setHasFixedSize(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {
        if (tag.equals(update_product)) {
            updateTheProductPrice(jsonObject);
        }
    }

    @Override
    public void onFailure(String message, String tag) {

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
                            updatePurchaseFormRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            });

//            updateProductDetailPrice.updateProductDetailPrice(Integer.parseInt(productId), minPrice);
            if (updateTheProductPrice != null)
                updateTheProductPrice.updatePrice(Integer.parseInt(productId), minPrice);

            ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                    (price), (minPrice));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void scroll() {
        if (recyclerView != null)
            recyclerView.smoothScrollBy(0, 200);
    }
}
