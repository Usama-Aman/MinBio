package com.vic.vicwsp.Views.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.MyProductsAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.ProductsPagination.Datum;
import com.vic.vicwsp.Models.Response.ProductsPagination.ProductsPagination;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.SignIn;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class MyProducts extends Fragment {

    private View v;
    private ImageView logout, cart, back, ivListNull;
    private TextView cartText, listNull;
    private RecyclerView productsRecyclerView;
    private MyProductsAdapter myProductsAdapter;
    private ProductsPagination productsPagination;
    private ArrayList<Datum> productsData = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private String catId;
    private boolean fromSearch = false;
    private boolean isLoading = false;
    private int CURRENT_PAGE = 0;
    private int LAST_PAGE = -1;
    private ConstraintLayout cartConstraint,supportConstraint;
    public boolean radiationStatus = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_products_list, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initViews();
        setAdapter();
        initScrollListener();

        if (productsData.size() == 0) {
            Common.showDialog(getContext());
            callPaginationApi(0, false, getContext());
        }


        return v;
    }

    private void initViews() {

        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.VISIBLE);

        supportConstraint= getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.VISIBLE);
        supportConstraint.setOnClickListener(v -> {
            ((MainActivity) getContext()).showSupportDialog();
        });

        ConstraintLayout changeLanguageLayout = getActivity().findViewById(R.id.changeLanguageLayout);
        changeLanguageLayout.setVisibility(View.GONE);

        TextView notificationBadge = getActivity().findViewById(R.id.notificationBadge);
        notificationBadge.setVisibility(View.VISIBLE);
        ImageView ivNotifications = getActivity().findViewById(R.id.ivNotifications);
        ivNotifications.setVisibility(View.VISIBLE);

        //Updating the notification badge
        int c = ((MainActivity) getContext()).notificationCount;
        ((MainActivity) getContext()).notificationCount = 0;
        ((MainActivity) getContext()).setNotificationBadge(c, 0);

        productsRecyclerView = v.findViewById(R.id.productsRecycler);
        listNull = v.findViewById(R.id.tvListNull);
        ivListNull = v.findViewById(R.id.ivListNull);
        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        back.setVisibility(View.GONE);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        swipeRefreshLayout = v.findViewById(R.id.swipeToRefreshProducts);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            callPaginationApi(CURRENT_PAGE, true, getContext());
        });

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
        myProductsAdapter.notifyItemInserted(productsData.size() - 1);

        if (CURRENT_PAGE != LAST_PAGE)

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    callPaginationApi(CURRENT_PAGE, false, getContext());
                }
            }, 2000);

        else {
            productsData.remove(productsData.size() - 1);
            myProductsAdapter.notifyItemRemoved(productsData.size());
        }
    }

    private void callPaginationApi(int c, boolean isRefresh, Context context) {

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ProductsPagination> call = null;


        if (isRefresh)
            call = api.getMerchantProducts("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken), 0);
        else
            call = api.getMerchantProducts("Bearer " + SharedPreference.getSimpleString(context, Constants.accessToken), c + 1);


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

                        LAST_PAGE = response.body().getMeta().getLastPage();
                        CURRENT_PAGE = response.body().getMeta().getCurrentPage();


                        if (productsData.size() > 0) {
                            productsData.remove(productsData.size() - 1);
                            myProductsAdapter.notifyItemRemoved(productsData.size());
                        }

                        radiationStatus = response.body().isRadiation_status();

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

                        myProductsAdapter.notifyDataSetChanged();
                        isLoading = false;

                    }else if (response.code() == 401) {
                        SharedPreference.saveSimpleString(getContext(), Constants.isLoggedIn, "false");
                        Intent i = new Intent(getContext(), SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }  else {
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


    }

    private void setAdapter() {
        myProductsAdapter = new MyProductsAdapter(getContext(), productsData,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        productsRecyclerView.setLayoutManager(mLayoutManager);
        productsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        productsRecyclerView.setAdapter(myProductsAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }
}
