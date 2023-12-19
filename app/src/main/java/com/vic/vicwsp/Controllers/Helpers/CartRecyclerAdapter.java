package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.ShowNullMessage;
import com.vic.vicwsp.Controllers.Interfaces.UpdateDeliveryCharges;
import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Models.Request.Cart.Product;
import com.vic.vicwsp.Models.Request.Cart.Product_;
import com.vic.vicwsp.Models.Response.Outstanding.OutstandingModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.Coverage;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.MapsActivity;
import com.vic.vicwsp.Views.Fragments.Cart;
import com.vic.vicwsp.Views.Fragments.Payment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.suke.widget.SwitchButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicwsp.Controllers.Helpers.ItemThreePurchaseAdapter.cartTotal;
import static com.vic.vicwsp.Utils.Common.round;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryPickupDate;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryType;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingAddress;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingDeliveryCharges;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingLatlng;

public class CartRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<CartModel> carts;                    //Array list having all the data to show on cart screen
    private ItemFooter itemFooter;                         //Instance of the footer item of the Cart Screen
    private ItemHeader itemHeader;                         //Instance of the header item of the Cart Screen
    private ShowNullMessage showNullMessage;               //Interface to show the no data message on cart screen
    private AlertDialog alertDialog;
    private UpdateDeliveryCharges updateDeliveryCharges;   //Interface to update the delivery charges on product added or removed
    private Cart fragment;
    private int outstandingCheck = 0;
    private OutstandingModel outstandingModel;
    private long mLastClickTime = 0;
    private boolean isTruck = false, isFast = false;
    Calendar todaysDate;

    //    Local Broad cast receiver to update the view after the location is set
    private void registerSendDeliveryBroadCast() {

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals("sendDeliveryBroadCast")) {
                        if (!shippingAddress.equals("")) {

                            if (carts.size() > 0) {
                                fragment.updateDeliveryCharges(context, getMerchants(),
                                        String.valueOf(shippingLatlng.latitude), String.valueOf(shippingLatlng.longitude));

                                itemFooter.tvCartAddress.setVisibility(View.VISIBLE);
                                itemFooter.tvCartAddress.setText(context.getResources().getString(R.string.cartAddressText) + " : " +
                                        shippingAddress);
                            }
                        } else
                            notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter("sendDeliveryBroadCast"));
    }

    //Constructor
    public CartRecyclerAdapter(Context context, ArrayList<CartModel> carts, Cart cart) {
        this.context = context;
        todaysDate = Calendar.getInstance();
        this.fragment = cart;
        this.carts = carts;
        showNullMessage = cart;
        updateDeliveryCharges = cart;
        registerSendDeliveryBroadCast();
        isTruck = false;
        isFast = false;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_header, parent, false);
            return new ItemHeader(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_cart, parent, false);
            return new ItemFooter(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder.getItemViewType() == 0) {
                itemHeader = ((ItemHeader) holder);
                itemHeader.bind(position);
            } else if (holder.getItemViewType() == 1) {
                itemFooter = ((ItemFooter) holder);
                itemFooter.bind();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    //Overriding method to get the position of the items of the recycler view
    @Override
    public int getItemViewType(int position) {

        if (carts.size() == 1) {
            return position;
        } else {
            if (position == carts.size())
                return 1;
            else
                return 0;
        }
    }

    @Override
    public int getItemCount() {
        return carts.size() + 1;
    }

    //Item header class for Cart screen
    public class ItemHeader extends RecyclerView.ViewHolder {

        private TextView productName, cartUnit, productDescription, price, tvCartProductDiscount, tvCartProductVariety;
        private ImageView deleteItem;
        private ConstraintLayout cartHeader;
        private ConstraintLayout constraintLayout;
        private CustomSearchableSpinner cartSpinner;
        private ArrayList<String> saleCasesStrings = new ArrayList<>();

        private ItemHeader(@NonNull View itemView) {
            super(itemView);
            productDescription = itemView.findViewById(R.id.tvCartProductDescription);
            cartUnit = itemView.findViewById(R.id.cartUnit);
            productName = itemView.findViewById(R.id.tvCartProductName);
            price = itemView.findViewById(R.id.tvCartPrice);
            deleteItem = itemView.findViewById(R.id.ivDeleteCartProduct);
            cartHeader = itemView.findViewById(R.id.cartHeader);
            constraintLayout = itemView.findViewById(R.id.cartLinear);
            tvCartProductDiscount = itemView.findViewById(R.id.tvCartProductDiscount);
            cartSpinner = itemView.findViewById(R.id.cartSpinner);
            tvCartProductVariety = itemView.findViewById(R.id.tvCartProductVariety);
        }


        @SuppressLint("SetTextI18n")
        public void bind(final int position) {
            try {
                if (position % 2 == 1) {
                    cartHeader.setBackgroundColor(context.getResources().getColor(R.color.white));
                    constraintLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.purchase_edittext));
                } else {
                    cartHeader.setBackgroundColor(context.getResources().getColor(R.color.catalogueBackground));
                    constraintLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.white_purchase_edittext));
                }
                productName.setText(carts.get(position).getProduct_name());
                tvCartProductVariety.setText(carts.get(position).getProductVariety());
                cartUnit.setText(carts.get(position).getUnit());
                productDescription.setText(context.getResources().getString(R.string.cartSoldBy) + " " + carts.get(position).getCompanyName());
                if (AppUtils.convertStringToDouble(carts.get(position).getDiscount()) > 0.0) {
                    tvCartProductDiscount.setVisibility(View.VISIBLE);
                    tvCartProductDiscount.setText(context.getResources().getString(R.string.discount_on_product, carts.get(position).getDiscount()) + context.getResources().getString(R.string.percentage));
                } else {
                    tvCartProductDiscount.setVisibility(View.GONE);
                }


                price.setText(Common.round(AppUtils.convertStringToDouble(carts.get(position).getPrice()) *
                        carts.get(position).getQuantity(), 2) + SharedPreference.getSimpleString(context, Constants.currency));

                // Deleting the item from the cart
                deleteItem.setOnClickListener(view -> {

                    deleteItem.requestFocus();

                    alertDialog = new AlertDialog.Builder(context)
                            .setMessage(context.getResources().getString(R.string.cartDeleteProduct))
                            .setPositiveButton(context.getResources()
                                    .getString(R.string.yes), (dialog, which) -> {

                                DatabaseHelper db = new DatabaseHelper(context);
                                db.deleteCart(carts.get(position));

                                ArrayList<CartModel> cartListFromDb = new ArrayList<>();
                                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                                cartListFromDb = databaseHelper.getAllCartData();

                                if (cartListFromDb.size() == 0)
                                    cartTotal = 0;
                                else {
                                    cartTotal = getTotal(cartListFromDb);
                                }

                                databaseHelper.close();

                                ((MainActivity) context).updateCart(String.valueOf(Common.round(cartTotal, 2)));

                                carts.remove(position);


                                if (carts.size() == 0) {
                                    itemFooter.footer.setVisibility(View.GONE);
                                    showNullMessage.showNullMessage();
                                }

                                Intent sendDeliveryBroadCast = new Intent("sendDeliveryBroadCast");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(sendDeliveryBroadCast);

                                notifyDataSetChanged();

                            })
                            .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    alertDialog.dismiss();
                                }
                            }).show();
                });


                setUpSpinner(position);

                for (int i = 0; i < saleCasesStrings.size(); i++) {
                    if (AppUtils.convertStringToFloat(saleCasesStrings.get(i)) == carts.get(position).getQuantity()) {
                        cartSpinner.setSelection(i);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setUpSpinner(int position) {

            saleCasesStrings.clear();

            if (carts.get(position).getSubUnit().equals("")) {
                for (float i = AppUtils.convertStringToFloat(carts.get(position).getSaleCase()); i <= carts.get(position).getStock(); i = i + AppUtils.convertStringToFloat(carts.get(position).getSaleCase()))
                    saleCasesStrings.add(String.format(Locale.ENGLISH, "%.2f", i));
            } else {
                for (int i = 1; i <= carts.get(position).getStock(); i++)
                    saleCasesStrings.add(String.format(Locale.ENGLISH, "%.2f", i));
            }


            cartSpinner.setPositiveButton(context.getResources().getString(R.string.close_spinner));
            cartSpinner.setTitle(context.getResources().getString(R.string.select_quantity));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, saleCasesStrings);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cartSpinner.setAdapter(adapter);
            cartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                    DatabaseHelper databaseHelper = new DatabaseHelper(context);

                    databaseHelper.updateCart(new CartModel(carts.get(position).getSession_id(),
                            carts.get(position).getProduct_id(), carts.get(position).getMarchant_id(),
                            AppUtils.convertStringToFloat(String.valueOf(adapterView.getSelectedItem())), carts.get(position).getPrice(),
                            carts.get(position).getProduct_name(), carts.get(position).getProduct_description(),
                            carts.get(position).getMarchant_name(), carts.get(position).getProduct_image(),
                            carts.get(position).getStock(), carts.get(position).getUnit(), carts.get(position).getDiscount(),
                            carts.get(position).getVat(), String.valueOf(carts.get(position).getSaleCase()), carts.get(position).getSubUnit(),
                            carts.get(position).getDelivery_type(), carts.get(position).getCompanyName(), carts.get(position).getProductVariety()));

                    ArrayList<CartModel> list = databaseHelper.getAllCartData();
                    cartTotal = getTotal(list);
                    ((MainActivity) context).updateCart(String.valueOf(round(cartTotal, 2)));

                    CartModel model = databaseHelper.getSingleCartItem(carts.get(position).getProduct_id(), carts.get(position).getMarchant_id());

                    carts.get(position).setSession_id(model.getSession_id());
                    carts.get(position).setProduct_id(model.getProduct_id());
                    carts.get(position).setMarchant_id(model.getMarchant_id());
                    carts.get(position).setQuantity(model.getQuantity());
                    carts.get(position).setPrice(model.getPrice());
                    carts.get(position).setProduct_name(model.getProduct_name());
                    carts.get(position).setProduct_description(model.getProduct_description());
                    carts.get(position).setMarchant_name(model.getMarchant_name());
                    carts.get(position).setProduct_image(model.getProduct_image());
                    carts.get(position).setStock(model.getStock());
                    carts.get(position).setSubUnit(model.getSubUnit());

                    price.setText(Common.round(AppUtils.convertStringToDouble(carts.get(position).getPrice()) * carts.get(position).getQuantity(), 2) + SharedPreference.getSimpleString(context, Constants.currency));
                    if (itemFooter != null)
                        calculateTheFooter();

                    databaseHelper.close();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }
    }

    private static double getTotal(ArrayList<CartModel> cartList) {
        double priceTotal = 0;
        try {
            for (int i = 0; i < cartList.size(); i++) {
                priceTotal += cartList.get(i).getQuantity() * AppUtils.convertStringToDouble(cartList.get(i).getPrice());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceTotal;
    }


    //Item Footer class of the cart Screen
    public class ItemFooter extends RecyclerView.ViewHolder {

        private ConstraintLayout footer, btnConfirmCart;
        private TextView tvTotaltHtAnswer, tvTVAAnswer, tvTVA, tvTotalTTCAnswer, deliveryCharges, deliveryChargesAnwser, tvCartBottom,
                tvTotalDiscountAnswer, tvTotalDiscount, tvOutstandingText;
        private TextView btnAddAddress;
        private SwitchButton outstandingSwitch;
        private TextView btnCalendar, tvCartAddress, tvCartDate;

        private ItemFooter(@NonNull View itemView) {
            super(itemView);

            btnCalendar = itemView.findViewById(R.id.btnCalendar);
            footer = itemView.findViewById(R.id.cartFooter);
            btnConfirmCart = itemView.findViewById(R.id.btnConfirmCart);
            btnAddAddress = itemView.findViewById(R.id.btnAddAddress);
            tvTotaltHtAnswer = itemView.findViewById(R.id.tvTotaltHtAnswer);
            tvTVAAnswer = itemView.findViewById(R.id.tvTVAAnswer);
            tvTVA = itemView.findViewById(R.id.tvTVA);
            tvTotalTTCAnswer = itemView.findViewById(R.id.tvTotalTTCAnswer);
            tvTotalDiscountAnswer = itemView.findViewById(R.id.tvTotalDiscountAnswer);
            tvTotalDiscount = itemView.findViewById(R.id.tvTotalDiscount);

            deliveryCharges = itemView.findViewById(R.id.tvDeliveryCharges);
            deliveryChargesAnwser = itemView.findViewById(R.id.tvDeliveryChargesAndwer);

            outstandingSwitch = itemView.findViewById(R.id.outstandingSwitch);
            tvOutstandingText = itemView.findViewById(R.id.tvOutstandingSwitchText);

            tvCartAddress = itemView.findViewById(R.id.tvCartAddress);
            tvCartDate = itemView.findViewById(R.id.tvCartDate);

        }

        @SuppressLint("SetTextI18n")
        public void bind() {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c);

            itemFooter.tvCartDate.setVisibility(View.VISIBLE);
            itemFooter.tvCartDate.setText(context.getResources().getString(R.string.cartDateText) + " : " +
                    formattedDate);

            try {
                btnCalendar.setVisibility(View.VISIBLE);
                btnAddAddress.setVisibility(View.VISIBLE);
                btnConfirmCart.setVisibility(View.VISIBLE);
                tvTotalDiscountAnswer.setVisibility(View.VISIBLE);
                tvTotalDiscount.setVisibility(View.VISIBLE);
                outstandingSwitch.setVisibility(View.VISIBLE);
                tvOutstandingText.setVisibility(View.VISIBLE);

                if (outstandingCheck == 0)
                    outstandingSwitch.setChecked(false);
                else if (outstandingCheck == 1)
                    outstandingSwitch.setChecked(true);


                if (shippingAddress.equals("")) {
                    btnConfirmCart.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
                    btnConfirmCart.setEnabled(false);
                } else {
                    btnConfirmCart.setBackground(context.getResources().getDrawable(R.drawable.round_button_green));
                    btnConfirmCart.setEnabled(true);
                }

                if (shippingDeliveryCharges.equals("0.00")) {
                    deliveryChargesAnwser.setVisibility(View.GONE);
                    deliveryCharges.setVisibility(View.GONE);
                } else {
                    deliveryChargesAnwser.setVisibility(View.VISIBLE);
                    deliveryCharges.setVisibility(View.VISIBLE);
                    deliveryChargesAnwser.setText(shippingDeliveryCharges + SharedPreference.getSimpleString(context, Constants.currency));
                }

                //Outstanding switch
                outstandingSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                        btnConfirmCart.setEnabled(false);
                        if (isChecked) {
                            outstandingCheck = 1;
                        } else {
                            outstandingCheck = 0;
                        }
                        if (shippingAddress.equals("")) {
                            btnConfirmCart.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
                            btnConfirmCart.setEnabled(false);
                        } else {
                            btnConfirmCart.setBackground(context.getResources().getDrawable(R.drawable.round_button_green));
                            btnConfirmCart.setEnabled(true);
                        }
                    }
                });

                //Calendar button click to go to Native Calender to select Delivery/Pick-up date(Not a required field)
                btnCalendar.setOnClickListener(view -> {
                    showDatePicker(deliveryPickupDate);
                    btnCalendar.setEnabled(true);
                });

                //Delivery address button click to go to Maps Screen to get the location of the user
                btnAddAddress.setOnClickListener(view -> {
                    showDeliveryDialog();
                    btnAddAddress.setEnabled(true);
                });

                // Validate the basket button click -> going to the payment screen
                btnConfirmCart.setOnClickListener(view -> {

                    if (outstandingCheck == 0) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("fromOrderDetails", false);
                        Payment payment = new Payment();
                        payment.setArguments(bundle);
                        ((AppCompatActivity) context)
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.mainFragment, payment, "Payment")
                                .addToBackStack("Payment").commit();
                    } else if (outstandingCheck == 1) {
                        try {
                            callCoverageApi();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            /*
            Calculating the price and other things to show on the footer of the cart screen
             */
                calculateTheFooter();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private void calculateTheFooter() {

        double htt = 0.0, discount = 0.0, vat = 0.0, total = 0.0;

        for (int i = 0; i < carts.size(); i++) {
            htt += AppUtils.convertStringToDouble(carts.get(i).getPrice()) * carts.get(i).getQuantity();
            double d = (AppUtils.convertStringToDouble(carts.get(i).getPrice()) * carts.get(i).getQuantity() / 100) * AppUtils.convertStringToDouble(carts.get(i).getDiscount());
            vat += ((AppUtils.convertStringToDouble(carts.get(i).getPrice()) * carts.get(i).getQuantity() - d) / 100) * AppUtils.convertStringToDouble(carts.get(i).getVat());
            discount += d;
        }

        try {
            itemFooter.tvTotaltHtAnswer.setText(Common.round(htt, 2) + SharedPreference.getSimpleString(context, Constants.currency));

            if (discount <= 0) {
                itemFooter.tvTotalDiscount.setVisibility(View.GONE);
                itemFooter.tvTotalDiscountAnswer.setVisibility(View.GONE);
            } else {
                itemFooter.tvTotalDiscount.setVisibility(View.VISIBLE);
                itemFooter.tvTotalDiscountAnswer.setVisibility(View.VISIBLE);
                itemFooter.tvTotalDiscountAnswer.setText(Common.round(discount, 2) + SharedPreference.getSimpleString(context, Constants.currency));
            }

            itemFooter.tvTVAAnswer.setText(Common.round(vat, 2) + SharedPreference.getSimpleString(context, Constants.currency));
            total = (htt - discount) + vat + AppUtils.convertStringToDouble(shippingDeliveryCharges);

            itemFooter.tvTotalTTCAnswer.setText(BigDecimal.valueOf(Math.floor(total * 100) / 100) + SharedPreference.getSimpleString(context, Constants.currency));


        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        itemFooter.tvCartDate.setVisibility(View.VISIBLE);
                        itemFooter.tvCartDate.setText(context.getResources().getString(R.string.cartDateText) + " : " +
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


    //Showing dialog to get the delivery type
    private void showDeliveryDialog() {

        try {
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
                    isFast = true;
                    isTruck = false;
                }
                dialog.dismiss();
                sendingToMapsActivity();
            });

            ConstraintLayout pickUp = dialog.findViewById(R.id.pickup);
            pickUp.setOnClickListener(v -> {
                deliveryType = Constants.pickupDelivery;
                shippingLatlng = new LatLng(1, 1);
                shippingAddress = "pickupAddress";
                shippingDeliveryCharges = "0.00";
                isFast = false;
                isTruck = false;
                itemFooter.btnConfirmCart.setBackground(context.getResources().getDrawable(R.drawable.round_button_green));
                itemFooter.btnConfirmCart.setEnabled(true);
                itemFooter.deliveryChargesAnwser.setVisibility(View.GONE);
                itemFooter.deliveryCharges.setVisibility(View.GONE);

                itemFooter.tvCartAddress.setVisibility(View.VISIBLE);
                itemFooter.tvCartAddress.setText(context.getResources().getString(R.string.cartAddressText) + " : " +
                        context.getResources().getString(R.string.pick_up));

                notifyDataSetChanged();
                dialog.dismiss();
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //Function TO go to Maps Screen

    private void sendingToMapsActivity() {

        String merchantsKeys = getMerchants();
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromNego", false);
        bundle.putBoolean("fromSettings", false);
        bundle.putBoolean("fromCart", true);
        bundle.putString("merchantKeys", merchantsKeys);
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //Function to get the non redundant merchant ids String from the list of merchants

    private String getMerchants() {
        ArrayList<String> merchants = new ArrayList<>();
        merchants.clear();

        for (int i = 0; i < carts.size(); i++)
            merchants.add(String.valueOf(carts.get(i).getMarchant_id()));

        Set<String> set = new HashSet<>(merchants);
        merchants.clear();
        merchants.addAll(set);

        return android.text.TextUtils.join(",", merchants);
    }


    private void callCoverageApi() {

        Common.showDialog(context);
        try {
            ArrayList<Integer> merchants = new ArrayList<>();
            merchants.clear();

//            DatabaseHelper db = new DatabaseHelper(context);
//            carts = db.getAllCartData();
//            db.close();


            for (int i = 0; i < carts.size(); i++) {
                merchants.add(carts.get(i).getMarchant_id());
            }

            Set<Integer> set = new HashSet<>(merchants);
            merchants.clear();
            merchants.addAll(set);

            ArrayList<Product> products = new ArrayList<>();
            for (int m = 0; m < merchants.size(); m++) {
                Product product = new Product();
                product.setMerchantId(merchants.get(m));
                ArrayList<Product_> product_s = new ArrayList<>();
                for (int c = 0; c < carts.size(); c++) {
                    if (merchants.get(m) == carts.get(c).getMarchant_id()) {
                        product_s.add(new Product_(String.valueOf(carts.get(c).getProduct_id()), String.valueOf(carts.get(c).getQuantity()),
                                carts.get(c).getDiscount(), carts.get(c).getVat()));
                    }
                }
                product.setProducts(product_s);
                products.add(product);
            }

            Gson gson = new Gson();
            String json = gson.toJson(products);


            JSONArray jsonArray = new JSONArray(json);

            Api api = RetrofitClient.getClient().create(Api.class);

            Call<ResponseBody> call = api.getCoverage("Bearer " +
                            SharedPreference.getSimpleString(context, Constants.accessToken)
                    , String.valueOf(shippingLatlng.latitude), String.valueOf(shippingLatlng.longitude), deliveryType, jsonArray);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Common.dissmissDialog();
                    try {
                        JSONObject jsonObject = null;
                        if (response.isSuccessful()) {
                            jsonObject = new JSONObject(response.body().string());
                            String message = jsonObject.getString("message");

                            JSONObject data = jsonObject.getJSONObject("data");
                            String amount = data.getString("overdraft_amount");
                            String overdraft_logs = data.getString("overdraft_logs");

                            Intent intent = new Intent(context, Coverage.class);
                            intent.putExtra("message", message);
                            intent.putExtra("amount", amount);
                            intent.putExtra("overdraft_logs", overdraft_logs);
                            intent.putExtra("fromWhere", "Cart");
                            context.startActivity(intent);
                        } else {
                            jsonObject = new JSONObject(response.errorBody().string());
                            showToast((AppCompatActivity) context, jsonObject.getString("message"), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Common.dissmissDialog();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
