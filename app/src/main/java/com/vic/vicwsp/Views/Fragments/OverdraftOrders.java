package com.vic.vicwsp.Views.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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

import com.vic.vicwsp.Controllers.Helpers.PastOrdersAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.OrdersHistory.Datum;
import com.vic.vicwsp.Models.Response.OrdersHistory.OrderHistory;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.SignIn;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Utils.Constants.mLastClickTime;

public class OverdraftOrders extends Fragment {

    private View v;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivListNull;
    private TextView listNull;
    private TextView hashNo, tvOrderDate, tvOrderAmount, tvOrderStatus;
    private ImageView btnOrderDetails;
    private PastOrdersAdapter prRecyclerAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ImageView back;
    private OrderHistory orderHistory;
    private ArrayList<Datum> orderHistoryData = new ArrayList<>();
    private ConstraintLayout overdraftItemConstraint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_overdraft_orders, container, false);

        initViews();
        getDataFromApi();

        return v;
    }

    private void initViews() {
        swipeRefreshLayout = v.findViewById(R.id.overdraftSwipe);
        swipeRefreshLayout.setOnRefreshListener(this::getDataFromApi);

        back = getActivity().findViewById(R.id.ivToolbarBack);
        back.setVisibility(View.GONE);
        recyclerView = v.findViewById(R.id.pastRecycler);
        listNull = v.findViewById(R.id.tvListNull);
        ivListNull = v.findViewById(R.id.ivListNull);

        overdraftItemConstraint = v.findViewById(R.id.overdraftItemConstraint);

        hashNo = v.findViewById(R.id.hashA);
        tvOrderAmount = v.findViewById(R.id.tvAmountA);
        btnOrderDetails = v.findViewById(R.id.btnDetails);
        tvOrderStatus = v.findViewById(R.id.tvStatusA);
        tvOrderDate = v.findViewById(R.id.tvDateA);


        btnOrderDetails.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            Datum datum = new Datum(orderHistoryData.get(0).getId(), orderHistoryData.get(0).getDate(),
                    orderHistoryData.get(0).getGrandtotal(), orderHistoryData.get(0).getSubtotal(),
                    orderHistoryData.get(0).getStatus(), orderHistoryData.get(0).getDetails());

            Bundle bundle = new Bundle();
            bundle.putBoolean("fromReceived", false);
            bundle.putParcelable("OrderHistoryDetail", datum);
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setArguments(bundle);

            sendFragment(orderDetail, "OrderDetail");
        });

    }

    private void sendFragment(Fragment fragment, String key) {
        ((AppCompatActivity) getContext())
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, fragment, "Order Detail")
                .addToBackStack(key).commit();
    }

    private void getDataFromApi() {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<OrderHistory> call = api.getOverDraftOrders("Bearer " +
                SharedPreference.getSimpleString(getContext(), Constants.accessToken), "overdraft");
        call.enqueue(new Callback<OrderHistory>() {
            @Override
            public void onResponse(Call<OrderHistory> call, Response<OrderHistory> response) {

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

                try {

                    if (response.isSuccessful()) {

                        orderHistory = new OrderHistory(response.body().getData(),
                                response.body().getLinks(), response.body().getMeta(), response.body().getStatus(),
                                response.body().getMessage());

                        for (int i = 0; i < orderHistoryData.size(); i++) {
                            Datum datum = new Datum(orderHistoryData.get(i).getId(), orderHistoryData.get(i).getDate(),
                                    orderHistoryData.get(i).getGrandtotal(), orderHistoryData.get(i).getSubtotal(),
                                    orderHistoryData.get(i).getStatus(), orderHistoryData.get(i).getDetails());

                            orderHistoryData.add(datum);
                        }

                        if (orderHistoryData.size() == 0) {
                            listNull.setVisibility(View.VISIBLE);
                            ivListNull.setVisibility(View.VISIBLE);
                            overdraftItemConstraint.setVisibility(View.GONE);
                        } else {
                            listNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                            overdraftItemConstraint.setVisibility(View.VISIBLE);

                            hashNo.setText(String.valueOf(orderHistoryData.get(0).getId()));
                            tvOrderDate.setText(String.valueOf(orderHistoryData.get(0).getDate()));

                            tvOrderAmount.setText(Common.convertInKFormat(String.valueOf(AppUtils.convertStringToDouble(orderHistoryData.get(0).getSubtotal())
                                    - AppUtils.convertStringToDouble(orderHistoryData.get(0).getDetails().getDiscount())))
                                    + SharedPreference.getSimpleString(getContext(), Constants.currency));

                            switch (orderHistoryData.get(0).getStatus()) {
                                case "Completed":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.CompleteStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.signUpButtonColor));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Pre-order":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.PreOrderStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.signUpButtonColor));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Pending-payment":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.Pending_payment));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.signUpButtonColor));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Paid":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.PaidStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.signUpButtonColor));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Nego-approved":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.NegoapprovedStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.signUpButtonColor));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Declined":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.DeclineStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.red));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Expired":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.ExpiredStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.red));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Unpaid":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.UnpaidStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.red));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Pending":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.PendingStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.grey));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Negotiation":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.negotitiationStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.splashColor));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_nego), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                case "Nego-rejected":
                                    tvOrderStatus.setText(getContext().getResources().getString(R.string.NegoRejectedStatus));
                                    tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.red));
                                    tvOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                                    tvOrderStatus.setCompoundDrawablePadding(8);
                                    break;
                                default:
                                    break;

                            }
                        }

                    } else if (response.code() == 401) {
                        SharedPreference.saveSimpleString(getContext(), Constants.isLoggedIn, "false");
                        Intent i = new Intent(getContext(), SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
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
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

                listNull.setVisibility(View.VISIBLE);
                ivListNull.setVisibility(View.VISIBLE);
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });

    }
}
