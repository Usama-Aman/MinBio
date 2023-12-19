package com.vic.vicwsp.Views.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.ReceivedOrdersAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.OrdersHistory.Datum;
import com.vic.vicwsp.Models.Response.OrdersHistory.OrderHistory;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class ReceivedOrders extends Fragment {

    private View v;
    private ImageView ivListNull, back;
    private TextView listNull;
    private ReceivedOrdersAdapter receivedOrdersAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    boolean isLoading = false;
    private int CURRENT_PAGE = 0;
    private int LAST_PAGE = -1;
    private OrderHistory orderHistory;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Datum> orderHistoryData = new ArrayList<>();

    private void refreshTheReceivedOrder() {

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                callPaginationApi(0, true, context);
            }

        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("refreshTheReceived"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_orders_received, container, false);
        refreshTheReceivedOrder();
        initViews();
        initRecyclerView();
        initScrollListener();
        if (orderHistoryData.size() == 0) {
            callPaginationApi(0, false, getContext());
        }
        return v;
    }


    //Initializing the views
    private void initViews() {
        back = getActivity().findViewById(R.id.ivToolbarBack);
        back.setVisibility(View.GONE);
        recyclerView = v.findViewById(R.id.receivedRecycler);
        listNull = v.findViewById(R.id.tvListNull);
        ivListNull = v.findViewById(R.id.ivListNull);
        swipeRefreshLayout = v.findViewById(R.id.receivedSwipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            callPaginationApi(0, true, getContext());
        });
    }

    //Initializing the recycler view
    private void initRecyclerView() {
        receivedOrdersAdapter = new ReceivedOrdersAdapter(getContext(), orderHistoryData);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(receivedOrdersAdapter);
    }


    //Initializing the scroll view for pagination
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == orderHistoryData.size() - 1) {
                        recyclerView.post(() -> loadMore());
                        isLoading = true;
                    }
                }
            }
        });
    }


    private void loadMore() {
        try {
            orderHistoryData.add(null);
            receivedOrdersAdapter.notifyItemInserted(orderHistoryData.size() - 1);

            if (CURRENT_PAGE != LAST_PAGE) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        callPaginationApi(CURRENT_PAGE, false, getContext());
                    }
                }, 2000);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        orderHistoryData.remove(orderHistoryData.size() - 1);
                        receivedOrdersAdapter.notifyItemRemoved(orderHistoryData.size());
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //calling pagination api to get the more orders
    private void callPaginationApi(int c, boolean isRefresh, Context context) {
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<OrderHistory> pagination = null;
        if (isRefresh)
            pagination = api.getReceivedOrders("Bearer " +
                    SharedPreference.getSimpleString(context, Constants.accessToken), 0);
        else
            pagination = api.getReceivedOrders("Bearer " +
                    SharedPreference.getSimpleString(context, Constants.accessToken), c + 1);


        pagination.enqueue(new Callback<OrderHistory>() {
            @Override
            public void onResponse(Call<OrderHistory> call, Response<OrderHistory> response) {
                if (isRefresh) {
                    swipeRefreshLayout.setRefreshing(false);
                    orderHistoryData.clear();
                }
                try {
                    if (response.isSuccessful()) {

                        LAST_PAGE = response.body().getMeta().getLastPage();
                        CURRENT_PAGE = response.body().getMeta().getCurrentPage();


                        if (orderHistoryData.size() > 0) {
                            orderHistoryData.remove(orderHistoryData.size() - 1);
                            receivedOrdersAdapter.notifyItemRemoved(orderHistoryData.size());
                        }

                        orderHistory = new OrderHistory(response.body().getData(),
                                response.body().getLinks(), response.body().getMeta(), response.body().getStatus(),
                                response.body().getMessage());

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            Datum datum = new Datum(response.body().getData().get(i).getId(), response.body().getData().get(i).getDate(),
                                    response.body().getData().get(i).getGrandtotal(), response.body().getData().get(i).getSubtotal(),
                                    response.body().getData().get(i).getStatus(), response.body().getData().get(i).getDetails());

                            orderHistoryData.add(datum);
                        }

                        if (orderHistoryData.size() == 0) {
                            listNull.setVisibility(View.VISIBLE);
                            ivListNull.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            listNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        receivedOrdersAdapter.notifyDataSetChanged();
                        isLoading = false;

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(getActivity(), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<OrderHistory> call, Throwable t) {
                listNull.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }

}
