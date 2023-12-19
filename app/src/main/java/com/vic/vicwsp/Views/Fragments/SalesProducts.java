package com.vic.vicwsp.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.SalesProductsAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.ShowNullMessage;
import com.vic.vicwsp.Models.Response.AddProduct.AddProductModel;
import com.vic.vicwsp.Models.Response.AddProduct.Datum;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SalesProducts extends Fragment implements ShowNullMessage {

    private View v;
    private RecyclerView recyclerView;
    private SalesProductsAdapter salesProductsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ImageView addProduct;
    private int productId = 0;
    private ArrayList<Datum> productList = new ArrayList<>();
    private TextView tvListNull, heading;
    private String unit;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String productName = "";
    private ConstraintLayout cartConstraintLayout;
    private ImageView addButtonSale1;
    private ConstraintLayout addButtonSale2;
    private ImageView back;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sales_form_products, container, false);
        initViews();
        Common.showDialog(getContext());
        getProductList(getContext());

        setAdapter();
        return v;
    }


    private void initViews() {

        try {

            ConstraintLayout supportConstraint= getActivity().findViewById(R.id.toolbarSupport);
            supportConstraint.setVisibility(View.GONE);

            productId = getArguments().getInt("productId");
            productName = getArguments().getString("productName");

            tvListNull = v.findViewById(R.id.tvListNull);
            heading = v.findViewById(R.id.tvSalesProductsHeading);
            heading.setText(getContext().getResources().getString(R.string.sales_form, productName));

            swipeRefreshLayout = v.findViewById(R.id.salesProductSwipe);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                productList.clear();
                getProductList(getContext());
            });

            cartConstraintLayout = getActivity().findViewById(R.id.cartConstraintLayout);
            cartConstraintLayout.setVisibility(View.VISIBLE);

            back = getActivity().findViewById(R.id.ivToolbarBack);
            back.setVisibility(View.VISIBLE);


            addButtonSale1 = v.findViewById(R.id.AddButtonSale1);
            addButtonSale1.setOnClickListener(view -> {
                try {
                    callAddProduct();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            addButtonSale2 = v.findViewById(R.id.AddButtonSale2);
            addButtonSale2.setOnClickListener(view -> {
                try {
                    callAddProduct();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            recyclerView = v.findViewById(R.id.salesProductRecycler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callAddProduct() throws Exception {
        if (productId > 0) {
            SalesForm salesForm = new SalesForm();
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromEdit", false);
            bundle.putInt("productId", productId);
            salesForm.setArguments(bundle);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragment, salesForm, "Sales Form")
                    .addToBackStack("Sales Form")
                    .commit();

        } else
            Common.showToast((AppCompatActivity) getContext(), getContext().getResources().getString(R.string.something_is_not_right), false);

    }

    private void getProductList(Context context) {
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<AddProductModel> call = api.getProductList("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), productId);

        call.enqueue(new Callback<AddProductModel>() {
            @Override
            public void onResponse(Call<AddProductModel> call, Response<AddProductModel> response) {
                Common.dissmissDialog();
                swipeRefreshLayout.setRefreshing(false);
                productList.clear();
                try {
                    if (response.isSuccessful()) {
                        productList.addAll(response.body().getData());

                        if (productList.size() == 0) {
                            tvListNull.setVisibility(View.VISIBLE);
                            addButtonSale2.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvListNull.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            addButtonSale2.setVisibility(View.GONE);
                            salesProductsAdapter.notifyDataSetChanged();
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast((AppCompatActivity) context, jsonObject.getString("message"), false);
                        tvListNull.setVisibility(View.VISIBLE);
                        addButtonSale2.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AddProductModel> call, Throwable t) {
                Common.dissmissDialog();
                tvListNull.setVisibility(View.VISIBLE);
                addButtonSale2.setVisibility(View.VISIBLE);
                Log.d(TAG, "onFailure: ." + t.getMessage());
            }
        });

    }

    private void setAdapter() {
        salesProductsAdapter = new SalesProductsAdapter(getContext(), productList, productId, this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(salesProductsAdapter);
    }


    @Override
    public void showNullMessage() {

        tvListNull.setVisibility(View.VISIBLE);
        addButtonSale2.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

    }
}
