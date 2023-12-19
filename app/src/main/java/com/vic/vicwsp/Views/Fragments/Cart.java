package com.vic.vicwsp.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.CartRecyclerAdapter;
import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.ShowNullMessage;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.Controllers.Interfaces.UpdateDeliveryCharges;
import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingAddress;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingDeliveryCharges;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingLatlng;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

public class Cart extends Fragment implements ShowNullMessage, UpdateDeliveryCharges, SocketCallback {


    private CartRecyclerAdapter cartRecyclerAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private View v;
    ArrayList<CartModel> carts = new ArrayList<>();
    private TextView tvListNull;
    private ImageView ivListNull;
    private ImageView ivToolbarCart;
    private TextView tvToolbarCart;
    private ImageView back;
    private ConstraintLayout cartConstraint, cartConstraintLayoutToolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_cart, container, false);
        shippingAddress="";
        shippingDeliveryCharges="0.00";
        shippingLatlng=new LatLng(0.0,0.0);
        try {
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sockets.initializeCallback(this);
        return v;
    }

    //Initializing the views and taking decisions
    private void initViews() throws Exception {

        RecyclerView ticker = getActivity().findViewById(R.id.pricesTicker);
        ticker.setVisibility(View.GONE);

        ConstraintLayout supportConstraint= getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.GONE);


        recyclerView = v.findViewById(R.id.cartRecycler);
        cartConstraintLayoutToolbar = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraintLayoutToolbar.setVisibility(View.GONE);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        back.setVisibility(View.VISIBLE);
        tvListNull = v.findViewById(R.id.tvListNull);
        ivListNull = v.findViewById(R.id.ivListNull);
        cartConstraint = v.findViewById(R.id.cartConstraintEmpty);

        DatabaseHelper db = new DatabaseHelper(getContext());
        carts = db.getAllCartData();
        db.close();

        if (carts.size() > 0)
            if (!shippingAddress.equals("")) {
                String merchantKeys = getMerchants();
                updateDeliveryCharges(getContext(), merchantKeys, String.valueOf(shippingLatlng.latitude),
                        String.valueOf(shippingLatlng.longitude));
            }

        if (carts.size() == 0) {
            tvListNull.setVisibility(View.VISIBLE);
            ivListNull.setVisibility(View.VISIBLE);
            cartConstraint.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            initRecyclerView();
            tvListNull.setVisibility(View.GONE);
            ivListNull.setVisibility(View.GONE);
            cartConstraint.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    //Showing null message interface implementation
    public void showNullMessage() {
        try {
            tvListNull.setVisibility(View.VISIBLE);
            ivListNull.setVisibility(View.VISIBLE);
            cartConstraint.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Initialize the recycler view
    private void initRecyclerView() {
        try {
            cartRecyclerAdapter = new CartRecyclerAdapter(getContext(), carts, this);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cartRecyclerAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMerchants() {
        try {
            ArrayList<String> merchants = new ArrayList<>();
            merchants.clear();

            for (int i = 0; i < carts.size(); i++) {
                merchants.add(String.valueOf(carts.get(i).getMarchant_id()));
            }

            Set<String> set = new HashSet<>(merchants);
            merchants.clear();
            merchants.addAll(set);

            return android.text.TextUtils.join(",", merchants);
        } catch (Exception w) {
            w.printStackTrace();
        }

        return "";
    }

    // Update delivery charges interface implementation
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
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("status")) {
                            JSONObject data = new JSONObject();
                            data = jsonObject.getJSONObject("data");
                            shippingDeliveryCharges = data.getString("delivery_charges");
                        }

                        Common.dissmissDialog();
                        cartRecyclerAdapter.notifyDataSetChanged();

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        Log.d("Carts Delivery", "onResponse: " + jsonObject.getString("message"));
                    }

                    cartRecyclerAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Carts Delivery", "onFailure: " + t.getMessage());
            }
        });

    }

    public void notifyAdapter(int position) {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    cartRecyclerAdapter.notifyDataSetChanged();
                } else {
                    notifyAdapter(position);
                }
            }
        });
    }

    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {
        if (tag.equals(update_product)) {
            updateTheProductPrice(jsonObject);
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
            String price = jsonObject.getString("price");
            String minPrice = jsonObject.getString("minprice");

            getActivity().runOnUiThread(() -> {

                ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                        price, minPrice);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
