package com.vic.vicwsp.Views.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.CatalogueRecyclerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.Models.Categories;
import com.vic.vicwsp.Models.SubCategory;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.ApplicationClass;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.SignIn;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

public class Catalogue extends Fragment implements SocketCallback {

    private View v;
    private ImageView logout, cart, back, ivListNull;
    private TextView cartText, listNull;
    private ArrayList<Categories> categories = new ArrayList<>();
    private ArrayList<SubCategory> subCategories = new ArrayList<>();
    private RecyclerView catalogueRecyclerView;
    private CatalogueRecyclerAdapter catalogueRecyclerAdapter;
    private SwipeRefreshLayout catalogueSwipeToRefresh;
    private ConstraintLayout cartConstraint, supportConstraint;
    private long mLastClickTimeTicker = 0;
    private long mLastClickTimeCatalogue = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_catalogue, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        try {
            initViews();
            setAdapter();
            sockets.initializeCallback(this);

            if (categories.size() == 0) {

                Common.showDialog(getContext());
                callCategoriesApi();

                if (((MainActivity) getContext()).tickerModelArrayList.size() == 0) {
                    Log.d("isConnected", String.valueOf(sockets.getSocket().connected()));

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sockets.emit(ApplicationClass.ticker);
                        }
                    }, 1000);
                }
            } else {
                catalogueRecyclerView.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }


    //Initializing the views
    private void initViews() {
        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        catalogueRecyclerView = v.findViewById(R.id.catalogueRecycler);
        catalogueSwipeToRefresh = v.findViewById(R.id.catalogueSwipeToRefresh);

        ConstraintLayout changeLanguageLayout = getActivity().findViewById(R.id.changeLanguageLayout);
        changeLanguageLayout.setVisibility(View.GONE);

        supportConstraint = getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.VISIBLE);
        supportConstraint.setOnClickListener(v -> {
            ((MainActivity) getContext()).showSupportDialog();
        });

        TextView notificationBadge = getActivity().findViewById(R.id.notificationBadge);
        notificationBadge.setVisibility(View.VISIBLE);
        ImageView ivNotifications = getActivity().findViewById(R.id.ivNotifications);
        ivNotifications.setVisibility(View.VISIBLE);

        //Updating the notification badge
        int c = ((MainActivity) getContext()).notificationCount;
        ((MainActivity) getContext()).notificationCount = 0;
        ((MainActivity) getContext()).setNotificationBadge(c, 0);

        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.VISIBLE);
        listNull = v.findViewById(R.id.tvListNull);
        ivListNull = v.findViewById(R.id.ivListNull);
        logout.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        catalogueSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (((MainActivity) getContext()).tickerModelArrayList.size() == 0) {
                    Log.d("isConnected", String.valueOf(sockets.getSocket().connected()));
                    sockets.emit(ApplicationClass.ticker);
                }

                callCategoriesApi();
            }
        });
    }

    //Calling get categories api
    private void callCategoriesApi() {

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> categoriesCall = api.getCategories("Bearer " +
                SharedPreference.getSimpleString(getContext(), Constants.accessToken));

        categoriesCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                catalogueSwipeToRefresh.setRefreshing(false);
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        categories.clear();
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray data = jsonObject.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject o = data.getJSONObject(i);

                            Categories category = new Categories(o.getInt("id"), o.getString("name"),
                                    o.getString("image_path"));

                            categories.add(category);

                        }

                        if (categories.size() == 0) {
                            listNull.setVisibility(View.VISIBLE);
                            ivListNull.setVisibility(View.VISIBLE);
                            catalogueRecyclerView.setVisibility(View.GONE);
                        } else {
                            listNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                            catalogueRecyclerView.setVisibility(View.VISIBLE);
                        }

                        setAdapter();

                    } else if (response.code() == 401) {
                        SharedPreference.saveSimpleString(getContext(), Constants.isLoggedIn, "false");
                        Intent i = new Intent(getContext(), SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast((AppCompatActivity) getContext(), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                catalogueSwipeToRefresh.setRefreshing(false);
                Log.d(TAG, "onFailure: " + t.getMessage());
                listNull.setVisibility(View.VISIBLE);
                catalogueRecyclerView.setVisibility(View.GONE);
            }
        });


    }

    //Initializing adapter for recycler view
    private void setAdapter() {
        catalogueRecyclerAdapter = new CatalogueRecyclerAdapter(getContext(), categories);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        catalogueRecyclerView.setLayoutManager(mLayoutManager);
        catalogueRecyclerView.setItemAnimator(new DefaultItemAnimator());
        catalogueRecyclerView.setAdapter(catalogueRecyclerAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }

    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {
        try {
            if (tag.equals(update_product)) {
                updateTheProductPrice(jsonObject);
            } else if (tag.equals(ApplicationClass.ticker)) {
                getActivity().runOnUiThread(() -> {
                    try {
                        Log.d(TAG, "onSuccess: " + "Ticker is Caling again n again");
                        ((MainActivity) getContext()).initializeTicker(jsonObject);
                        System.gc();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String message, String tag) {
        Log.d(TAG, "onFailure: " + tag);
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
                ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                        (price), (minPrice));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
