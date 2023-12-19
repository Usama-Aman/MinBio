package com.vic.vicwsp.Views.Fragments;

import android.os.Bundle;
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

import com.vic.vicwsp.Controllers.Helpers.OrderDetailRecyclerAdapter;
import com.vic.vicwsp.Models.Response.OrdersHistory.Datum;
import com.vic.vicwsp.R;

public class OrderDetail extends Fragment {

    private View v;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private OrderDetailRecyclerAdapter orderDetailRecyclerAdapter;
    private Datum datum;
    private ImageView logout, cart, back;
    private TextView cartText;
    private ConstraintLayout cartConstraint;
    private boolean fromReceived = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_order_details, container, false);
        initViews();
        initRecyclerView();
        return v;
    }

    //Initializing the views
    private void initViews() {
        recyclerView = v.findViewById(R.id.orderDetailRecycler);
        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);

        ConstraintLayout supportConstraint= getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.GONE);

    }

    //Initializing the adapter for recycler view
    private void initRecyclerView() {
        datum = getArguments().getParcelable("OrderHistoryDetail");
        fromReceived = getArguments().getBoolean("fromReceived", false);
        orderDetailRecyclerAdapter = new OrderDetailRecyclerAdapter(getContext(), datum, fromReceived);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(orderDetailRecyclerAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
