package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Data;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class UpdatedProductDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Data data;
    private boolean fromFav = false;

    public UpdatedProductDetailRecyclerAdapter(Context context, Data data, boolean fromfav) {
        this.context = context;
        this.data = data;
        this.fromFav = fromfav;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_product_detail, parent, false);
            return new ItemOne(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_two_product_detail_updated, parent, false);
            return new ItemTwo(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (getItemViewType(position) == 0) {
                ((ItemOne) holder).bind(position);
            } else if (getItemViewType(position) == 1) {
                ((ItemTwo) holder).bind(position);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
//        if (position == 0)
        return 0;
//        else
//            return 1;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    private class ItemOne extends RecyclerView.ViewHolder {

        ImageView productDetailImage, ivFavoriteDetails;
        TextView tvProductNameDetails, tvPriceDetails, detailDescription, tvPerKg;


        private ItemOne(@NonNull View itemView) {
            super(itemView);

            productDetailImage = itemView.findViewById(R.id.productDetailsImage);
            tvProductNameDetails = itemView.findViewById(R.id.tvProductNameDetails);
            tvPriceDetails = itemView.findViewById(R.id.tvPriceDetails);
            detailDescription = itemView.findViewById(R.id.productDetailDescription);
            tvPerKg = itemView.findViewById(R.id.tvPerKg);
        }

        public void bind(int position) {

            tvProductNameDetails.setText(data.getName());
            tvPriceDetails.setText(Common.convertInKFormat((data.getDetails().getPrice())) + SharedPreference.getSimpleString
                    (context, Constants.currency));
            tvPerKg.setText(context.getResources().getString(R.string.per_kg, data.getUnit()));
            detailDescription.setText(data.getProductDescription());


            try {
                Picasso.get()
                        .load(data.getImagePath())
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .placeholder(context.getResources().getDrawable(R.drawable.place_holder))
                        .into(productDetailImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class ItemTwo extends RecyclerView.ViewHolder {

        private ExpandableListView expandableListView;
        private ExpandableItemProductDetail expandableItemProductDetail;
        private int lastExpandedPosition = -1;

        private ItemTwo(@NonNull View itemView) {
            super(itemView);
            expandableListView = itemView.findViewById(R.id.productDetailExpandableListView);
            expandableListView.setGroupIndicator(null);
            expandableListView.setChildIndicator(null);


        }

        public void bind(int position) {
            expandableItemProductDetail = new ExpandableItemProductDetail(context, data, fromFav);
            expandableListView.setAdapter(expandableItemProductDetail);
            setListViewHeightBasedOnItems(expandableListView);

            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int i) {
                    setListViewHeightBasedOnItems(expandableListView);
                    if (lastExpandedPosition != -1
                            && i != lastExpandedPosition) {
                        expandableListView.collapseGroup(lastExpandedPosition);
                    }
                    lastExpandedPosition = i;
                }
            });

            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int i) {
                    setListViewHeightBasedOnItems(expandableListView);
                }
            });

        }
    }


    private void setListViewHeightBasedOnItems(ExpandableListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }
}
