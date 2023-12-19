package com.vic.vicwsp.Views.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.ProductsRecyclerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.Controllers.Interfaces.UpdateTheProductPrice;
import com.vic.vicwsp.Models.Response.ProductsPagination.Datum;
import com.vic.vicwsp.Models.Response.ProductsPagination.ProductsPagination;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.ApplicationClass;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

@SuppressLint("ParcelCreator")
public class Products extends Fragment implements SocketCallback, UpdateTheProductPrice {

    private View v;
    private ImageView logout, cart, back, ivListNull;
    private TextView cartText, listNull;
    private RecyclerView productsRecyclerView;
    private ProductsRecyclerAdapter productsRecyclerAdapter;
    private ProductsPagination productsPagination;
    private ArrayList<Datum> productsData = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private String catId;
    private boolean fromSearch = false;
    private boolean isLoading = false;
    private int CURRENT_PAGE = 0;
    private int LAST_PAGE = -1;
    private String min, max, searchProduct;
    private int bio, origin;
    private ConstraintLayout cartConstraint;
    private String suggestion = "";
    private Call<ProductsPagination> call = null;
    private EditText etProductSuggestion;
    private ImageView btnSearchProducts;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("UpdateMerchants")) {
                try {
                    callPaginationApi(0, true, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.updated_products_with_suggestion, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        fromSearch = getArguments().getBoolean("fromSearch");

        initViews();
        setAdapter();
//        initScrollListener();


        if (productsData.size() == 0) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("UpdateMerchants"));
            if (fromSearch) {
                getDataFromBundle();
            } else {
                catId = getArguments().getString("categoryId");
                Common.showDialog(getContext());
                callPaginationApi(0, false, getContext());
            }
        }
        sockets.initializeCallback(this);
        if (((MainActivity) getContext()).tickerModelArrayList.size() == 0) {
            Log.d("isConnected", String.valueOf(sockets.getSocket().connected()));
            sockets.emit(ApplicationClass.ticker);
        }


        return v;
    }

    //Initializing the bundle
    private void getDataFromBundle() {

        min = getArguments().getString("minPrice");
        max = getArguments().getString("maxPrice");
        searchProduct = getArguments().getString("searchProduct");
        bio = getArguments().getInt("bio");
        origin = getArguments().getInt("origin");
        productsPagination = getArguments().getParcelable("SearchResult");
        productsData = productsPagination.getData();
        CURRENT_PAGE = productsPagination.getMeta().getCurrentPage();
        LAST_PAGE = productsPagination.getMeta().getLastPage();

        setAdapter();
    }

    private void initViews() {

        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.VISIBLE);

        ConstraintLayout supportConstraint = getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.GONE);

        productsRecyclerView = v.findViewById(R.id.productsRecycler);
        etProductSuggestion = v.findViewById(R.id.etProductSuggestion);
        listNull = v.findViewById(R.id.tvListNull);
        ivListNull = v.findViewById(R.id.ivListNull);
        btnSearchProducts = v.findViewById(R.id.btnProductSearch);
        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        swipeRefreshLayout = v.findViewById(R.id.swipeToRefreshProducts);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            etProductSuggestion.setText("");
            callPaginationApi(0, true, getContext());
        });

        etProductSuggestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (s.toString().equals("") || s.toString().isEmpty()) {
//                    suggestion = s.toString();
//                    btnSearchProducts.setEnabled(false);
//                    Common.showDialog(getContext());
//                    callPaginationApi(0, true, getContext());
//
//                } else {
//                    suggestion = s.toString();
//                    btnSearchProducts.setEnabled(true);
//                }

                productsRecyclerAdapter.getFilter().filter(s.toString());
            }
        });

        btnSearchProducts.setOnClickListener(v -> {
            Common.showDialog(getContext());
            callPaginationApi(0, true, getContext());
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void initScrollListener() {
        productsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productsData.size() - 1) {
                        recyclerView.post(() -> loadMore());
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        productsData.add(null);
        productsRecyclerAdapter.notifyItemInserted(productsData.size() - 1);

        if (CURRENT_PAGE != LAST_PAGE)

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    callPaginationApi(CURRENT_PAGE, false, getContext());
                }
            }, 2000);

        else {
            productsData.remove(productsData.size() - 1);
            productsRecyclerAdapter.notifyItemRemoved(productsData.size());
        }
    }

    private void callPaginationApi(int c, boolean isRefresh, Context context) {

        try {
            Api api = RetrofitClient.getClient().create(Api.class);
            if (SharedPreference.getSimpleString(context, Constants.isBioOn).equals("true")) {
                if (isRefresh)
                    call = api.getBioProducts("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken));
                else
                    call = api.getBioProducts("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken));
            } else {
                if (isRefresh)
                    call = api.getPagination("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken), Integer.parseInt(catId));
                else
                    call = api.getPagination("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken), Integer.parseInt(catId));
            }

            call.enqueue(new Callback<ProductsPagination>() {
                @Override
                public void onResponse(Call<ProductsPagination> call, Response<ProductsPagination> response) {
                    if (isRefresh) {
                        swipeRefreshLayout.setRefreshing(false);
                        productsData.clear();
                    }
                    try {
                        Common.dissmissDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (response.isSuccessful()) {

//                            LAST_PAGE = response.body().getMeta().getLastPage();
//                            CURRENT_PAGE = response.body().getMeta().getCurrentPage();


                            if (productsData.size() > 0) {
                                productsData.remove(productsData.size() - 1);
                                productsRecyclerAdapter.notifyItemRemoved(productsData.size());
                            }

                            productsPagination = new ProductsPagination(response.body().getData(),
                                    response.body().getLinks(), response.body().getMeta(), response.body().getStatus(),
                                    response.body().getMessage());

                            for (int i = 0; i < response.body().getData().size(); i++) {
                                Datum datum = new Datum(response.body().getData().get(i).getId(), response.body().getData().get(i).getName(),
                                        response.body().getData().get(i).getImagePath(), response.body().getData().get(i).getProductDescription(),
                                        response.body().getData().get(i).getCategoryId(), response.body().getData().get(i).getIs_warranted(),
                                        response.body().getData().get(i).getDetails(), response.body().getData().get(i).getSubproduct_id()
                                        , response.body().getData().get(i).getSellers());
                                productsData.add(datum);
                            }

                            if (productsData.size() == 0) {
                                listNull.setVisibility(View.VISIBLE);
                                ivListNull.setVisibility(View.VISIBLE);
                                productsRecyclerView.setVisibility(View.GONE);
                            } else {
                                listNull.setVisibility(View.GONE);
                                ivListNull.setVisibility(View.GONE);
                                productsRecyclerView.setVisibility(View.VISIBLE);
                            }

                            productsRecyclerAdapter.notifyDataSetChanged();
                            isLoading = false;

                        } else {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            showToast(((AppCompatActivity) context), jsonObject.getString("message"), false);
                        }
                    } catch (Exception e) {
                        showToast(((AppCompatActivity) context), context.getResources().getString(R.string.something_is_not_right), false);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ProductsPagination> call, Throwable t) {
                    Common.dissmissDialog();
                    listNull.setVisibility(View.VISIBLE);
                    productsRecyclerView.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAdapter() {
        productsRecyclerAdapter = new ProductsRecyclerAdapter(getContext(), productsData, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        productsRecyclerView.setLayoutManager(mLayoutManager);
        productsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        productsRecyclerView.setAdapter(productsRecyclerAdapter);
    }

    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {

        if (tag.equals(update_product)) {
            updateTheProductPrice(jsonObject);
        } else if (tag.equals(ApplicationClass.ticker)) {
            getActivity().runOnUiThread(() -> {
                try {
                    Log.d(ContentValues.TAG, "onSuccess: " + "Ticker is Caling again n again");
                    ((MainActivity) getContext()).initializeTicker(jsonObject);
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onFailure(String message, String tag) {
        Log.e("Tag", "onFailure: " + message + tag);
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
                for (int i = 0; i < productsData.size(); i++) {
                    if (productsData.get(i).getId() == Integer.parseInt(productId)) {
                        productsData.get(i).getDetails().setPrice(minPrice);
                        productsRecyclerAdapter.notifyItemChanged(i);
                    }
                }
            });


            ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                    (price), (minPrice));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePrice(int productId, String price) {
        Log.d("Tag", "updatePrice: ");

        try {
            for (int i = 0; i < productsData.size(); i++) {
                if (productsData.get(i).getId() == productId) {
                    productsData.get(i).getDetails().setPrice(price);
                    productsRecyclerAdapter.notifyItemChanged(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
