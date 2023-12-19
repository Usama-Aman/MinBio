package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.UpdateDeliveryCharges;
import com.vic.vicwsp.Models.Response.NegoUpdated.Data;
import com.vic.vicwsp.Models.Response.NegoUpdated.NegoUpdated;
import com.vic.vicwsp.Models.Response.NegoUpdated.NegotiationList;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.MapsActivity;
import com.vic.vicwsp.Views.Fragments.Negotiation;
import com.google.android.gms.maps.model.LatLng;

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

import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryPickupDate;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryType;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingAddress;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingDeliveryCharges;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingLatlng;
import static java.lang.String.valueOf;

public class NegotiationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ItemHeader itemHeader;
    private ItemFooter itemFooter;
    private Context context;
    Runnable runnable = null;
    private int fromOrderDetails;       //To check from which screen we are coming from which screen.
    // 0 WHEN COMING FROM THE PURCHASE SCREEN
    // 1 WHEN COMING FROM ORDER DETAILS
    // 2 UPDATE THE VIEW WHEN LOCATION IS SET
    private boolean negoHideCheck = false;

    private AlertDialog alertDialog;
    private int orderId;
    private Data negoData; //Negotiaion data
    private ArrayList<NegotiationList> negotiations;    //list of negotiations
    private UpdateDeliveryCharges updateDeliveryCharges; //Interface to update the delivery charges
    private String thresholdRange = "0.00";
    private Negotiation fragment;
    private boolean isFast = false, isTruck = false;
    Calendar todaysDate;

    //Receiver to update the delivery charges
    public void receiveBroadcastFromMap() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String merchantKeys = intent.getStringExtra("merchantKeys");
                if (!shippingAddress.equals("")) {

                    updateDeliveryCharges.updateDeliveryCharges(context, merchantKeys,
                            valueOf(shippingLatlng.latitude), valueOf(shippingLatlng.longitude));
                    if (fromOrderDetails == 0) {
                        itemFooter.tvNegoAddress.setVisibility(View.VISIBLE);
                        itemFooter.tvNegoAddress.setText(context.getResources().getString(R.string.cartAddressText) + " : " + shippingAddress);
                    }

                } else {
                    notifyDataSetChanged();
                }
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter("negoBroadCast"));
    }

    public NegotiationRecyclerAdapter(Context context, Data negoData
            , ArrayList<NegotiationList> negotiations
            , boolean fromOrderDetails, int orderId, Negotiation negotiation) {

        this.context = context;
        this.negoData = negoData;
        this.negotiations = negotiations;
        this.orderId = orderId;
        updateDeliveryCharges = negotiation;
        fragment = negotiation;
        receiveBroadcastFromMap();
        if (fromOrderDetails)
            this.fromOrderDetails = 1;
        else
            this.fromOrderDetails = 0;

        todaysDate = Calendar.getInstance();
        isFast = false;
        isTruck = false;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recieved_nego, parent, false);
            return new ItemHeader(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_negotiation, parent, false);
            return new ItemFooter(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        try {
            if (getItemViewType(position) == 0) {
                itemHeader = ((ItemHeader) holder);
                itemHeader.bind(position);
            } else if (getItemViewType(position) == 1) {
                itemFooter = ((ItemFooter) holder);
                itemFooter.bind(position);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return negotiations.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (negotiations.size() == 1)
            return position;
        else {
            if (position == negotiations.size())
                return 1;
            else
                return 0;
        }
    }


    public class ItemHeader extends RecyclerView.ViewHolder {

        TextView productName, quantity, pricePerKg, totalPrice;

        public ItemHeader(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tvPaymentProductName);
            quantity = itemView.findViewById(R.id.tvPaymentQuantity);
            pricePerKg = itemView.findViewById(R.id.tvPaymentPricePerKg);
            totalPrice = itemView.findViewById(R.id.tvPaymentTotalPrice);
        }

        @SuppressLint("SetTextI18n")
        public void bind(final int position) {
            productName.setText(negoData.getNegotiations().get(position).getProductName());
            quantity.setText(String.valueOf(negoData.getNegotiations().get(position).getQuantity()));

            if (fromOrderDetails == 0) {
                pricePerKg.setText(negoData.getNegotiations().get(position).getProductPrice() + SharedPreference.getSimpleString(context, Constants.currency));
            } else if (fromOrderDetails == 1) {
                if (position == negoData.getNegotiations().size() - 1)
                    pricePerKg.setText(context.getResources().getString(R.string.yourPrice) + "  " + negoData.getNegotiations().get(position).getProductPrice() + SharedPreference.getSimpleString(context, Constants.currency));
                else {
                    pricePerKg.setText(context.getResources().getString(R.string.proposalPrice) + "  " + Common.round(AppUtils.convertStringToDouble(negoData.getNegotiations().get(position).getTotal()) / negoData.getNegotiations().get(position).getQuantity(), 2)
                            + SharedPreference.getSimpleString(context, Constants.currency));
                }
            }

            totalPrice.setText(Common.round(AppUtils.convertStringToDouble(negoData.getNegotiations().get(position).getTotal()), 2) + SharedPreference.getSimpleString(context,
                    Constants.currency));


            switch (negoData.getNegotiations().get(position).getStatus()) {
                case "Accepted":
                    totalPrice.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    break;
                case "Rejected":
                case "Pending":
                    highlightPrices(position, totalPrice);
                    break;
                default:
                    break;
            }

            negoHideCheck = negoData.getNegotiations().get(0).getStatus().equals("Rejected");

        }
    }

    private void highlightPrices(int position, TextView totalPrice) {

//        if (position<negotiations.size()-1){
        if (negotiations.get(position).getIsMerchant() == 1) {
            totalPrice.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
        } else {
            totalPrice.setTextColor(context.getResources().getColor(R.color.red));
        }
//        }

    }

    public class ItemFooter extends RecyclerView.ViewHolder {

        private TextView btnAddAddress;
        private TextView tvNegoPriceSign, tvDeliveryCharges, tvDeliveryChargesAnswer, tvNegoTimerText;
        private EditText etNegoPrice;
        private ConstraintLayout negoLayoutFooter, btnAcceptOrder;
        private TextView orderCancelMessage, tvNegoDate, tvNegoAddress;
        private ImageView btnNego;
        private TextView btnCalendar;
        final long[] diff = {0};
        final long[] minutes = {0};
        final long[] seconds = {0};
        final long[] hours = {0};

        public ItemFooter(@NonNull View itemView) {
            super(itemView);
            tvNegoPriceSign = itemView.findViewById(R.id.tvNegoPriceSign);
            etNegoPrice = itemView.findViewById(R.id.etNegoPrice);
            tvDeliveryCharges = itemView.findViewById(R.id.tvDeliveryChargesNego);
            tvDeliveryChargesAnswer = itemView.findViewById(R.id.tvDeliveryChargesAndwerNego);
            tvNegoTimerText = itemView.findViewById(R.id.tvNegoTimerText);
            orderCancelMessage = itemView.findViewById(R.id.orderCancelMessage);
            negoLayoutFooter = itemView.findViewById(R.id.negoLayoutFooter);
            btnCalendar = itemView.findViewById(R.id.btnCalendar);
            btnAddAddress = itemView.findViewById(R.id.destination);
            btnNego = itemView.findViewById(R.id.sendProposal);
            btnAcceptOrder = itemView.findViewById(R.id.btnAcceptOrder);

            tvNegoDate = itemView.findViewById(R.id.tvNegoDate);
            tvNegoAddress = itemView.findViewById(R.id.tvNegoAddress);

        }

        @SuppressLint("SetTextI18n")
        public void bind(final int position) {

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                if (AppUtils.isSet(negoData.getExpired_at())) {
                    Date date = format.parse(negoData.getExpired_at());
                    Date c = Calendar.getInstance().getTime();

                    String formattedDate = format.format(c);
                    Date e = format.parse(formattedDate);

                    diff[0] = date.getTime() - e.getTime();
                    minutes[0] = diff[0] / (60 * 1000) % 60;
                    seconds[0] = diff[0] / 1000 % 60;
                    hours[0] = diff[0] / (60 * 60 * 1000);

                    NumberFormat f = new DecimalFormat("00");

                    if (diff[0] > 0)
                        tvNegoTimerText.setText(context.getResources().getString(R.string.you_have) + " " + negoData.getRe_tries() + " " +
                                context.getResources().getString(R.string.x_tries) + " " + f.format(hours[0]) + ":" + f.format(minutes[0]) + ":" + f.format(seconds[0])
                                + " " + context.getResources().getString(R.string.in_minutes));
                    else
                        tvNegoTimerText.setText(context.getResources().getString(R.string.you_have) + " " + String.valueOf(negoData.getRe_tries()) + " " +
                                context.getResources().getString(R.string.x_tries) + " 00:00:00 " + context.getResources().getString(R.string.in_minutes));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            tvNegoPriceSign.setText(SharedPreference.getSimpleString(context, Constants.currency));

            // Enabling and disabling the views on the basis of from order detail screen checks
            if (fromOrderDetails == 0) {
                btnAddAddress.setVisibility(View.VISIBLE);
                tvNegoTimerText.setVisibility(View.GONE);
                btnAddAddress.setEnabled(true);
                orderCancelMessage.setVisibility(View.GONE);
                negoLayoutFooter.setVisibility(View.VISIBLE);

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c);

                itemFooter.tvNegoDate.setVisibility(View.VISIBLE);
                itemFooter.tvNegoDate.setText(context.getResources().getString(R.string.cartDateText) + " : " +
                        formattedDate);

            } else if (fromOrderDetails == 1) {
                btnAddAddress.setVisibility(View.GONE);
                btnAddAddress.setEnabled(false);
                tvNegoTimerText.setVisibility(View.VISIBLE);

                if (negoData.getOrder_status().equals("Negotiation")) {
                    orderCancelMessage.setVisibility(View.GONE);

                    if (negotiations.get(0).getStatus().equals("Pending")) {
                        if (negotiations.get(0).getIsMerchant() == 1) {
                            negoLayoutFooter.setVisibility(View.VISIBLE);
                            btnAcceptOrder.setVisibility(View.VISIBLE);
                        } else {
                            negoLayoutFooter.setVisibility(View.GONE);
                        }
                    } else {
                        negoLayoutFooter.setVisibility(View.GONE);
                    }
                }

                if (negoData.getOrder_status().equals("Nego-approved")) {
                    orderCancelMessage.setVisibility(View.GONE);
                    negoLayoutFooter.setVisibility(View.GONE);
                } else if (negoData.getOrder_status().equals("Expired")) {
                    orderCancelMessage.setVisibility(View.VISIBLE);
                    negoLayoutFooter.setVisibility(View.GONE);
                } else if (negoData.getOrder_status().equals("Nego-rejected")) {
                    if (negoData.getRe_tries() == 0 && diff[0] < 0) {
                        orderCancelMessage.setVisibility(View.VISIBLE);
                        negoLayoutFooter.setVisibility(View.GONE);
                    } else if (negoData.getRe_tries() == 0 && diff[0] > 0) {
                        orderCancelMessage.setVisibility(View.VISIBLE);
                        negoLayoutFooter.setVisibility(View.GONE);
                    } else if (negoData.getRe_tries() > 0 && diff[0] < 0) {
                        orderCancelMessage.setVisibility(View.VISIBLE);
                        negoLayoutFooter.setVisibility(View.GONE);
                    } else {
                        orderCancelMessage.setVisibility(View.GONE);
                        negoLayoutFooter.setVisibility(View.VISIBLE);

                        if (negotiations.get(0).getIsMerchant() == 1) {
                            btnAcceptOrder.setVisibility(View.VISIBLE);
                        } else {
                            btnAcceptOrder.setVisibility(View.GONE);
                        }
                    }
                }
                countDownStart();
            }

            if (negoData.getDeliveryCharges().equals("0.00")) {
                tvDeliveryCharges.setVisibility(View.GONE);
                tvDeliveryChargesAnswer.setVisibility(View.GONE);
            } else {
                tvDeliveryCharges.setVisibility(View.VISIBLE);
                tvDeliveryChargesAnswer.setVisibility(View.VISIBLE);
                tvDeliveryChargesAnswer.setText(negoData.getDeliveryCharges() + SharedPreference.getSimpleString(context, Constants.currency));
            }


            // Button click to send the proposed price on negotiation
            btnNego.setOnClickListener(view -> {
                try {
                    if (fromOrderDetails == 1) {
                        if (etNegoPrice.getText().toString().isEmpty()) {
                            showToast((AppCompatActivity) context, context.getResources().getString(R.string.negoPriceErrorMessage), false);
                        } else
                            postNego();
                    } else {
                        if (shippingAddress.equals(""))
                            showToast((AppCompatActivity) context, context.getResources().getString(R.string.nego_add_shipping_address), false);
                        else if (etNegoPrice.getText().toString().isEmpty()) {
                            showToast((AppCompatActivity) context, context.getResources().getString(R.string.negoPriceErrorMessage), false);
                        } else {
                            if (etNegoPrice.getText().toString().isEmpty())
                                etNegoPrice.setText("0");
                            callNegoOrderApi(position);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            //Button click to add the address
            btnAddAddress.setOnClickListener(view -> {
                if (fromOrderDetails == 0)
                    showDeliveryDialog();

            });

            //Calendar button click to go to Native Calender to select Delivery/Pick-up date(Not a required field)
            btnCalendar.setOnClickListener(view -> {
                if (fromOrderDetails == 0) {
                    showDatePicker(deliveryPickupDate);
                    btnCalendar.setEnabled(true);
                }
            });

            btnAcceptOrder.setOnClickListener(v -> {
                acceptProposal();
            });

        }

        //Showing DatePicker to get Pick-up/Delivery Date
        private void showDatePicker(String pickedDate) {
            Calendar nowDate = Calendar.getInstance();


            if (AppUtils.isSet(pickedDate)) {
                nowDate = AppUtils.convertStrDateToCalendar(pickedDate);
            }

            DatePickerDialog dpd = new DatePickerDialog(context,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            month++;
                            deliveryPickupDate = AppUtils.formatDateFromDate(year + "-" + month + "-" + dayOfMonth);

                            itemFooter.tvNegoDate.setVisibility(View.VISIBLE);
                            itemFooter.tvNegoDate.setText(context.getResources().getString(R.string.cartDateText) + " : " +
                                    deliveryPickupDate);

                        }
                    },

                    nowDate.get(Calendar.YEAR),
                    nowDate.get(Calendar.MONTH),
                    nowDate.get(Calendar.DAY_OF_MONTH)
            );


            long nowDateCal = todaysDate.getTimeInMillis();
            dpd.getDatePicker().setMinDate(nowDateCal);
            dpd.show();
        }

        @SuppressLint("SetTextI18n")
        private void countDownStart() {

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
                            hours[0] = diff[0] / (60 * 60 * 1000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        NumberFormat f = new DecimalFormat("00");

                        tvNegoTimerText.setText(context.getResources().getString(R.string.you_have) + " " + negoData.getRe_tries() + " " +
                                context.getResources().getString(R.string.x_tries) + " " + f.format(hours[0]) + ":" + f.format(minutes[0]) + ":" + f.format(seconds[0])
                                + " " + context.getResources().getString(R.string.in_minutes));
                    }

                    public void onFinish() {
                        NumberFormat f = new DecimalFormat("00");
                        tvNegoTimerText.setText(context.getResources().getString(R.string.you_have) + " " + valueOf(negoData.getRe_tries()) + " " +
                                context.getResources().getString(R.string.x_tries) + " 00:00:00 " + context.getResources().getString(R.string.in_minutes));
                        System.out.println("Timer is stopped");
                        System.out.println("Difference value " + diff[0]);
                        System.gc();

                        if (negoData.getOrder_status().equals("Nego-rejected")) {
                            negoLayoutFooter.setVisibility(View.GONE);
                            orderCancelMessage.setVisibility(View.VISIBLE);
                        } else {
                            if (negoData.getOrder_status().equals("Negotiation")) {
                                negoLayoutFooter.setVisibility(View.GONE);
                            }
                        }
                    }
                }.start();

            }
        }
    }


    //Showing dialog to get the delivery type
    private void showDeliveryDialog() {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_delivery);

        ConstraintLayout truckLoad = dialog.findViewById(R.id.truckSharing);
        truckLoad.setOnClickListener(v -> {
            deliveryType = Constants.truckLoadDelivery;
            if (!isTruck) {
                shippingAddress = "";
                shippingDeliveryCharges = "0.00";
                negoData.setDeliveryCharges("0.00");
                isTruck = true;
                isFast = false;
            }
            dialog.dismiss();
            sendingToMapsActivity();
        });

        ConstraintLayout fast = dialog.findViewById(R.id.fastSharing);
        fast.setOnClickListener(v -> {
            deliveryType = Constants.fastDelivery;
            if (!isFast) {
                shippingAddress = "";
                shippingDeliveryCharges = "0.00";
                negoData.setDeliveryCharges("0.00");
                isFast = true;
                isTruck = false;
            }
            dialog.dismiss();
            sendingToMapsActivity();
        });

        ConstraintLayout pickUp = dialog.findViewById(R.id.pickup);
        pickUp.setOnClickListener(v -> {
            deliveryType = Constants.pickupDelivery;
            isFast = false;
            isTruck = false;
            shippingLatlng = new LatLng(1, 1);
            shippingAddress = "pickupAddress";
            shippingDeliveryCharges = "0.00";
            negoData.setDeliveryCharges("0.00");

            itemFooter.tvNegoAddress.setVisibility(View.VISIBLE);
            itemFooter.tvNegoAddress.setText(context.getResources().getString(R.string.cartAddressText) + " : " +
                    context.getResources().getString(R.string.pick_up));

            notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.show();

    }

    private void sendingToMapsActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromCart", false);
        bundle.putBoolean("fromSettings", false);
        bundle.putBoolean("fromNego", true);
        bundle.putString("merchantKeys", negoData.getMerchantId());
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //Calling the send proposal api for the first time when the user negotiate on a product
    private void callNegoOrderApi(int position) throws Exception {
        Common.showDialog(context);
        Api api = RetrofitClient.getClient().create(Api.class);

        //Comment in data sending in ItemThreePurchaseRecyclerAdapter is used here.
        Call<NegoUpdated> call = api.sendProposal("Bearer " +
                        SharedPreference.getSimpleString(context, Constants.accessToken), Integer.parseInt(negoData.getProductId()),
                AppUtils.convertStringToDouble(itemFooter.etNegoPrice.getText().toString()), negoData.getNegotiations().get(position - 1).getQuantity(),
                Integer.parseInt(negoData.getMerchantId()), shippingLatlng.latitude, shippingLatlng.longitude, shippingAddress,
                deliveryType, deliveryPickupDate);

        call.enqueue(new Callback<NegoUpdated>() {
            @Override
            public void onResponse(Call<NegoUpdated> call, Response<NegoUpdated> response) {
                Common.dissmissDialog();
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
//                        negotiations.clear();
                        NegoUpdated negoUpdated = new NegoUpdated(response.body().getStatus(),
                                response.body().getMessage(), response.body().getData());
//                        negoData = negoUpdated.getData();

                        showToast((AppCompatActivity) context, negoUpdated.getMessage(), true);

                        shippingAddress = "";
                        shippingDeliveryCharges = "0.00";
                        shippingLatlng = new LatLng(0, 0);
                        deliveryType = "";
                        deliveryPickupDate = "";


                        ((AppCompatActivity) context).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        ((MainActivity) context).goToOrders();

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast((AppCompatActivity) context, jsonObject.getString("message"), false);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                itemFooter.btnNego.setEnabled(false);
//                itemFooter.btnNego.setBackgroundColor(context.getResources().getColor(R.color.catalogueBackground));
            }

            @Override
            public void onFailure(Call<NegoUpdated> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Tag", "onFailure: " + t.getMessage());
            }
        });

    }

    //Calling post proposals api when the user is coming from the order details screen
    private void postNego() throws Exception {
        Common.showDialog(context);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<NegoUpdated> call = api.postProposals("Bearer " +
                        SharedPreference.getSimpleString(context, Constants.accessToken), orderId,
                Common.round(AppUtils.convertStringToDouble(itemFooter.etNegoPrice.getText().toString()), 2), 0);

        call.enqueue(new Callback<NegoUpdated>() {
            @Override
            public void onResponse(Call<NegoUpdated> call, Response<NegoUpdated> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        negotiations.clear();
                        NegoUpdated negoUpdated = new NegoUpdated(response.body().getStatus(),
                                response.body().getMessage(), response.body().getData());
                        negoData = negoUpdated.getData();

                        negotiations = negoData.getNegotiations();

                        notifyDataSetChanged();

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

    private void acceptProposal() {
        Common.showDialog(context);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.acceptProposal("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), orderId, 0);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast(((AppCompatActivity) context), jsonObject.getString("message"), true);

                        ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();
                        Intent intent = new Intent("refreshThePast");
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


}
