package com.vic.vicwsp.Views.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Helpers.NotificationAdapter;
import com.vic.vicwsp.Controllers.Helpers.PricesTickerRecyclerAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Models.Response.NegoUpdated.Data;
import com.vic.vicwsp.Models.Response.Notifications.Datum;
import com.vic.vicwsp.Models.Response.Notifications.NotificationModel;
import com.vic.vicwsp.Models.TickerModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.ApplicationClass;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.InternetBCR;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.Complaints.ComplaintChat;
import com.vic.vicwsp.Views.Activities.Support.SupportChat;
import com.vic.vicwsp.Views.Dialog.BottomSheetSCDialog;
import com.vic.vicwsp.Views.Fragments.Cart;
import com.vic.vicwsp.Views.Fragments.Catalogue;
import com.vic.vicwsp.Views.Fragments.MyProducts;
import com.vic.vicwsp.Views.Fragments.Negotiation;
import com.vic.vicwsp.Views.Fragments.Orders;
import com.vic.vicwsp.Views.Fragments.OrdersTrackingList;
import com.vic.vicwsp.Views.Fragments.Research;
import com.vic.vicwsp.Views.Fragments.Settings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicwsp.Utils.Common.getTotal;
import static com.vic.vicwsp.Utils.Common.hideKeyboard;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Utils.Constants.currency;
import static com.vic.vicwsp.Utils.SharedPreference.getSimpleString;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;
    public TextView tvToolbarCartText;
    private ImageView ivToolbarCart, back, headerLogo;
    private Fragment fragment;
    private ConstraintLayout cartConstraintLayout;
    public static String shippingAddress;
    public static LatLng shippingLatlng;
    public static String shippingDeliveryCharges = "0.00";
    public static String deliveryType;
    public static String deliveryPickupDate;
    public static double purchaseAmount = 0;
    public static ApplicationClass sockets;
    private RecyclerView pricesTicker;
    private PricesTickerRecyclerAdapter pricesTickerRecyclerAdapter;
    private LinearLayoutManager horizontalLayoutManager;
    public ArrayList<TickerModel> tickerModelArrayList = new ArrayList<>();
    private Handler handler = new Handler();
    private Dialog proposalDialog;
    private boolean isDialogOn = false;
    private ArrayList<CartModel> cartModelArrayList = new ArrayList<>();
    private BroadcastReceiver internetBCR;
    private Data negoData;
    private TextView cartBadge;

    private ConstraintLayout notificationBackground, notificationLayout;
    private ImageView ivAr, ivNotifications;
    private TextView notificationBadge;
    private RecyclerView notificationRecyclerView;
    private ImageView notificationNullImage, closeNotificationPanel;
    private TextView notificationNullText;
    private NotificationModel notificationModel;
    private ArrayList<Datum> notificationDatum = new ArrayList<>();
    private NotificationAdapter notificationAdapter;
    private boolean isNotificationLoading = false;
    private int NOTIFICATION_CURRENT_PAGE = 0;
    private int NOTIFICATION_LAST_PAGE = -1;
    private SwipeRefreshLayout notificationSwipeToRefresh;
    private Button notificationClearAll;
    public int notificationCount = 0;

    public BroadcastReceiver moveToNego = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("MoveToNego")) {

                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {

                        negoData = bundle.getParcelable("NegoData");

                        Bundle bundle1 = new Bundle();
                        bundle1.putBoolean("fromBuy", true);
                        bundle1.putBoolean("fromOrderDetails", false);
                        bundle1.putParcelable("NegoData", negoData);

                        if (negoData != null) {
                            Negotiation negotiation = new Negotiation();
                            negotiation.setArguments(bundle1);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.mainFragment, negotiation, "NegotiationList")
                                    .addToBackStack("NegotiationList")
                                    .commitAllowingStateLoss();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public BroadcastReceiver updateCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("UpdateCart")) {

                    double amount = AppUtils.convertStringToDouble(intent.getStringExtra("amount"));
                    String unit = intent.getStringExtra("unit");
                    boolean showToast = intent.getBooleanExtra("showToast", false);

                    if (showToast && amount > 0) {
                        String msg = getResources().getString(R.string.added_to_cart, amount + unit);
                        showToast(MainActivity.this, msg, true);

                        Log.d("asd", "onReceive: " + "Receiver is called");

                    }


                    DatabaseHelper databaseHelper = new DatabaseHelper(context);
                    ArrayList<CartModel> cartModelArrayList = databaseHelper.getAllCartData();
                    updateCart(String.valueOf(getTotal(cartModelArrayList)));
                    databaseHelper.close();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    };

    public static boolean radiationStatus = false;
    public BroadcastReceiver updateNotificationBadge = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("UpdateNotificationBadge")) {
                    int c = intent.getIntExtra("count", 0);

                    setNotificationBadge(c, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public int mainUserId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.isAppRunning = true;
        internetBCR = new InternetBCR();
        mainUserId = Integer.parseInt(SharedPreference.getSimpleString(this, Constants.userId));
        sockets = (ApplicationClass) getApplication();
        sockets.initializeSocket(mainUserId);

        Log.d("BeforeSend", "onCreate: " + sockets.getSocket().connected());

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(moveToNego, new IntentFilter("MoveToNego"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(updateCart, new IntentFilter("UpdateCart"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(updateNotificationBadge, new IntentFilter("UpdateNotificationBadge"));

        initViews();
        getSettingsApi();
        @SuppressLint("HardwareIds")
        String androidID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        SharedPreference.saveSimpleString(MainActivity.this, Constants.deviceId, androidID);
        getToken();

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        cartModelArrayList = databaseHelper.getAllCartData();

        if (cartModelArrayList.size() > 0)
            updateCart(String.valueOf(getTotal(cartModelArrayList)));
        else
            updateCart("0.00");

        databaseHelper.close();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent.hasExtra("type") && intent.hasExtra("orderId")) {
            boolean isComingFromNotification = intent.getBooleanExtra("isComingFromNotification", false);
            if (isComingFromNotification) {
                String type = intent.getStringExtra("type");


                int orderId = 0;

                if (intent.hasExtra("orderId")) {
                    if (intent.getExtras().get("orderId") instanceof Integer) {
                        orderId = intent.getIntExtra("orderId", 0);
                    } else if (intent.getExtras().get("orderId") instanceof String) {
                        if (AppUtils.isSet(intent.getStringExtra("orderId")))
                            orderId = Integer.parseInt(intent.getStringExtra("orderId"));
                    }
                }

                String messageData = intent.getStringExtra("message");
                String quantity = intent.getStringExtra("quantity");
                String unit = intent.getStringExtra("unit");
                String price = intent.getStringExtra("price");
                String body = intent.getStringExtra("body");

                checkForNotification(type, orderId, messageData, quantity, unit, price, body);
            }
        }

    }

    //Initializing the views
    private void initViews() {
        pricesTicker = findViewById(R.id.pricesTicker);
        initLayoutManager();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        cartBadge = findViewById(R.id.cartBadge);

        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(view -> {
            hideKeyboard(MainActivity.this);
            getSupportFragmentManager().popBackStack();
        });


        notificationRecyclerView = findViewById(R.id.notificationRecycler);
        notificationBackground = findViewById(R.id.notificationBackground);
        notificationBackground.setOnClickListener(view -> {
            notificationBackground.setVisibility(View.GONE);
            ivAr.setVisibility(View.GONE);
            notificationLayout.setVisibility(View.GONE);
        });
        notificationLayout = findViewById(R.id.notificationLayout);
        ivAr = findViewById(R.id.ivAr);
        notificationBadge = findViewById(R.id.notificationBadge);
        ivNotifications = findViewById(R.id.ivNotifications);
        notificationNullImage = findViewById(R.id.ivListNull);
        closeNotificationPanel = findViewById(R.id.closeNotificationPanel);
        notificationNullText = findViewById(R.id.tvListNull);
        notificationSwipeToRefresh = findViewById(R.id.notificationSwipeToRefresh);
        notificationClearAll = findViewById(R.id.notificationClearAll);

        notificationClearAll.setOnClickListener(v -> {
            clearAllNotifications();
        });

        closeNotificationPanel.setOnClickListener(v -> {
            if (notificationBackground.getVisibility() == View.VISIBLE) {
                notificationBackground.setVisibility(View.GONE);
                ivAr.setVisibility(View.GONE);
                notificationLayout.setVisibility(View.GONE);
            }
        });

        notificationSwipeToRefresh.setOnRefreshListener(() -> {
            getNotificationFromApi(true);
        });

        ivNotifications.setOnClickListener(view -> {
            handlingNotification();
        });


        headerLogo = findViewById(R.id.headerLogo);
        headerLogo.setOnClickListener(view -> {
            comeToHome();
        });


        cartConstraintLayout = findViewById(R.id.cartConstraintLayout);
        cartConstraintLayout.setOnClickListener(view -> {
            hideKeyboard(MainActivity.this);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, new Cart(), "Cart")
                    .addToBackStack("Cart").commit();
        });

        tvToolbarCartText = findViewById(R.id.tvToolbarCartText);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);
        fragment = new Catalogue();
        addFragment(fragment, "Catalogue");

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("type") && intent.hasExtra("orderId")) {
                boolean isComingFromNotification = intent.getBooleanExtra("isComingFromNotification", false);

                SharedPreference.saveBoolean(this, "FromNoti", isComingFromNotification);

                if (isComingFromNotification) {
                    String type = intent.getStringExtra("type");
                    int orderId = intent.getIntExtra("orderId", 0);
                    String messageData = intent.getStringExtra("message");
                    String quantity = intent.getStringExtra("quantity");
                    String unit = intent.getStringExtra("unit");
                    String price = intent.getStringExtra("price");
                    String body = intent.getStringExtra("body");

                    checkForNotification(type, orderId, messageData, quantity, unit, price, body);
                }
            }
        }
    }


    public void comeToHome() {

        try {
            hideKeyboard(MainActivity.this);

            MainActivity.this.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Menu menu = bottomNavigationView.getMenu();
            MenuItem item = menu.getItem(0);
            item.setChecked(true);
            if (!(fragment instanceof Catalogue)) {
                fragment = new Catalogue();
                addFragment(fragment, "Catalogue");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Getting token from firebase

    private void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Log.d(TAG, "getToken: " + "Firebase is working");

                        String token = task.getResult().getToken();
                        SharedPreference.saveSimpleString(MainActivity.this, Constants.fcmToken, token);
                        Common.fcmDevices(MainActivity.this,
                                SharedPreference.getSimpleString(MainActivity.this, Constants.deviceId),
                                SharedPreference.getSimpleString(MainActivity.this, Constants.fcmToken));

                    }

                });

    }
    //Bottom navigation view on the main screen

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {

        if (notificationBackground.getVisibility() == View.VISIBLE) {
            notificationBackground.setVisibility(View.GONE);
            ivAr.setVisibility(View.GONE);
            notificationLayout.setVisibility(View.GONE);
        }


        switch (item.getItemId()) {
            case R.id.catalog:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                item.setChecked(true);
                if (!(fragment instanceof Catalogue)) {
                    System.gc();
                    fragment = new Catalogue();
                    addFragment(fragment, "Catalogue");
                }
                break;
            case R.id.research:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                System.gc();
                item.setChecked(true);
                if (!(fragment instanceof Research)) {
                    fragment = new Research();
                    addFragment(fragment, "Research");
                }
                break;
            case R.id.orders:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                System.gc();
                item.setChecked(true);
                if (!(fragment instanceof Orders)) {
                    fragment = new Orders();
                    addFragment(fragment, "Orders");
                }
                break;
            case R.id.myProducts:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                System.gc();
                item.setChecked(true);
                if (!(fragment instanceof MyProducts)) {
                    fragment = new MyProducts();
                    addFragment(fragment, "MyProducts");
                }
                break;
            case R.id.settings:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                System.gc();
                item.setChecked(true);
                if (!(fragment instanceof Settings)) {
                    fragment = new Settings();
                    addFragment(fragment, "Settings");
                }
                break;
            default:
                break;
        }
        return false;
    };

    private void addFragment(Fragment fragment, String key) {
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, fragment, key).commit();
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments >= 1) {
            getSupportFragmentManager().popBackStack();
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        } else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(MainActivity.this, getResources().getString(R.string.back_pressed_again), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void updateCart(String value) {

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        ArrayList<CartModel> cartModels = databaseHelper.getAllCartData();

        if (cartModels.size() == 0) {
            cartBadge.setVisibility(View.GONE);
        } else if (cartModels.size() >= 100) {
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText("99+");
        } else {
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(String.valueOf(cartModels.size()));
        }
        databaseHelper.close();

        this.tvToolbarCartText.setText(Common.convertInKFormat((value)) + SharedPreference.getSimpleString(MainActivity.this, Constants.currency));
    }

    //Setting api to get the global values in the apps

    private void getSettingsApi() {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> getSettings = api.getSettings("Bearer " +
                getSimpleString(MainActivity.this, Constants.accessToken));

        getSettings.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());

                        JSONArray data = new JSONArray();
                        data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = new JSONObject();
                            object = data.getJSONObject(i);
                            if (object.getString("key").equals("currency")) {
                                SharedPreference.saveSimpleString(MainActivity.this,
                                        currency, object.getString("value"));
                            } else if (object.getString("key").equals("vat")) {
                                SharedPreference.saveSimpleString(MainActivity.this,
                                        Constants.vat, object.getString("value"));
                            } else if (object.getString("key").equals("company_phone")) {
                                SharedPreference.saveSimpleString(MainActivity.this,
                                        Constants.companyPhone, object.getString("value"));
                            }
                        }
                        setNotificationBadge(jsonObject.getInt("notifications"), 0);
                        radiationStatus = jsonObject.getBoolean("radiation_status");

                        if (cartModelArrayList.size() > 0)
                            updateCart(String.valueOf(getTotal(cartModelArrayList)));
                        else
                            updateCart("0.00");

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(MainActivity.this, jsonObject.getString("message"), false);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Settings Api", "onFailure: " + t.getMessage());
            }
        });

    }

    private void checkForNotification(String type, int orderId, String messageData, String quantity, String unit, String price, String body) {

        String value = SharedPreference.getSimpleString(getApplicationContext(), Constants.isLoggedIn);

        if (value.equals("false")) {
            Intent i = new Intent(MainActivity.this, SignIn.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        } else {

            if (type.equals(Constants.ProposalReceived)) {
                showProposalDialog(MainActivity.this, orderId, unit, quantity, price, body);
            } else if (type.equals(Constants.ProposalAccepted)) {
                goToOrdersFromNoti(type, orderId);
            } else if (type.equals(Constants.ProposalAcceptedPreOrder)) {
                goToOrders();
            } else if (type.equals(Constants.ProposalRejected)) {
                goToOrdersFromNoti(type, orderId);
            } else if (type.equals(Constants.OrderExpired)) {
                goToOrdersFromNoti(type, orderId);
            } else if (type.equals(Constants.OrderAcceptedByDriver)) {
                goToTrackOrderScreen(orderId);
            } else if (type.equals(Constants.OrderCompletedByDriver)) {
                goToTrackOrderScreen(orderId);
            } else if (type.equals(Constants.DriverPickedUpProducts)) {
                goToTrackOrderScreen(orderId);
            } else if (type.equals(Constants.DriverReachedAtLocation)) {
                goToTrackOrderScreen(orderId);
            } else if (type.equals(Constants.DriverPickedUpProductsSeller)) {
                goToOrders();
            } else if (type.equals(Constants.MessageReceived)) {
                sendToChat(messageData, String.valueOf(orderId));
            } else if (type.equals(Constants.MessageReceivedFromDriverToSeller)) {
                sendToDriverSeller(messageData, String.valueOf(orderId));
            } else if (type.equals(Constants.MessageReceivedFromDriverToBuyer)) {
                sendToBuyerDriver(messageData, String.valueOf(orderId));
            } else if (type.equals(Constants.AdminCommentOnSupport)) {
                goToSupportChatScreen(orderId);
            } else if (type.equals(Constants.AdminCommentOnComplaint)) {
                goToComplaintChatScreen(orderId);
            }
            System.gc();
        }

    }

    private void sendToChat(String messageData, String orderNumber) {
        try {
            JSONObject jsonObject = new JSONObject(messageData);
//            int orderId = jsonObject.getInt("order_id");
            Intent intent = new Intent(MainActivity.this, NegoChat.class);
            intent.putExtra("orderId", Integer.parseInt(orderNumber));
            intent.putExtra("userName", jsonObject.getString("user_name"));
            intent.putExtra("orderNumber", orderNumber);

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToDriverSeller(String messageData, String orderNumber) {
        try {
            JSONObject jsonObject = new JSONObject(messageData);
            int orderId = jsonObject.getInt("order_id");
            Intent intent = new Intent(MainActivity.this, Seller2Driver.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("userName", jsonObject.getString("user_name"));
            intent.putExtra("orderNumber", orderNumber);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToBuyerDriver(String messageData, String orderNumber) {
        try {
            JSONObject jsonObject = new JSONObject(messageData);
            int orderId = jsonObject.getInt("order_id");
            Intent intent = new Intent(MainActivity.this, Buyer2Driver.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("userName", jsonObject.getString("user_name"));
            intent.putExtra("orderNumber", orderNumber);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToTrackOrderScreen(int orderId) {
//        SharedPreference.saveBoolean(this, "ComingFromNoti", true);

        Bundle bundle = new Bundle();
        bundle.putInt("orderId", orderId);
        OrdersTrackingList ordersTrackingList = new OrdersTrackingList();
        ordersTrackingList.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, ordersTrackingList, "").addToBackStack("").commit();

    }

    public void goToOrdersFromNoti(String type, int orderId) {

        System.gc();

        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.getItem(2);
        item.setChecked(true);
        fragment = new Orders();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putInt("orderId", orderId);

        fragment.setArguments(bundle);
        addFragment(fragment, "Orders");


    }

    //Function to go tto the orders screen

    public void goToOrders() {

        System.gc();

        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.getItem(2);
        item.setChecked(true);
        fragment = new Orders();
        addFragment(fragment, "Orders");
    }


    //Function to go to Complaint chat screen

    public void goToComplaintChatScreen(int complaintId) {

        System.gc();
        Intent i = new Intent(this, ComplaintChat.class);
        i.putExtra("complaintId", String.valueOf(complaintId));
        startActivity(i);
    }

    //Function to go to Complaint chat screen

    public void goToSupportChatScreen(int supportId) {

        System.gc();
        Intent i = new Intent(this, SupportChat.class);
        i.putExtra("supportId", String.valueOf(supportId));
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sockets = (ApplicationClass) getApplication();
        sockets.getSocket().connect();

        if (SharedPreference.getSimpleString(MainActivity.this, Constants.afterPayementSendToOrders).equals(Constants.afterPayementSendToOrders)) {
            SharedPreference.saveSimpleString(MainActivity.this, Constants.afterPayementSendToOrders, "");
            goToOrders();
        }

        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void showProposalDialog(Context context, int orderId, String unit, String quantity, String price, String body)
            throws WindowManager.BadTokenException {

        if (isDialogOn) {
            proposalDialog.dismiss();
            createDialog(context, orderId, unit, quantity, price, body);
        } else
            createDialog(context, orderId, unit, quantity, price, body);
    }

    private void createDialog(Context context, int orderId, String unit, String q, String p, String b) {
        proposalDialog = new Dialog(context);
        proposalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        proposalDialog.setCancelable(true);
        proposalDialog.setContentView(R.layout.dialog_nego_notification);

        TextView title = proposalDialog.findViewById(R.id.proposalDescription);
        TextView price = proposalDialog.findViewById(R.id.proposalPriceAnswer);
        TextView quantity = proposalDialog.findViewById(R.id.proposalQuantityAnswer);
        ConstraintLayout cancelAndGoToOrders = proposalDialog.findViewById(R.id.cancelAndGoToOrders);

        title.setText(b);
        price.setText(Common.round(AppUtils.convertStringToDouble(p), 2) + " " + SharedPreference.getSimpleString(context, currency));
        quantity.setText(q + " " + unit);

        ConstraintLayout accept, reject;

        accept = proposalDialog.findViewById(R.id.btnAcceptDialog);
        reject = proposalDialog.findViewById(R.id.btnRejectDialog);
        accept.setOnClickListener(view -> {
            proposalDialog.dismiss();
            isDialogOn = false;
            acceptProposal(orderId);

        });
        reject.setOnClickListener(view -> {
            proposalDialog.dismiss();
            isDialogOn = false;
            rejectProposal(orderId);
        });

        proposalDialog.show();
        isDialogOn = true;
        cancelAndGoToOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proposalDialog.dismiss();
                goToOrders();
            }
        });

    }


    private void rejectProposal(int orderId) {

        Common.showDialog(MainActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.rejectProposal("Bearer " +
                        SharedPreference.getSimpleString(MainActivity.this, Constants.accessToken)
                , orderId);

        Log.d(TAG, "rejectProposal: " + orderId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast(MainActivity.this, jsonObject.getString("message"), true);
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(MainActivity.this, jsonObject.getString("message"), false);
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

    private void acceptProposal(int orderId) {

        Common.showDialog(MainActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.acceptProposal("Bearer " +
                SharedPreference.getSimpleString(MainActivity.this, Constants.accessToken), orderId, 1);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();

                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast(MainActivity.this, jsonObject.getString("message"), true);
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(MainActivity.this, jsonObject.getString("message"), false);
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

    private void initLayoutManager() {
        horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(MainActivity.this) {
                    private static final float SPEED = 4000f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return 5.0f;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };

        pricesTickerRecyclerAdapter = new PricesTickerRecyclerAdapter(MainActivity.this, tickerModelArrayList) {
            @Override
            public void load() {
                if (horizontalLayoutManager.findFirstVisibleItemPosition() > 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pricesTicker.scrollToPosition(0);
                        }
                    });
                }
            }
        };

        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        pricesTicker.setLayoutManager(horizontalLayoutManager);
        pricesTicker.setHasFixedSize(true);
        pricesTicker.setDrawingCacheEnabled(true);
        pricesTicker.setItemViewCacheSize(10);
        pricesTicker.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        pricesTicker.setAdapter(pricesTickerRecyclerAdapter);
        autoScroll();

    }

    public void autoScroll() {
        final int speedScroll = 1200;
        final Runnable runnable = new Runnable() {
            int scrollCount = 0;

            @Override
            public void run() {
                if (horizontalLayoutManager.findLastCompletelyVisibleItemPosition() == tickerModelArrayList.size() - 1) {
                    pricesTickerRecyclerAdapter.load();
                }
                pricesTicker.smoothScrollToPosition(scrollCount++);
                handler.postDelayed(this, speedScroll);
            }
        };
        handler.postDelayed(runnable, speedScroll);
    }

    public void initializeTicker(JSONObject jsonObject) {
        try {
            tickerModelArrayList.clear();

            JSONArray jsonArray = jsonObject.getJSONArray("array");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                tickerModelArrayList.add(new TickerModel(jsonObject1.getInt("product_id"), jsonObject1.getString("product_name"), jsonObject1.getString("product_name_fr"),
                        jsonObject1.getDouble("minprice"), jsonObject1.getString("status")));
            }
            runOnUiThread(() -> pricesTickerRecyclerAdapter.notifyDataSetChanged());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void updateTicker(int productId, int merchantId, String priceStr, String minPriceStr) {

        Double price = AppUtils.convertStringToDouble(priceStr), minPrice = AppUtils.convertStringToDouble(minPriceStr);

        for (int i = 0; i < tickerModelArrayList.size(); i++) {
            if (tickerModelArrayList.get(i).getProductId() == productId) {
                if (tickerModelArrayList.get(i).getMinprice() > minPrice) {

                    int finalI1 = i;

                    runOnUiThread(() -> {
                        tickerModelArrayList.get(finalI1).setStatus("UP");
                        tickerModelArrayList.get(finalI1).setMinprice(minPrice);
                        pricesTickerRecyclerAdapter.notifyItemChanged(finalI1);
                    });
                } else if (tickerModelArrayList.get(i).getMinprice() < minPrice) {

                    int finalI = i;
                    runOnUiThread(() -> {
                        try {
                            tickerModelArrayList.get(finalI).setStatus("DOWN");
                            tickerModelArrayList.get(finalI).setMinprice(minPrice);
                            pricesTickerRecyclerAdapter.notifyItemChanged(finalI);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }

    }


    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void handlingNotification() {
        NOTIFICATION_CURRENT_PAGE = 0;
        NOTIFICATION_LAST_PAGE = -1;
        notificationDatum.clear();

        notificationBackground.setVisibility(View.VISIBLE);
        ivAr.setVisibility(View.VISIBLE);
        notificationLayout.setVisibility(View.VISIBLE);
        initScrollListener();

        notificationAdapter = new NotificationAdapter(MainActivity.this, notificationDatum, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        notificationRecyclerView.setLayoutManager(mLayoutManager);
        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        notificationRecyclerView.setAdapter(notificationAdapter);

        Common.showDialog(MainActivity.this);
        getNotificationFromApi(false);
    }

    //Initializing the scroll view for pagination
    private void initScrollListener() {
        notificationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isNotificationLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == notificationDatum.size() - 1) {
                        recyclerView.post(() -> loadMore());
                        isNotificationLoading = true;
                    }
                }
            }
        });
    }


    private void loadMore() {
        try {
            notificationDatum.add(null);
            notificationAdapter.notifyItemInserted(notificationDatum.size() - 1);

            if (NOTIFICATION_CURRENT_PAGE != NOTIFICATION_LAST_PAGE) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getNotificationFromApi(false);
                    }
                }, 1500);
            } else {
                notificationRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        notificationDatum.remove(notificationDatum.size() - 1);
                        notificationAdapter.notifyItemRemoved(notificationDatum.size());
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNotificationFromApi(boolean isRefresh) {

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<NotificationModel> call = null;
        if (isRefresh)
            call = api.getNotificationsFromApi("Bearer " +
                    SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken), 0);
        else
            call = api.getNotificationsFromApi("Bearer " +
                    SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken), NOTIFICATION_CURRENT_PAGE + 1);

        call.enqueue(new Callback<NotificationModel>() {
            @Override
            public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {
                try {
                    Common.dissmissDialog();

                    if (isRefresh) {
                        notificationSwipeToRefresh.setRefreshing(false);
                        notificationDatum.clear();
                    }

                    if (response.isSuccessful()) {

                        NOTIFICATION_LAST_PAGE = response.body().getMeta().getLastPage();
                        NOTIFICATION_CURRENT_PAGE = response.body().getMeta().getCurrentPage();

                        if (notificationDatum.size() > 0) {
                            notificationDatum.remove(notificationDatum.size() - 1);
                            notificationAdapter.notifyItemRemoved(notificationDatum.size());
                        }

                        notificationModel = new NotificationModel(response.body().getData(), response.body().getLinks(), response.body().getMeta(),
                                response.body().getStatus(), response.body().getMessage());

                        notificationDatum.addAll(notificationModel.getData());
                        notificationCount = 0;
                        setNotificationBadge(notificationModel.getMeta().getTotal(), 0);

                        if (notificationDatum.size() == 0) {
                            notificationNullImage.setVisibility(View.VISIBLE);
                            notificationNullText.setVisibility(View.VISIBLE);
                            notificationRecyclerView.setVisibility(View.GONE);
                            notificationClearAll.setVisibility(View.GONE);
                        } else {
                            notificationNullImage.setVisibility(View.GONE);
                            notificationNullText.setVisibility(View.GONE);
                            notificationRecyclerView.setVisibility(View.VISIBLE);
                            notificationClearAll.setVisibility(View.VISIBLE);
                        }

                        notificationAdapter.notifyDataSetChanged();
                        isNotificationLoading = false;

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(MainActivity.this, jsonObject.getString("message"), false);
                        notificationNullImage.setVisibility(View.VISIBLE);
                        notificationNullText.setVisibility(View.VISIBLE);
                        notificationRecyclerView.setVisibility(View.GONE);
                        notificationClearAll.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<NotificationModel> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
                notificationNullImage.setVisibility(View.VISIBLE);
                notificationNullText.setVisibility(View.VISIBLE);
                notificationRecyclerView.setVisibility(View.GONE);
                notificationClearAll.setVisibility(View.GONE);
            }
        });
    }

    private void clearAllNotifications() {
        Common.showDialog(MainActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.clearAllNotifications("Bearer " +
                SharedPreference.getSimpleString(MainActivity.this, Constants.accessToken));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Common.dissmissDialog();
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast(MainActivity.this, jsonObject.getString("message"), true);

                        notificationDatum.clear();
                        notificationAdapter.notifyDataSetChanged();
                        notificationNullImage.setVisibility(View.VISIBLE);
                        notificationNullText.setVisibility(View.VISIBLE);
                        notificationRecyclerView.setVisibility(View.GONE);
                        notificationClearAll.setVisibility(View.GONE);
                        notificationCount = 0;
                        notificationBadge.setVisibility(View.GONE);

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(MainActivity.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("", "onFailure: " + t.getMessage());
            }
        });
    }

    public void hideNotificationBar() {

        notificationBackground.setVisibility(View.GONE);
        ivAr.setVisibility(View.GONE);
        notificationLayout.setVisibility(View.GONE);
    }

    public void setNotificationBadge(int count, int check) {

        if (check == 0)       // 0 to add count -- 1 to subtract count
            notificationCount += count;
        else if (check == 1)
            notificationCount -= count;

        if (notificationCount <= 0) {
            notificationBadge.setVisibility(View.GONE);
            notificationCount = 0;
        } else {

            try {
                Settings settings = (Settings) getSupportFragmentManager().findFragmentByTag("Settings");
                if (settings != null && settings.isVisible()) {
                    notificationBadge.setVisibility(View.GONE);
                } else {
                    notificationBadge.setVisibility(View.VISIBLE);
                    if (notificationCount > 99)
                        notificationBadge.setText("99+");
                    else
                        notificationBadge.setText(String.valueOf(notificationCount));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showingNullMessage() {
        notificationNullText.setVisibility(View.VISIBLE);
        notificationNullImage.setVisibility(View.VISIBLE);
        notificationRecyclerView.setVisibility(View.GONE);
    }

    public void showSupportDialog() {

        BottomSheetSCDialog bottomSheetDialog = new BottomSheetSCDialog();
        bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());

    }


}
