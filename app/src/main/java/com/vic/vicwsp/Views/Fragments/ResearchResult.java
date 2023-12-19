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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.ResearchResultAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
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

public class ResearchResult extends Fragment implements SocketCallback {

    private View v;
    private RecyclerView researchRecyclerView;
    private ImageView logout, cart, back, ivListNull;
    private TextView listNull;
    private ConstraintLayout cartConstraint;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false;
    private int CURRENT_PAGE = 0;
    private int LAST_PAGE = -1;
    private String min, max;
    private FavoriteModel favoriteModel;
    private ArrayList<Datum> favoriteData = new ArrayList<>();
    private ResearchResultAdapter researchResultAdapter;
    private int originId = 0, merchantId = 0, productId = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_research_result, container, false);

        initViews();
        setAdapter();
        initScrollListener();
        getDataFromBundle();

        sockets.initializeCallback(this);
        return v;
    }


    //Initializing the bundle
    private void getDataFromBundle() {
        min = getArguments().getString("min");
        max = getArguments().getString("max");
        productId = getArguments().getInt("productId");
        merchantId = getArguments().getInt("merchantId");
        originId = getArguments().getInt("originId");


        if (favoriteData.size() == 0) {
            Common.showDialog(getContext());
            callPaginationApi(CURRENT_PAGE, false, getContext());
        }
    }

    private void initViews() {

        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.VISIBLE);

        ConstraintLayout supportConstraint= getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.GONE);

        researchRecyclerView = v.findViewById(R.id.researchRecycler);
        listNull = v.findViewById(R.id.tvListNull);
        ivListNull = v.findViewById(R.id.ivListNull);
        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        swipeRefreshLayout = v.findViewById(R.id.swipeToRefreshProducts);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            callPaginationApi(0, true, getContext());
        });

    }


    private void initScrollListener() {
        researchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        researchResultAdapter.notifyItemInserted(favoriteData.size() - 1);

        if (CURRENT_PAGE != LAST_PAGE)

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    callPaginationApi(CURRENT_PAGE, false, getContext());
                }
            }, 1500);

        else {
            favoriteData.remove(favoriteData.size() - 1);
            researchResultAdapter.notifyItemRemoved(favoriteData.size());
        }
    }


    private void callPaginationApi(int c, boolean isRefresh, Context context) {

        try {
            Api api = RetrofitClient.getClient().create(Api.class);
            Call<FavoriteModel> call = null;
            call = api.searchProduct("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken),
                    productId, merchantId, originId, min, max, c + 1);

            call.enqueue(new Callback<FavoriteModel>() {
                @Override
                public void onResponse(Call<FavoriteModel> call, Response<FavoriteModel> response) {
                    CURRENT_PAGE = 1;
                    swipeRefreshLayout.setRefreshing(false);
                    Common.dissmissDialog();
                    if (isRefresh)
                        favoriteData.clear();
                    try {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: " + response);

                            LAST_PAGE = response.body().getMeta().getLastPage();
                            CURRENT_PAGE = response.body().getMeta().getCurrentPage();


                            if (favoriteData.size() > 0) {
                                favoriteData.remove(favoriteData.size() - 1);
                                researchResultAdapter.notifyItemRemoved(favoriteData.size());
                            }

                            favoriteModel = new FavoriteModel(response.body().getData(),
                                    response.body().getLinks(), response.body().getMeta(), response.body().getStatus(),
                                    response.body().getMessage());

                            favoriteData.addAll(favoriteModel.getData());

                            if (favoriteData.size() == 0) {
                                listNull.setVisibility(View.VISIBLE);
                                ivListNull.setVisibility(View.VISIBLE);
                                researchRecyclerView.setVisibility(View.GONE);
                            } else {
                                listNull.setVisibility(View.GONE);
                                ivListNull.setVisibility(View.GONE);
                                researchRecyclerView.setVisibility(View.VISIBLE);
                            }

                            researchResultAdapter.notifyDataSetChanged();
                            isLoading = false;

                        } else {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            showToast(((AppCompatActivity) context), jsonObject.getString("message"), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<FavoriteModel> call, Throwable t) {
                    Common.dissmissDialog();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setAdapter() {
        researchResultAdapter = new ResearchResultAdapter(getContext(), favoriteData);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        researchRecyclerView.setLayoutManager(mLayoutManager);
        researchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        researchRecyclerView.setAdapter(researchResultAdapter);
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
                            researchResultAdapter.notifyItemChanged(i);
                        }
                    }

                    ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                            (price), (minPrice));

                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(String message, String tag) {

    }
}
