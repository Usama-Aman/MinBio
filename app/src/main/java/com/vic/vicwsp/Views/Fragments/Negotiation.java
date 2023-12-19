package com.vic.vicwsp.Views.Fragments;

import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.NegotiationRecyclerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.UpdateDeliveryCharges;
import com.vic.vicwsp.Models.Response.NegoUpdated.Data;
import com.vic.vicwsp.Models.Response.NegoUpdated.NegoUpdated;
import com.vic.vicwsp.Models.Response.NegoUpdated.NegotiationList;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.NegoChat;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingAddress;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingDeliveryCharges;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingLatlng;

public class Negotiation extends Fragment implements UpdateDeliveryCharges {

    private View v;
    private TextView header;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TextView tvListNull, tvPaymentPricePerKg;
    private ImageView ivToolbarCart;
    private NegotiationRecyclerAdapter negotiationRecyclerAdapter;
    private boolean fromBuy, fromOrderDetails;
    private int orderId;
    private ImageView logout, cart, back;
    private TextView cartText;
    private NegoUpdated negoUpdated;
    private Data negoData;
    private ArrayList<NegotiationList> negotiationsArrayList
            = new ArrayList<>();
    private ConstraintLayout cartConstraint, chatImage;
    private TextView marchantName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v == null)
            v = inflater.inflate(R.layout.fragment_nego, container, false);
        shippingAddress = "";
        shippingDeliveryCharges = "0.00";
        shippingLatlng = new LatLng(0.0, 0.0);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initViews();
        checkBundle();

        return v;
    }

    //Initializing the views
    private void initViews() {
        header = v.findViewById(R.id.tvPayment);
        tvPaymentPricePerKg = v.findViewById(R.id.tvPaymentPricePerKg);
        recyclerView = v.findViewById(R.id.paymentRecycler);
        marchantName = v.findViewById(R.id.vendorNameNego);
        chatImage = v.findViewById(R.id.negoChatLayout);
        chatImage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NegoChat.class);
            intent.putExtra("orderId", negoData.getParentOrderId());
            intent.putExtra("orderNumber", String.valueOf(orderId));
            intent.putExtra("userName", negoData.getMerchantName());
            startActivity(intent);
        });
        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);

        ConstraintLayout supportConstraint = getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.GONE);
    }

    //Checking the  bundle
    private void checkBundle() {
        try {
            fromBuy = getArguments().getBoolean("fromBuy");
            fromOrderDetails = getArguments().getBoolean("fromOrderDetails");
            if (fromBuy && !fromOrderDetails) {
                if (negotiationsArrayList.size() == 0) {
                    negoData = getArguments().getParcelable("NegoData");
                    negotiationsArrayList = negoData.getNegotiations();
                    tvPaymentPricePerKg.setText(getContext().getResources().getString(R.string.price) + " / " + negoData.getNegotiations().get(negoData.getNegotiations().size() - 1).getUnit());
                }
                if (!shippingAddress.equals(""))
                    updateDeliveryCharges(getContext(), negoData.getMerchantId(), String.valueOf(shippingLatlng.latitude),
                            String.valueOf(shippingLatlng.longitude));
                marchantName.setText(negoData.getMerchantName());

                if (negotiationsArrayList.size() == 0)
                    getFragmentManager().popBackStack();
                else
                    initRecyclerView();
                negotiationRecyclerAdapter.notifyDataSetChanged();
            } else if (fromOrderDetails && !fromBuy) {
                orderId = getArguments().getInt("orderId");
                callGetNegoApi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyAdapter() {
        negotiationRecyclerAdapter.notifyDataSetChanged();
    }


    public void showNullMessage() {
        tvListNull.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }


    //Initializing the adapter for recycler
    private void initRecyclerView() {
        negotiationRecyclerAdapter = new NegotiationRecyclerAdapter(getContext(), negoData,
                negotiationsArrayList, fromOrderDetails, orderId, this);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(negotiationRecyclerAdapter);
    }

    //Calling the api to get the negotiation list
    private void callGetNegoApi() {
        if (SharedPreference.getBoolean(getContext(), "FromNoti")) {
            SharedPreference.saveBoolean(getContext(), "FromNoti", false);
        } else
            Common.showDialog(getContext());
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<NegoUpdated> call = api.getProposals("Bearer " +
                SharedPreference.getSharedPrefValue(getContext(), Constants.accessToken), orderId, "past");
        call.enqueue(new Callback<NegoUpdated>() {
            @Override
            public void onResponse(Call<NegoUpdated> call, Response<NegoUpdated> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        negotiationsArrayList.clear();
                        negoUpdated = new NegoUpdated(response.body().getStatus(),
                                response.body().getMessage(), response.body().getData());
                        negoData = negoUpdated.getData();

                        negotiationsArrayList = negoData.getNegotiations();

                        marchantName.setText(negoData.getMerchantName());
                        initRecyclerView();
                        negotiationRecyclerAdapter.notifyDataSetChanged();

                        tvPaymentPricePerKg.setText(getContext().getResources().getString(R.string.price) + " / " + negoData.getNegotiations().get(negoData.getNegotiations().size() - 1).getUnit());


                        if (negoData.getOrder_status().equals("Expired") || negoData.getOrder_status().equals("Nego-approved")) {
                            chatImage.setVisibility(View.GONE);
                            chatImage.setEnabled(false);
                        } else {
                            chatImage.setVisibility(View.VISIBLE);
                            chatImage.setEnabled(true);
                        }

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<NegoUpdated> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }


    //Update delivery api interface to update the charges
    @Override
    public void updateDeliveryCharges(Context context, String merchants, String lat, String lng) {

        Common.showDialog(getContext());

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> getDeliveryCharges = api.getDeliveryCharges("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), merchants, lat, lng);

        getDeliveryCharges.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Common.dissmissDialog();
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("status")) {
                            JSONObject data = new JSONObject();
                            data = jsonObject.getJSONObject("data");

                            shippingDeliveryCharges = data.getString("delivery_charges");
                        }

                        negoData.setDeliveryCharges(shippingDeliveryCharges);
                        negotiationRecyclerAdapter.notifyDataSetChanged();

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        Log.d("Carts Delivery", "onResponse: " + jsonObject.getString("message"));
                    }
                    negotiationRecyclerAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Carts Delivery", "onFailure: " + t.getMessage());
            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }

}
