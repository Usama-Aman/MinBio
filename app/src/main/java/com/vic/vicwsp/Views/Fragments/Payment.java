package com.vic.vicwsp.Views.Fragments;

import android.content.ContentValues;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Helpers.PaymentRecyclerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

public class Payment extends Fragment implements SocketCallback {


    private PaymentRecyclerAdapter paymentRecyclerAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private View v;
    private ArrayList<CartModel> carts = new ArrayList<>();
    private TextView tvListNull;
    private ImageView ivToolbarCart;
    private boolean fromOrderDetails;
    private int orderId = 0;
    private String total = "0";
    private ImageView back;
    private ConstraintLayout cartConstraint;
    private AlertDialog alertDialog;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_payment, container, false);
        context = getContext();
        initViews();
        checkBundle();
        sockets.initializeCallback(this);

        return v;
    }

    //Checking bundle and taking decisions
    private void checkBundle() {

        try {
            fromOrderDetails = getArguments().getBoolean("fromOrderDetails");
            if (fromOrderDetails) {
                orderId = getArguments().getInt("orderId");

                if (SharedPreference.getBoolean(getContext(), "FromNoti")) {
                    SharedPreference.saveBoolean(getContext(), "FromNoti", false);
                } else
                    Common.showDialog(getContext());

                callApi();
            } else
                getDataFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Initializing the views
    private void initViews() {
        RecyclerView ticker = getActivity().findViewById(R.id.pricesTicker);
        ticker.setVisibility(View.GONE);

        ConstraintLayout supportConstraint = getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.GONE);


        recyclerView = v.findViewById(R.id.paymentRecycler);
        tvListNull = v.findViewById(R.id.tvPnull);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.GONE);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        back.setVisibility(View.VISIBLE);
    }


    //Getting the data from the database
    private void getDataFromDatabase() {
        DatabaseHelper db = new DatabaseHelper(getContext());
        carts = db.getAllCartData();
        db.close();

        if (carts.size() == 0) {
            tvListNull.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            initRecyclerView();
            tvListNull.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    //Calling api to get the list of the product when comming from the notification
    private void callApi() {

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.getProposalPayment("Bearer " +
                SharedPreference.getSimpleString(getContext(), Constants.accessToken), orderId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                carts.clear();
                Common.dissmissDialog();
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());

                        JSONObject data = jsonObject.getJSONObject("data");

                        CartModel cartModel = new CartModel(0, 0, 0, AppUtils.convertStringToFloat(data.getString("quantity")),
                                data.getString("product_price"), data.getString("product_name"),
                                "", "", "", 0.0, data.getString("unit")
                                , "", "", "", "", data.getString("delivery_type"), data.getString("company_name")
                                , data.getString("product_variety"));

                        total = data.getString("total");

                        carts.add(cartModel);
                        initRecyclerView();
                        paymentRecyclerAdapter.notifyDataSetChanged();

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Tag", "onFailure: ." + t.getMessage());
            }
        });

    }

    public void showNullMessage() {
        tvListNull.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        paymentRecyclerAdapter = new PaymentRecyclerAdapter(getContext(), carts, orderId, fromOrderDetails, total);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(paymentRecyclerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {
        if (tag.equals(update_product)) {
            updateTheProductPrice(jsonObject);
        }
    }

    @Override
    public void onFailure(String message, String tag) {
        Log.d(ContentValues.TAG, "onFailure: " + tag);
    }

    private void updateTheProductPrice(JSONObject jsonObject) {
        try {
            String merchantId = jsonObject.getString("merchant_id");
            String productId = jsonObject.getString("product_id");
            String price = jsonObject.getString("price");
            String minPrice = jsonObject.getString("minprice");

            getActivity().runOnUiThread(() -> {

                ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                        (price), (minPrice));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
