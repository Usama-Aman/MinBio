package com.vic.vicwsp.Views.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.ReceivedNegoAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.NegoUpdated.Data;
import com.vic.vicwsp.Models.Response.NegoUpdated.NegoUpdated;
import com.vic.vicwsp.Models.Response.NegoUpdated.NegotiationList;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.NegoChat;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicwsp.Utils.Common.showToast;

public class ReceivedNego extends Fragment {

    private View v;
    private TextView header, tvNegoTimerText, tvPaymentPricePerKg;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ImageView logout, back;
    private NegoUpdated negoUpdated;
    private Data negoData;
    private ArrayList<NegotiationList> negotiationsArrayList = new ArrayList<>();
    private ReceivedNegoAdapter receivedNegoAdapter;
    private ConstraintLayout cartConstraint;
    private int orderId;
    private ConstraintLayout btnAcceptOrder, btnRejectOrder;
    private ConstraintLayout receivedNegoBottom;
    private Context context;
    private ConstraintLayout chat, sendProposalConstraint;
    private EditText etNegoPrice;
    private ImageView btnSendProposal;
    private TextView tvNegoPriceSign;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_received_nego, container, false);
        orderId = getArguments().getInt("orderId");
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initViews();
        callGetNegoApi();

        return v;
    }


    //Initializing the views
    private void initViews() {
        context = getContext();

        RecyclerView ticker = getActivity().findViewById(R.id.pricesTicker);
        ticker.setVisibility(View.GONE);

        header = v.findViewById(R.id.tvPayment);
        sendProposalConstraint = v.findViewById(R.id.sendProposalConstraint);
        chat = v.findViewById(R.id.negoChatLayout);
        chat.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NegoChat.class);
            intent.putExtra("orderId", negoData.getChildOrderId());
            intent.putExtra("orderNumber", String.valueOf(orderId));
            intent.putExtra("userName", negoData.getMerchantName());
            startActivity(intent);
        });

        recyclerView = v.findViewById(R.id.paymentRecycler);
        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        ConstraintLayout supportConstraint = getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.GONE);
        receivedNegoBottom = v.findViewById(R.id.receivedNegoBottom);
        tvNegoTimerText = v.findViewById(R.id.tvNegoTimerRecieved);
        tvNegoTimerText.setVisibility(View.VISIBLE);

        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);

        etNegoPrice = v.findViewById(R.id.etNegoPrice);
        btnSendProposal = v.findViewById(R.id.sendProposal);
        tvNegoPriceSign = v.findViewById(R.id.tvNegoPriceSign);
        tvNegoPriceSign.setText(SharedPreference.getSimpleString(context, Constants.currency));

        btnSendProposal.setOnClickListener(b -> {
            if (etNegoPrice.getText().toString().isEmpty())
                showToast((AppCompatActivity) context, context.getResources().getString(R.string.negoPriceErrorMessage), false);
            else
                postNego();
        });

        btnAcceptOrder = v.findViewById(R.id.btnAcceptOrder);
        btnRejectOrder = v.findViewById(R.id.btnRejectOrder);

        tvPaymentPricePerKg = v.findViewById(R.id.tvPaymentPricePerKg);

        btnAcceptOrder.setOnClickListener(view -> {
            acceptProposal();
        });
        btnRejectOrder.setOnClickListener(view -> {
            rejectProposal();
        });
    }

    //Initializing the adapter for recycler
    private void initRecyclerView() {
        receivedNegoAdapter = new ReceivedNegoAdapter(negotiationsArrayList, getContext());
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(receivedNegoAdapter);
    }

    //Calling the api to get the negotiation list
    private void callGetNegoApi() {
        Common.showDialog(getContext());
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<NegoUpdated> call = api.getProposals("Bearer " +
                SharedPreference.getSharedPrefValue(getContext(), Constants.accessToken), orderId, "received");
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
                        tvPaymentPricePerKg.setText(getContext().getResources().getString(R.string.price) + " / " + negoData.getNegotiations().get(negoData.getNegotiations().size() - 1).getUnit());

                        if (negoData.getOrder_status().equals("Negotiation")) {
                            checkForIsMerchantProposal();
                            countDownStart();
                        } else {
                            receivedNegoBottom.setVisibility(View.GONE);
                            sendProposalConstraint.setVisibility(View.GONE);
                        }


                        initRecyclerView();

                        if (negoData.getOrder_status().equals("Expired") || negoData.getOrder_status().equals("Nego-approved")) {
                            chat.setVisibility(View.GONE);
                            chat.setEnabled(false);
                        } else {
                            chat.setVisibility(View.VISIBLE);
                            chat.setEnabled(true);
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

    private void checkForIsMerchantProposal() {

        if (negotiationsArrayList.get(0).getIsMerchant() == 0) {
            receivedNegoBottom.setVisibility(View.VISIBLE);
            sendProposalConstraint.setVisibility(View.VISIBLE);
        } else {
            sendProposalConstraint.setVisibility(View.GONE);
            receivedNegoBottom.setVisibility(View.GONE);
        }

    }

    private void rejectProposal() {
        Common.showDialog(getContext());
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.rejectProposal("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), orderId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast(((AppCompatActivity) context), jsonObject.getString("message"), true);

                        getFragmentManager().popBackStack();
                        Intent intent = new Intent("refreshTheReceived");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) context), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("MainActivity", "onFailure: " + t.getMessage());
            }
        });

    }

    private void acceptProposal() {
        Common.showDialog(getContext());

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.acceptProposal("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), orderId, 1);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast(((AppCompatActivity) context), jsonObject.getString("message"), true);

                        getFragmentManager().popBackStack();
                        Intent intent = new Intent("refreshTheReceived");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) context), jsonObject.getString("message"), false);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("MainActivity", "onFailure: " + t.getMessage());
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void countDownStart() {

        final long[] diff = {0};
        final long[] minutes = {0};
        final long[] seconds = {0};
        final long[] hours = {0};


        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(negoData.getExpired_at());
            System.out.println(date);


            Date c = Calendar.getInstance().getTime();

            String formattedDate = format.format(c);
            Date e = format.parse(formattedDate);

            diff[0] = date.getTime() - e.getTime();
            minutes[0] = diff[0] / (60 * 1000) % 60;
            seconds[0] = diff[0] / 1000 % 60;
            hours[0] = diff[0] / (60 * 60 * 1000);


            NumberFormat f = new DecimalFormat("00");

            if (diff[0] > 0) {
                tvNegoTimerText.setText(" " + f.format(hours[0]) + ":" + String.valueOf(f.format(minutes[0])) + ":" + String.valueOf(f.format(seconds[0]))
                        + " " + context.getResources().getString(R.string.in_minutes));
                if (negoData.getOrder_status().equals("Negotiation")) {
//                    receivedNegoBottom.setVisibility(View.VISIBLE);
                    checkForIsMerchantProposal();
                } else {
                    receivedNegoBottom.setVisibility(View.GONE);
                    sendProposalConstraint.setVisibility(View.GONE);
                }
            } else {
                tvNegoTimerText.setText(" 00:00:00 " + context.getResources().getString(R.string.in_minutes));
                receivedNegoBottom.setVisibility(View.GONE);
                receivedNegoBottom.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        if (diff[0] > 0) {

            CountDownTimer t = new CountDownTimer(diff[0], 1000) {
                public void onTick(long millisUntilFinished) {

                    try {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = format.parse(negoData.getExpired_at());

                        Date c = Calendar.getInstance().getTime();

                        String formattedDate = format.format(c);
                        Date e = format.parse(formattedDate);

                        diff[0] = date.getTime() - e.getTime();
                        minutes[0] = diff[0] / (60 * 1000) % 60;
                        seconds[0] = diff[0] / 1000 % 60;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    NumberFormat f = new DecimalFormat("00");

                    tvNegoTimerText.setText(" " + f.format(hours[0]) + ":" + String.valueOf(f.format(minutes[0])) + ":" + String.valueOf(f.format(seconds[0]))
                            + " " + context.getResources().getString(R.string.in_minutes));
                }

                public void onFinish() {

                    tvNegoTimerText.setText("00:00:00 " + context.getResources().getString(R.string.in_minutes));
                    System.out.println("Timer is stopped");
                    System.gc();

                    receivedNegoBottom.setVisibility(View.GONE);
                    hideChat();
                }
            }.start();

        }
    }


    private void showChat() {
        chat.setVisibility(View.VISIBLE);
        chat.setEnabled(true);
    }

    private void hideChat() {
        chat.setVisibility(View.GONE);
        chat.setEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }

    private void postNego() {
        Common.showDialog(context);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<NegoUpdated> call = api.postProposals("Bearer " +
                        SharedPreference.getSimpleString(context, Constants.accessToken), orderId,
                Common.round(AppUtils.convertStringToDouble(etNegoPrice.getText().toString()), 2), 1);

        call.enqueue(new Callback<NegoUpdated>() {
            @Override
            public void onResponse(Call<NegoUpdated> call, Response<NegoUpdated> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        callGetNegoApi();

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast((AppCompatActivity) context, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<NegoUpdated> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(Constraints.TAG, "onFailure: " + t.getMessage());
            }
        });

    }


}
