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
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.FavoriteRecyclerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.ShowNullMessage;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.Controllers.Interfaces.UpdateTheProductPrice;
import com.vic.vicwsp.Models.Response.FavoriteUpdate.Datum;
import com.vic.vicwsp.Models.Response.FavoriteUpdate.FavoriteModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
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

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

@SuppressLint("ParcelCreator")
public class Favorites extends Fragment implements ShowNullMessage, SocketCallback, UpdateTheProductPrice {

    private View v;
    private ImageView logout, cart, back, ivListNull;
    private TextView cartText, listNull;
    private RecyclerView favoriteRecyclerView;
    private FavoriteRecyclerAdapter favoriteRecyclerAdapter;
    private boolean isLoading = false;
    private int CURRENT_PAGE = 0;
    private int LAST_PAGE = -1;
    private SwipeRefreshLayout favSwipeToRefresh;
    private BroadcastReceiver mReceiver;
    private ConstraintLayout cartConstraint;
    private FavoriteModel favoriteModel;
    private ArrayList<Datum> favoriteData = new ArrayList<>();
    public boolean radiationStatus=false;

    //Local broad cast to update the star on the products screen when update from the product detail screen
    public void registerBroadCastReceiver() {

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction() != null && intent.getAction().equals("UpdateFavoriteProducts")) {
                        callPaginationApiRefresh(context, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter("UpdateFavoriteProducts"));

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_fravorites_list, container, false);
        registerBroadCastReceiver();
        initViews();
        setAdapter();
        if (favoriteData.size() == 0) {
            callPaginationApi(getContext(), 0);
            Common.showDialog(getContext());
        }
        initScrollListener();

        sockets.initializeCallback(this);
        return v;
    }

    private void initViews() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.VISIBLE);

        favoriteRecyclerView = v.findViewById(R.id.favoritesRecycler);
        listNull = v.findViewById(R.id.tvListNull);
        ivListNull = v.findViewById(R.id.ivListNull);
        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        favSwipeToRefresh = v.findViewById(R.id.favSwipeToRefresh);
        favSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callPaginationApiRefresh(getContext(), 0);
            }
        });
    }


    //Initializing the adapter for the recycler view
    private void setAdapter() {
        favoriteRecyclerAdapter = new FavoriteRecyclerAdapter(getContext(), favoriteData, this, radiationStatus);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        favoriteRecyclerView.setLayoutManager(mLayoutManager);
        favoriteRecyclerView.setItemAnimator(new DefaultItemAnimator());
        favoriteRecyclerView.setAdapter(favoriteRecyclerAdapter);
    }

    //Function to check the scroll the last item on scroll for pagination
    private void initScrollListener() {
        favoriteRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == favoriteData.size() - 1) {
                        recyclerView.post(() -> loadMore());
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        favoriteData.add(null);
        favoriteRecyclerAdapter.notifyItemInserted(favoriteData.size() - 1);

        if (CURRENT_PAGE != LAST_PAGE) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    callPaginationApi(getContext(), CURRENT_PAGE);
                }
            }, 2000);
        } else {
            favoriteData.remove(favoriteData.size() - 1);
            favoriteRecyclerAdapter.notifyItemRemoved(favoriteData.size());
        }

    }


    //Calling the pagination api
    private void callPaginationApi(Context context, int c) {

        Api api = RetrofitClient.getClient().create(Api.class);

        final Call<FavoriteModel> pagination = api.getFavourite("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), c + 1);

        pagination.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(Call<FavoriteModel> call, Response<FavoriteModel> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: " + response);

                        LAST_PAGE = response.body().getMeta().getLastPage();
                        CURRENT_PAGE = response.body().getMeta().getCurrentPage();

                        radiationStatus=response.body().isRadiation_status();

                        if (favoriteData.size() > 0) {
                            favoriteData.remove(favoriteData.size() - 1);
                            favoriteRecyclerAdapter.notifyItemRemoved(favoriteData.size());
                        }

                        favoriteModel = new FavoriteModel(response.body().getData(),
                                response.body().getLinks(), response.body().getMeta(), response.body().getStatus(),
                                response.body().getMessage());

                        favoriteData.addAll(favoriteModel.getData());

                        if (favoriteData.size() == 0) {
                            listNull.setVisibility(View.VISIBLE);
                            ivListNull.setVisibility(View.VISIBLE);
                            favoriteRecyclerView.setVisibility(View.GONE);
                        } else {
                            listNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                            favoriteRecyclerView.setVisibility(View.VISIBLE);
                        }

                        favoriteRecyclerAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<FavoriteModel> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                listNull.setVisibility(View.VISIBLE);
                favoriteRecyclerView.setVisibility(View.GONE);
            }
        });


    }

    //calling the refresh api
    private void callPaginationApiRefresh(Context context, int c) {

        Api api = RetrofitClient.getClient().create(Api.class);

        final Call<FavoriteModel> pagination = api.getFavourite("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), c);

        pagination.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(Call<FavoriteModel> call, Response<FavoriteModel> response) {
                CURRENT_PAGE = 1;
                favSwipeToRefresh.setRefreshing(false);
                Common.dissmissDialog();
                favoriteData.clear();
                try {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: " + response);


                        if (favoriteData.size() > 0) {
                            favoriteData.remove(favoriteData.size() - 1);
                            favoriteRecyclerAdapter.notifyItemRemoved(favoriteData.size());
                        }

                        favoriteModel = new FavoriteModel(response.body().getData(),
                                response.body().getLinks(), response.body().getMeta(), response.body().getStatus(),
                                response.body().getMessage());

                        favoriteData.addAll(favoriteModel.getData());

                        if (favoriteData.size() == 0) {
                            listNull.setVisibility(View.VISIBLE);
                            ivListNull.setVisibility(View.VISIBLE);
                            favoriteRecyclerView.setVisibility(View.GONE);
                        } else {
                            listNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                            favoriteRecyclerView.setVisibility(View.VISIBLE);
                        }

                        favoriteRecyclerAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<FavoriteModel> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void showNullMessage() {
        listNull.setVisibility(View.VISIBLE);
        ivListNull.setVisibility(View.VISIBLE);
        favoriteRecyclerView.setVisibility(View.GONE);
    }


    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {

        if (tag.equals(update_product)) {
            try {
                String merchantId = jsonObject.getString("merchant_id");
                String productId = jsonObject.getString("product_id");
                String subProductId = jsonObject.getString("subproduct_id");
                String price = jsonObject.getString("price");
                String minPrice = jsonObject.getString("minprice");
                String subprominprice = jsonObject.getString("subprominprice");

                getActivity().runOnUiThread(() -> {
                    for (int i = 0; i < favoriteData.size(); i++) {
                        if (favoriteData.get(i).getId() == Integer.parseInt(productId)) {
                            favoriteData.get(i).setPrice(minPrice);
                            favoriteRecyclerAdapter.notifyItemChanged(i);
                        }
                    }

                    ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                            (price),(minPrice));

                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(String message, String tag) {
        Log.e(TAG, "onFailure: " + message + tag);
    }


    @Override
    public void updatePrice(int productId, String price) {
        for (int i = 0; i < favoriteData.size(); i++) {
            if (favoriteData.get(i).getId() == productId) {
                favoriteData.get(i).setPrice(price);
                favoriteRecyclerAdapter.notifyItemChanged(i);
            }
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
