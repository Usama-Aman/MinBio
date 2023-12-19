package com.vic.vicwsp.Views.Fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dueeeke.tablayout.SegmentTabLayout;
import com.dueeeke.tablayout.listener.OnTabSelectListener;
import com.vic.vicwsp.Controllers.Helpers.MyPagerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Views.Activities.MainActivity;

import org.json.JSONObject;

import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

public class Orders extends Fragment implements SocketCallback {

    private View v;
    private SegmentTabLayout orderTabs;
    private ImageView logout, cart, back;
    private TextView cartText;
    private ConstraintLayout cartConstraint, supportConstraint;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_orders, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initViews();
        setupTabs();
        sockets.initializeCallback(this);

        checkingForNotifications();
        return v;
    }

    private void checkingForNotifications() {

        Bundle b = getArguments();

        if (b != null) {

            String type = b.getString("type");
            int orderId = b.getInt("orderId");

            if (type.equals(Constants.ProposalRejected)) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromBuy", false);
                bundle.putBoolean("fromOrderDetails", true);
                bundle.putInt("orderId", orderId);

                Negotiation negotiation = new Negotiation();
                negotiation.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, negotiation, "Negotiation")
                        .addToBackStack("Negotiation")
                        .commit();

            }

            if (type.equals(Constants.ProposalAccepted)) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("fromOrderDetails", true);
                bundle.putInt("orderId", orderId);
                Payment payment = new Payment();
                payment.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, payment, "Payment")
                        .addToBackStack("Payment")
                        .commit();

            }
            if (type.equals(Constants.ProposalReceived)) {

                Bundle bundle = new Bundle();
                bundle.putInt("orderId", orderId);
                ReceivedNego receivedNego = new ReceivedNego();
                receivedNego.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, receivedNego, "ReceivedNego")
                        .addToBackStack("ReceivedNego")
                        .commit();

            }
        }
        setArguments(null);
    }


    //Initializing the views
    private void initViews() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.GONE);

        supportConstraint = getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.VISIBLE);
        supportConstraint.setOnClickListener(v -> {
            ((MainActivity) getContext()).showSupportDialog();
        });

        ConstraintLayout changeLanguageLayout = getActivity().findViewById(R.id.changeLanguageLayout);
        changeLanguageLayout.setVisibility(View.GONE);

        orderTabs = v.findViewById(R.id.orderTabs);
        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        back.setVisibility(View.GONE);

        TextView notificationBadge = getActivity().findViewById(R.id.notificationBadge);
        notificationBadge.setVisibility(View.VISIBLE);
        ImageView ivNotifications = getActivity().findViewById(R.id.ivNotifications);
        ivNotifications.setVisibility(View.VISIBLE);

        //Updating the notification badge
        int c = ((MainActivity) getContext()).notificationCount;
        ((MainActivity) getContext()).notificationCount = 0;
        ((MainActivity) getContext()).setNotificationBadge(c, 0);
    }

    //Initializing the tabs of orders screen
    private void setupTabs() {
        String[] titles = {getContext().getResources().getString(R.string.past),
                getContext().getResources().getString(R.string.received),
                getContext().getResources().getString(R.string.order_overdraft)};

        orderTabs.setTextUnselectColor(getContext().getResources().getColor(R.color.splashColor));
        orderTabs.setIndicatorColor(getContext().getResources().getColor(R.color.splashColor));
        orderTabs.setDividerColor(getContext().getResources().getColor(R.color.catalogueBackground));
        orderTabs.setIndicatorAnimEnable(true);
        orderTabs.setIndicatorBounceEnable(true);
        orderTabs.setTextAllCaps(true);
        orderTabs.setTabSpaceEqual(false);

        final ViewPager viewPager = v.findViewById(R.id.ordersViewpager);
        PagerAdapter pagerAdapter = new MyPagerAdapter(getChildFragmentManager(), titles);
        viewPager.setAdapter(pagerAdapter);
        orderTabs.setTabData(titles);
        orderTabs.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                orderTabs.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
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
            String subProductId = jsonObject.getString("subproduct_id");
            String price = jsonObject.getString("price");
            String minPrice = jsonObject.getString("minprice");
            String subprominprice = jsonObject.getString("subprominprice");

            ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                    (price), (minPrice));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }
}
