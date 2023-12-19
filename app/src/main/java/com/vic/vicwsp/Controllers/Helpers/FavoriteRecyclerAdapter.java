package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.ShowNullMessage;
import com.vic.vicwsp.Models.Response.FavoriteUpdate.Datum;
import com.vic.vicwsp.Models.Response.FavoriteUpdate.Detail;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.SubProduct;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.ProductView;
import com.vic.vicwsp.Views.Fragments.Favorites;
import com.vic.vicwsp.Views.Fragments.ProductDetail;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class FavoriteRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View v;
    private Context context;
    private ArrayList<Datum> favoriteData;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ShowNullMessage showNullMessage; //Interface to show the null message

    private Favorites favorites;

    public FavoriteRecyclerAdapter(Context context, ArrayList<Datum> favoriteData, Favorites favorites, boolean radiationStatus) {
        this.context = context;
        this.favoriteData = favoriteData;
        showNullMessage = favorites;
        this.favorites = favorites;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = inflater.inflate(R.layout.item_fav_search_products, parent, false);
            viewHolder = new Items(v);
        } else {
            v = inflater.inflate(R.layout.item_loader_pagination_products, parent, false);
            viewHolder = new Progress(v);
        }
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {


        try {
            if (holder instanceof Progress) {
                showLoadingView((Progress) holder, position);
            } else {


                ((Items) holder).productName.setText(favoriteData.get(position).getDetail().getProductName());
                ((Items) holder).productVariety.setText(favoriteData.get(position).getName());
                ((Items) holder).price.setText(Common.convertInKFormat((favoriteData.get(position).getPrice())) + SharedPreference.getSimpleString
                        (context, Constants.currency));

                Glide.with(context)
                        .load(favoriteData.get(position).getImagePath())
                        .into(((Items) holder).productImage);
                Glide.with(context)
                        .load(context.getResources().getDrawable(R.drawable.del))
                        .into(((Items) holder).delete);


                ((Items) holder).delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Common.showDialog(context);
                        try {
                            callDeleteApi(favoriteData.get(position).getSubproductId(), ((Items) holder), position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                ((Items) holder).productConstraint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!favorites.radiationStatus) {
                            goToProductDetails(new ProductDetail(), "Product Details", view, position);
                        } else {
                            Common.showToast((AppCompatActivity) context, context.getResources().getString(R.string.radiation_status_is_not_good), false);
                        }

                    }
                });
            }
        } catch (
                ClassCastException e) {
            e.printStackTrace();
        }


    }

    private void showLoadingView(Progress holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return favoriteData == null ? 0 : favoriteData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return favoriteData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    //Calling api to delete the item from favourites
    //Function to go to the Product detail screen

    private void goToProductDetails(Fragment fragment, String key, View view, int position) {

        Detail detail = favoriteData.get(position).getDetail();

        SubProduct subProduct = new SubProduct(detail.getId(), detail.getProductVariety(), detail.getProDescription(), detail.getPrice(),
                detail.getMinprice(), detail.getMaxprice(), detail.getStock(), detail.getSaleCase(), detail.getIsBio(), detail.getIsNego(),
                detail.getVat(), detail.getDiscount(), detail.getOriginFlag(), detail.getImagePath(), detail.getIsFavourite(), detail.getUnit(),
                detail.getSubUnit(), detail.getProClass(), detail.getSize(), null, detail.getCompanyName(), 0);

        Bundle bundle = new Bundle();
        bundle.putInt("merchantId", favoriteData.get(position).getDetail().getSellerId());
        bundle.putString("merchantName", favoriteData.get(position).getDetail().getSellerName());
        bundle.putParcelable("SubProduct", subProduct);
        bundle.putString("productName", favoriteData.get(position).getDetail().getProductName());
        bundle.putParcelableArrayList("Files", detail.getFiles());
        Intent intent = new Intent(context, ProductView.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //Class to show the progress bar on scroll pagination
    public class Progress extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public Progress(View v) {
            super(v);
            progressBar = itemView.findViewById(R.id.progressBarPagination);
        }

    }

    //Item class to show the favourites list
    protected class Items extends RecyclerView.ViewHolder {

        private TextView price, from, productName, productRetail, marchants, productVariety;

        private ImageView productImage, delete, go;
        private ConstraintLayout productConstraint;

        Items(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.ivProductImage);
            marchants = itemView.findViewById(R.id.tvMarchants);
            marchants.setVisibility(View.GONE);
            price = itemView.findViewById(R.id.tvPrice);
            from = itemView.findViewById(R.id.tvFrom);
            productName = itemView.findViewById(R.id.tvProductName);
            productRetail = itemView.findViewById(R.id.tvProductRetail);
            delete = itemView.findViewById(R.id.ivDeleteProduct);
            go = itemView.findViewById(R.id.ivIconGo);
            productConstraint = itemView.findViewById(R.id.productsConstraint);
            productVariety = itemView.findViewById(R.id.tvProductVariety);
        }

    }

    //Same Api used for Add to fav and delete from fav // Logic is in api
    private void callDeleteApi(Integer id, final Items holder, final int position) throws Exception {

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> setFav = api.setFavorite("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), id);

        setFav.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("status")) {
                            JSONObject data = new JSONObject();
                            data = jsonObject.getJSONObject("data");
                            if (data.getInt("is_favourite") == 0) {
                                favoriteData.remove(position);
                                notifyDataSetChanged();
                            }

                            showToast((AppCompatActivity) context, jsonObject.getString("message"), true);

                            if (favoriteData.size() == 0) {
                                showNullMessage.showNullMessage();
                            }

                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast((AppCompatActivity) context, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    showToast((AppCompatActivity) context, context.getResources().getString(R.string.something_is_not_right), false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Tag", "onFailure: " + t.getMessage());
            }
        });

    }
}