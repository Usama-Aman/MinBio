package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Controllers.Interfaces.ScrollUpListView;
import com.vic.vicwsp.Controllers.Interfaces.SendingDataToProductView;
import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Data;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Seller;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.SubProduct;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.ProductView;
import com.vic.vicwsp.Views.Fragments.PurchaseForm;
import com.vic.vicwsp.Views.Fragments.SalesProducts;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Timer;

import static com.vic.vicwsp.Utils.Common.showToast;

//import com.elementary.minbio.Models.Response.ProductDetail.Seller;

public class UpdatePurchaseFormRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ItemThree holderItemThree;
    private ItemTwo holderItemTwo;
    private Data data;
    public static double purchaseAmount = 0;
    private ArrayList<Seller> bioSellers = new ArrayList<>();
    private ScrollUpListView scrollUpListView;

    public UpdatePurchaseFormRecyclerAdapter(Context context, Data data, PurchaseForm purchaseForm) {
        this.context = context;
        this.data = data;
        purchaseAmount = 0;
        scrollUpListView = purchaseForm;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_one, parent, false);
            return new ItemOne(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_second, parent, false);
            return new ItemTwo(itemView);
        } else if (viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_two_product_detail_updated, parent, false);
            return new ItemThree(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (getItemViewType(position) == 0) {
                ((ItemOne) holder).bind();
            } else if (getItemViewType(position) == 1) {
                holderItemTwo = ((ItemTwo) holder);
                holderItemTwo.bind(position);
            } else if (getItemViewType(position) == 2) {
                holderItemThree = (ItemThree) holder;
                holderItemThree.bind();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else if (position == 1)
            return 1;
        else
            return 2;
    }


    class ItemOne extends RecyclerView.ViewHolder {
        ImageView productDetailImage;
        TextView tvProductNameDetails, tvPriceDetails, tvPerKgPurchase;
        ConstraintLayout toSellConstraint;

        private ItemOne(@NonNull View itemView) {
            super(itemView);
            productDetailImage = itemView.findViewById(R.id.purchaseImage);
            tvProductNameDetails = itemView.findViewById(R.id.tvProductNamePurchase);
            tvPriceDetails = itemView.findViewById(R.id.tvPricePurchase);
            tvPerKgPurchase = itemView.findViewById(R.id.tvPerKgPurchase);
            toSellConstraint = itemView.findViewById(R.id.btnToSellPurchase);

        }

        @SuppressLint("SetTextI18n")
        void bind() {

            Glide.with(context)
                    .load(data.getImagePath())
                    .into(productDetailImage);
            tvProductNameDetails.setText(data.getName());
            tvPriceDetails.setText(Common.convertInKFormat((data.getDetails().getPrice())) + SharedPreference.getSimpleString
                    (context, Constants.currency));


            if (!MainActivity.radiationStatus) {
                toSellConstraint.setBackground(context.getResources().getDrawable(R.drawable.round_button_green));
                toSellConstraint.setEnabled(true);
            } else {
                toSellConstraint.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
                toSellConstraint.setEnabled(false);
            }

            toSellConstraint.setOnClickListener(v -> {
                if (data != null)
                    if (SharedPreference.getSimpleString(context, Constants.isMerchant).equals("1")) {

                        SalesProducts salesProducts = new SalesProducts();
                        Bundle bundle = new Bundle();
                        bundle.putInt("productId", data.getId());
                        bundle.putString("productName", data.getName());
                        bundle.putString("unit", data.getUnit());
                        salesProducts.setArguments(bundle);

                        ((AppCompatActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainFragment, salesProducts, "Sales Products")
                                .addToBackStack("Sales Products")
                                .commit();
                    } else
                        showToast(((AppCompatActivity) context), context.getResources().getString(R.string.you_are_not_marchant), false);
                else
                    showToast(((AppCompatActivity) context), context.getResources().getString(R.string.something_is_not_right), false);
            });
        }
    }

    class ItemTwo extends RecyclerView.ViewHolder {

        EditText editText;
        SwitchButton switchButton;
        //        Switch switchButton;
        private TextView textView;
        private boolean isBioOn = false;
        double amount = 0;

        private ItemTwo(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.etPurchaseAmount);
            textView = itemView.findViewById(R.id.unitPurchase);
            switchButton = itemView.findViewById(R.id.purchaseBioiSwitch);
        }

        public void bind(int position) {


            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                private Timer timer = new Timer();
                private final long DELAY = 5000; // milliseconds

                @Override
                public void afterTextChanged(final Editable editable) {


                    if (editable.toString().equals("")) {
                        amount = 0;
                        purchaseAmount = 0;
                    } else {
                        amount = AppUtils.convertStringToDouble(editable.toString());
                        purchaseAmount = amount;
                    }

                    checkForStock();

                    holderItemThree.expandableItemPurchaseForm.notifyDataSetChanged();
                }
            });


            switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    bioSellers.clear();

                    try {
                        if (isChecked) {
                            isBioOn = true;
                            ArrayList<SubProduct> bioSubProducts = null;
                            for (int i = 0; i < data.getSellers().size(); i++) {
                                bioSubProducts = new ArrayList<>();
                                for (int j = 0; j < data.getSellers().get(i).getSubProducts().size(); j++) {
                                    if (data.getSellers().get(i).getSubProducts().get(j).getIsBio() == 1) {
                                        bioSubProducts.add(new SubProduct(data.getSellers().get(i).getSubProducts().get(j).getId(),
                                                data.getSellers().get(i).getSubProducts().get(j).getProductVariety(), data.getSellers().get(i).getSubProducts().get(j).getPro_description(),
                                                data.getSellers().get(i).getSubProducts().get(j).getPrice(), data.getSellers().get(i).getSubProducts().get(j).getMinprice(), data.getSellers().get(i).getSubProducts().get(j).getMaxprice(),
                                                data.getSellers().get(i).getSubProducts().get(j).getStock(), data.getSellers().get(i).getSubProducts().get(j).getSaleCase(),
                                                data.getSellers().get(i).getSubProducts().get(j).getIsBio(), data.getSellers().get(i).getSubProducts().get(j).getIsNego(),
                                                data.getSellers().get(i).getSubProducts().get(j).getVat(), data.getSellers().get(i).getSubProducts().get(j).getDiscount(),
                                                data.getSellers().get(i).getSubProducts().get(j).getOriginFlag(), data.getSellers().get(i).getSubProducts().get(j).getImagePath(),
                                                data.getSellers().get(i).getSubProducts().get(j).getIsFavourite(), data.getSellers().get(i).getSubProducts().get(j).getUnit(),
                                                data.getSellers().get(i).getSubProducts().get(j).getSub_unit(), data.getSellers().get(i).getSubProducts().get(j).getSub_unit(),
                                                data.getSellers().get(i).getSubProducts().get(j).getSubProductClass(), data.getSellers().get(i).getSubProducts().get(j).getFiles(),
                                                data.getSellers().get(i).getSubProducts().get(j).getCompany_name(), data.getSellers().get(i).getSubProducts().get(j).getHasStock()));
                                    }
                                }

                                if (bioSubProducts.size() > 0)
                                    bioSellers.add(new Seller(data.getSellers().get(i).getId(),
                                            data.getSellers().get(i).getName(), data.getSellers().get(i).getRating(),
                                            data.getSellers().get(i).getPrice(), bioSubProducts));
                            }

                            holderItemThree.setExpandable(bioSellers);
                            checkForStock();


                        } else {
                            isBioOn = false;
//                            holderItemThree.lastExpandedPosition = -1;
                            holderItemThree.setExpandable(data.getSellers());
                            checkForStock();
                        }

                        if (holderItemThree.lastExpandedPosition != -1)
                            if (isBioOn) {
                                for (int i = 0; i < bioSellers.size(); i++)
                                    if (i == holderItemThree.lastExpandedPosition)
                                        if (bioSellers.get(i).getSubProducts().size() > 0)
                                            holderItemThree.expandableListView.expandGroup(holderItemThree.lastExpandedPosition);
                                        else
                                            holderItemThree.lastExpandedPosition = -1;
                            } else {
                                for (int i = 0; i < data.getSellers().size(); i++)
                                    if (i == holderItemThree.lastExpandedPosition)
                                        if (data.getSellers().get(i).getSubProducts().size() > 0)
                                            holderItemThree.expandableListView.expandGroup(holderItemThree.lastExpandedPosition);
                            }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        private void checkForStock() {
            DatabaseHelper db = new DatabaseHelper(context.getApplicationContext());
            ArrayList<CartModel> cartArrayList = new ArrayList<>();
            cartArrayList = db.getAllCartData();

            if (cartArrayList.size() > 0) {

                if (isBioOn) {
                    checkFromCartAsWell(bioSellers, cartArrayList, amount);
                } else {

                    checkFromCartAsWell(data.getSellers(), cartArrayList, amount);
                }

            } else {
                if (isBioOn)
                    for (int i = 0; i < bioSellers.size(); i++) {
                        for (int j = 0; j < bioSellers.get(i).getSubProducts().size(); j++) {
                            if (bioSellers.get(i).getSubProducts().get(j).getStock() < amount) {
                                bioSellers.get(i).getSubProducts().get(j).setHasStock(1); //To blur the layout
                            } else
                                bioSellers.get(i).getSubProducts().get(j).setHasStock(0);
                        }
                    }
                else {
                    for (int i = 0; i < data.getSellers().size(); i++) {
                        for (int j = 0; j < data.getSellers().get(i).getSubProducts().size(); j++) {
                            if (data.getSellers().get(i).getSubProducts().get(j).getStock() < amount) {
                                data.getSellers().get(i).getSubProducts().get(j).setHasStock(1); //To blur the layout
                            } else
                                data.getSellers().get(i).getSubProducts().get(j).setHasStock(0);
                        }
                    }

                }

            }
        }

        private void checkFromCartAsWell(ArrayList<Seller> sellers, ArrayList<CartModel> cartArrayList, double amount) {

            for (int i = 0; i < sellers.size(); i++) {
                for (int j = 0; j < sellers.get(i).getSubProducts().size(); j++) {
                    for (int k = 0; k < cartArrayList.size(); k++) {
                        if (cartArrayList.get(k).getProduct_id() == sellers.get(i).getSubProducts().get(j).getId()) {
                            if (cartArrayList.get(k).getMarchant_id() == sellers.get(i).getId()) {
                                if (cartArrayList.get(k).getQuantity() + amount > sellers.get(i).getSubProducts().get(j).getStock()) {
                                    sellers.get(i).getSubProducts().get(j).setHasStock(1);  //To blur the layout
                                } else {
                                    sellers.get(i).getSubProducts().get(j).setHasStock(0);
                                }
                            } else {
                                if (sellers.get(i).getSubProducts().get(j).getStock() < amount) {
                                    sellers.get(i).getSubProducts().get(j).setHasStock(1);
                                } else {
                                    sellers.get(i).getSubProducts().get(j).setHasStock(0);
                                }
                            }
                        } else {
                            if (sellers.get(i).getSubProducts().get(j).getStock() < amount) {
                                sellers.get(i).getSubProducts().get(j).setHasStock(1);
                            } else {
                                sellers.get(i).getSubProducts().get(j).setHasStock(0);
                            }
                        }
                    }
                }
            }

        }

    }

    class ItemThree extends RecyclerView.ViewHolder implements SendingDataToProductView {

        ExpandableListView expandableListView;
        ExpandableItemPurchaseForm expandableItemPurchaseForm;
        private int lastExpandedPosition = -1;


        private ItemThree(@NonNull View itemView) {
            super(itemView);
            expandableListView = itemView.findViewById(R.id.productDetailExpandableListView);
        }

        public void bind() {
            setExpandable(data.getSellers());

            try {
                if (SharedPreference.getSimpleString(context, Constants.isBioOn).equals("true")) {
                    holderItemTwo.switchButton.setChecked(true);
                } else
                    holderItemTwo.switchButton.setChecked(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void setExpandable(ArrayList<Seller> sellers) {

            expandableItemPurchaseForm = new ExpandableItemPurchaseForm(context, sellers, true, this);
            expandableListView.setAdapter(expandableItemPurchaseForm);
            setListViewHeightBasedOnChildren(expandableListView);

            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int i) {
                    if (lastExpandedPosition != -1 && i != lastExpandedPosition) {
                        expandableListView.collapseGroup(lastExpandedPosition);
                    }
                    lastExpandedPosition = i;
                    setListViewHeightBasedOnChildren(expandableListView);
                    scrollUpListView.scroll();

                }
            });

            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int i) {
                    setListViewHeightBasedOnChildren(expandableListView);
                }
            });

        }

        @Override
        public void sendingDataToProductView(int listPosition, int expandedListPosition, Seller seller) {

            SubProduct subProduct = seller.getSubProducts().get(expandedListPosition);
            Bundle bundle = new Bundle();
            bundle.putInt("merchantId", seller.getId());
            bundle.putString("merchantName", seller.getName());
            bundle.putString("productName", data.getName());
            bundle.putParcelable("SubProduct", subProduct);
            bundle.putParcelableArrayList("Files", subProduct.getFiles());
            Intent intent = new Intent(context, ProductView.class);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }


    private void setListViewHeightBasedOnChildren(ExpandableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height = totalHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}
