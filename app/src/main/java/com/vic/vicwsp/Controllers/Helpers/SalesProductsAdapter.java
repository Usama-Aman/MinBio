package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.ShowNullMessage;
import com.vic.vicwsp.Models.Response.AddProduct.Datum;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Fragments.SalesForm;
import com.vic.vicwsp.Views.Fragments.SalesProducts;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SalesProductsAdapter extends RecyclerView.Adapter<SalesProductsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Datum> productList = new ArrayList<>();
    private int productId;
    private AlertDialog alertDialog;
    private ShowNullMessage showNullMessage;

    public SalesProductsAdapter(Context context, ArrayList<Datum> productList, int productId, SalesProducts fragment) {
        this.context = context;
        this.productList = productList;
        this.productId = productId;
        this.showNullMessage = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_products, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.name.setText(productList.get(position).getProductVariety());
        holder.price.setText(Common.convertInKFormat(productList.get(position).getPrice()) + SharedPreference.getSimpleString
                (context, Constants.currency));
        holder.unit.setText(context.getResources().getString(R.string.per_kg, productList.get(position).getUnit()));

        try {
            Glide.with(context)
                    .load(productList.get(position).getFile())
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder))
                    .into(holder.productImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SalesForm salesForm = new SalesForm();
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromEdit", true);
                bundle.putInt("saleProductId", productList.get(position).getId());
                bundle.putInt("productId", productId);
                bundle.putString("unit", productList.get(position).getUnit());
                salesForm.setArguments(bundle);

                ((AppCompatActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, salesForm, "Sales Form")
                        .addToBackStack("Sales Form")
                        .commit();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog = new AlertDialog.Builder(context)
                        .setMessage(context.getResources().getString(R.string.delete_product))
                        .setPositiveButton(context.getResources().getString(R.string.yes), (dialog, which) -> callDeleteSaleProductApi(position))
                        .setNegativeButton(context.getResources().getString(R.string.no), (dialogInterface, i) -> alertDialog.dismiss())
                        .show();
            }

            private void callDeleteSaleProductApi(int position) {

                Common.showDialog(context);
                Api api = RetrofitClient.getClient().create(Api.class);

                Call<ResponseBody> call = api.deleteSubProduct("Bearer " +
                        SharedPreference.getSimpleString(context, Constants.accessToken), productList.get(position).getId());

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Common.dissmissDialog();
                        JSONObject jsonObject = null;
                        try {
                            if (response.isSuccessful()) {

                                jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getBoolean("status")) {
                                    Common.showToast(((AppCompatActivity) context), jsonObject.getString("message"), true);
                                    productList.remove(position);
                                    notifyDataSetChanged();


                                    if (productList.size() == 0) {
                                        showNullMessage.showNullMessage();

                                        Intent intent = new Intent("CallProductDetail");
                                        intent.putExtra("productId", String.valueOf(productId));
                                        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);

                                        Intent updateMerchants = new Intent("UpdateMerchants");
                                        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(updateMerchants);

                                    }

                                    //TODO add delete socket price update
//                                    JSONObject emitJson = new JSONObject();
//                                    emitJson.put("price", salesPrice.getText().toString());
//                                    emitJson.put("merchant_id", SharedPreference.getSimpleString(getContext(), Constants.userId));
//                                    emitJson.put("product_id", String.valueOf(productIdFromBundle));
//                                    emitJson.put("subproduct_id", String.valueOf(saleProductId));
//                                    ApplicationClass applicationClass = (ApplicationClass) ((AppCompatActivity) context).getApplication();
//                                    applicationClass.emit(ApplicationClass.update_product, emitJson, context);

                                } else
                                    Common.showToast(((AppCompatActivity) context), jsonObject.getString("message"), false);

                            } else {
                                jsonObject = new JSONObject(response.errorBody().string());
                                Common.showToast(((AppCompatActivity) context), jsonObject.getString("message"), false);
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

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, price, unit;
        private ImageView delete, edit, productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvSalesProductName);
            productImage = itemView.findViewById(R.id.ivSalesProductImage);
            price = itemView.findViewById(R.id.tvSalesProductsPrice);
            unit = itemView.findViewById(R.id.tvSaleProductsUnit);
            edit = itemView.findViewById(R.id.ivEditSalesProduct);
            delete = itemView.findViewById(R.id.ivDeleteSalesProduct);

        }
    }
}
