package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Data;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Seller;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.SubProduct;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.CommentsActivity;

import org.json.JSONObject;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class ExpandableItemProductDetail extends BaseExpandableListAdapter {

    private Context context;
    private Data data;
    private boolean fromFav = false;

    public ExpandableItemProductDetail(Context context, Data data, boolean fromFav) {
        this.context = context;
        this.data = data;
        this.fromFav = fromFav;
    }

    @Override
    public int getGroupCount() {
        return data.getSellers().size();
    }

    @Override
    public int getChildrenCount(int i) {
        return data.getSellers().get(i).getSubProducts().size();
    }

    @Override
    public Object getGroup(int i) {
        return data.getSellers().get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return data.getSellers().get(i).getSubProducts().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View view, ViewGroup viewGroup) {

        Seller seller = (Seller) getGroup(listPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_two_pd_group_view, null);
        }

        TextView sellerName = view.findViewById(R.id.tvVendeurDetails);
        TextView price = view.findViewById(R.id.tvPriceDetails);
        MaterialRatingBar rattingBarDetails = view.findViewById(R.id.rattingBarDetails);
        sellerName.setText(seller.getName());
        price.setText(Common.convertInKFormat((seller.getPrice())) + SharedPreference.getSimpleString
                (context, Constants.currency));
        rattingBarDetails.setRating(AppUtils.convertStringToFloat(seller.getRating()));

        LinearLayout linearBarRating = view.findViewById(R.id.linearBarRating);
        linearBarRating.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("sellerId", seller.getId());
            intent.putExtras(bundle);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((AppCompatActivity) context, (View) rattingBarDetails, "ratingProductDetail");
            context.startActivity(intent, options.toBundle());
        });

        return view;
    }

    @Override
    public View getChildView(int listPosition, int expandedPosition, boolean b, View convertView, ViewGroup viewGroup) {
        SubProduct subProduct = (SubProduct) getChild(listPosition, expandedPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.sub_item_child_view_product_detail, null);
        }

        TextView productName, productPrice;
        ImageView bio, nego, origin, fav, productImage;

        productName = convertView.findViewById(R.id.ch_pd_productName);
        productPrice = convertView.findViewById(R.id.ch_pd_productPrice);

        bio = convertView.findViewById(R.id.ch_pd_productBio);
        nego = convertView.findViewById(R.id.ch_pd_productNego);
        origin = convertView.findViewById(R.id.ch_pd_productOrigin);
        fav = convertView.findViewById(R.id.ch_productFav);
        productImage = convertView.findViewById(R.id.ch_productImage);

        productPrice.setText(subProduct.getPrice() + SharedPreference.getSimpleString
                (context, Constants.currency));
        productName.setText(subProduct.getProductVariety());

        nego.setVisibility(View.GONE);
        bio.setVisibility(View.GONE);


        try {
            Glide.with(context)
                    .load(subProduct.getImagePath())
                    .placeholder(context.getResources().getDrawable(R.drawable.place_holder))
                    .into(productImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (subProduct.getIsNego() == 1) {
            nego.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(context.getResources().getDrawable(R.drawable.pr))
                    .into(nego);
        }
        if (subProduct.getIsBio() == 1) {
            bio.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(context.getResources().getDrawable(R.drawable.ab))
                    .into(bio);
        }
        Glide.with(context)
                .load(subProduct.getOriginFlag())
                .into(origin);


        if (subProduct.getIsFavourite() == 0) {
            Glide.with(context)
                    .load(R.drawable.star_un)
                    .into(fav);
        } else if (subProduct.getIsFavourite() == 1) {
            Glide.with(context)
                    .load(R.drawable.star_sl)
                    .into(fav);
        }


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calAddToFavApi(subProduct, subProduct.getId());
            }
        });


        return convertView;
    }

    private void calAddToFavApi(SubProduct subProduct, Integer id) {
        Common.showDialog(context);
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
                            JSONObject jdata = new JSONObject();
                            jdata = jsonObject.getJSONObject("data");
                            if (jdata.getInt("is_favourite") == 0) {
                                subProduct.setIsFavourite(0);
                            } else if (jdata.getInt("is_favourite") == 1) {
                                subProduct.setIsFavourite(1);
                            }
                            notifyDataSetChanged();
                            showToast(((AppCompatActivity) context), jsonObject.getString("message"), true);

                            if (fromFav) {
                                Intent f = new Intent("UpdateFavoriteProducts");
                                LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(f);
                            }

                        }

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) context), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    showToast(((AppCompatActivity) context), context.getResources().getString(R.string.something_is_not_right), false);
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

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
