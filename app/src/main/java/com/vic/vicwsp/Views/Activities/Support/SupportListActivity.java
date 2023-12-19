package com.vic.vicwsp.Views.Activities.Support;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.vic.vicwsp.Controllers.Helpers.Support.SupportListAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.Support.SupportList.SupportList;
import com.vic.vicwsp.Models.Response.Support.SupportList.SupportResponse;
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

public class SupportListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView complaintsListRecycler;
    private LinearLayoutManager layoutManager;
    private SupportListAdapter complaintsAdapter;
    private ImageView back, ivAddComplaint;
    private ImageView ivListNull;
    private TextView listNull;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<SupportList> complaintsListData = new ArrayList<>();
    boolean isLoading = false;
    private int CURRENT_PAGE = 0;
    private int LAST_PAGE = -1;
    SupportList complaintsData;

    TextView tvComplaintTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_complaints);
        initViews();
        initRecyclerView();
        initScrollListener();

        if (complaintsListData.size() == 0) {
            callPaginationApi(0, true, SupportListActivity.this);
        }
    }

    //Initializing the views
    private void initViews() {

        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(v -> finish());
        back.setVisibility(View.VISIBLE);

        ivAddComplaint = findViewById(R.id.ivAddComplaint);
        ivAddComplaint.setOnClickListener(v -> openComplaintFormActivity());
        ivAddComplaint.setVisibility(View.VISIBLE);

        tvComplaintTitle = findViewById(R.id.tvComplaintTitle);
        tvComplaintTitle.setText(getResources().getString(R.string.my_support));


        complaintsListRecycler = findViewById(R.id.complaintsListRecycler);
        listNull = findViewById(R.id.tvListNull);
        ivListNull = findViewById(R.id.ivListNull);

        swipeRefreshLayout = findViewById(R.id.complaintsListSwipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            callPaginationApi(0, true, SupportListActivity.this);
        });

    }

    //Initializing the recycler view
    private void initRecyclerView() {
        complaintsAdapter = new SupportListAdapter(SupportListActivity.this, complaintsListData);
        layoutManager = new LinearLayoutManager(SupportListActivity.this);
        complaintsListRecycler.setLayoutManager(layoutManager);
        complaintsListRecycler.setAdapter(complaintsAdapter);
    }

    //Initializing the scroll view for pagination
    private void initScrollListener() {

        complaintsListRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == complaintsListData.size() - 1) {
                        recyclerView.post(() -> loadMore());
                        isLoading = true;
                    }
                }
            }
        });
    }


    private void loadMore() {
        try {
            complaintsListData.add(null);
            complaintsAdapter.notifyItemInserted(complaintsListData.size() - 1);

            if (CURRENT_PAGE != LAST_PAGE) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        callPaginationApi(CURRENT_PAGE, false, SupportListActivity.this);
                    }
                }, 2000);
            } else {
                complaintsListRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        complaintsListData.remove(complaintsListData.size() - 1);
                        complaintsAdapter.notifyItemRemoved(complaintsListData.size());
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

        Call<SupportResponse> pagination = null;
        if (isRefresh) {
            Common.showDialog(SupportListActivity.this);
            pagination = api.getSupportList("Bearer " +
                    SharedPreference.getSimpleString(context, Constants.accessToken), 0);
        } else {
            pagination = api.getSupportList("Bearer " +
                    SharedPreference.getSimpleString(context, Constants.accessToken), c + 1);
        }


        pagination.enqueue(new Callback<SupportResponse>() {
            @Override
            public void onResponse(Call<SupportResponse> call, Response<SupportResponse> response) {
                Common.dissmissDialog();
                if (isRefresh) {

                    swipeRefreshLayout.setRefreshing(false);
                    complaintsListData.clear();
                }
                try {
                    if (response.isSuccessful()) {

                        LAST_PAGE = response.body().getSupportResponseData().getLastPage();
                        CURRENT_PAGE = response.body().getSupportResponseData().getCurrentPage();


                        if (complaintsListData.size() > 0) {
                            complaintsListData.remove(complaintsListData.size() - 1);
                            complaintsAdapter.notifyItemRemoved(complaintsListData.size());
                        }

                  /*      complaintsData = new SupportList(response.body().getData(),
                                response.body().getLinks(), response.body().getMeta(), response.body().getStatus(),
                                response.body().getMessage());*/

                        if (response.body().getSupportResponseData().getSupportList() != null && response.body().getSupportResponseData().getSupportList().size() != 0)
                            for (int i = 0; i < response.body().getSupportResponseData().getSupportList().size(); i++) {

                                complaintsData = response.body().getSupportResponseData().getSupportList().get(i);
                                SupportList datum = new SupportList(complaintsData.getId(), complaintsData.getUserId(),
                                        complaintsData.getSupportTopicId(), complaintsData.getType(), complaintsData.getEmail(),
                                        complaintsData.getComment(), complaintsData.getIsRead(), complaintsData.getSupportNo(), complaintsData.getStatus(), complaintsData.getAssignedTo(),
                                        complaintsData.getCreatedAt(), complaintsData.getUpdatedAt(), complaintsData.getSupportAssignedTo());

                                complaintsListData.add(datum);
                            }

                        if (complaintsListData.size() == 0) {
                            listNull.setVisibility(View.VISIBLE);
                            ivListNull.setVisibility(View.VISIBLE);
                            complaintsListRecycler.setVisibility(View.GONE);
                        } else {
                            listNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                            complaintsListRecycler.setVisibility(View.VISIBLE);
                        }

                        complaintsAdapter.notifyDataSetChanged();
                        isLoading = false;

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SupportListActivity.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SupportResponse> call, Throwable t) {
                Common.dissmissDialog();
                listNull.setVisibility(View.VISIBLE);
                complaintsListRecycler.setVisibility(View.GONE);
            }
        });
    }

    public void openComplaintFormActivity() {
        Intent i = new Intent(SupportListActivity.this, SendSupport.class);
        startActivity(i);
    }

    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SendSupport.isSendSupportOpen) {
            if (complaintsListData != null && complaintsListData.size() != 0) {
                complaintsListData.clear();
                complaintsAdapter.notifyDataSetChanged();
                callPaginationApi(0, true, SupportListActivity.this);
            }
        }
        SendSupport.isSendSupportOpen = false;
    }

    @Override
    public void onClick(View v) {

    }
}
