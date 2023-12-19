package com.vic.vicwsp.Controllers.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.ChartData.ProductDetailChartData;
import com.vic.vicwsp.Models.Response.ChartData.Report;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Data;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class FromTickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Data data;
    private ProductDetailChartData productDetailChartData;
    private centerFromTicker centerFromTicker;


    public FromTickerAdapter(Context context, Data data) {
        this.context = context;
        this.data = data;
        this.productDetailChartData = productDetailChartData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_product_detail, parent, false);
            return new headerFromTicker(itemView);
        } else if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bar_chart, parent, false);
            return new centerFromTicker(itemView);
        }
//        else if (viewType == 2) {
//            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_two_product_detail_updated, parent, false);
//            return new bottomFromTicker(itemView);
//        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (getItemViewType(position) == 0) {
                ((headerFromTicker) holder).bind(position);
            } else if (getItemViewType(position) == 1) {
                centerFromTicker = ((centerFromTicker) holder);
                centerFromTicker.bind(position);
            }
//            else if (getItemViewType(position) == 2) {
//                ((bottomFromTicker) holder).bind(position);
//            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class headerFromTicker extends RecyclerView.ViewHolder {

        ImageView productDetailImage, ivFavoriteDetails;
        TextView tvProductNameDetails, tvPriceDetails, detailDescription, tvPerKg;

        private headerFromTicker(@NonNull View itemView) {
            super(itemView);

            productDetailImage = itemView.findViewById(R.id.productDetailsImage);
            tvProductNameDetails = itemView.findViewById(R.id.tvProductNameDetails);
            tvPriceDetails = itemView.findViewById(R.id.tvPriceDetails);
            detailDescription = itemView.findViewById(R.id.productDetailDescription);
            tvPerKg = itemView.findViewById(R.id.tvPerKg);
        }


        private void bind(int position) {
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

            if (centerFromTicker != null)
                centerFromTicker.actualPriceAnswer.setText(
                        Common.convertInKFormat((data.getDetails().getPrice())) + SharedPreference.getSimpleString(context, Constants.currency));

        }
    }

    public class centerFromTicker extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Button annual, weekly, monthly;
        private TextView actualPrice, normalPrice, actualPriceAnswer, normalPriceAnswer;
        private ConstraintLayout loaderConstraint;
        private LineChartView lineChartView;

        private centerFromTicker(@NonNull View itemView) {
            super(itemView);

            annual = itemView.findViewById(R.id.btnAnnual);
            monthly = itemView.findViewById(R.id.btnMonthly);
            weekly = itemView.findViewById(R.id.btnWeekly);

            annual.setOnClickListener(this);
            monthly.setOnClickListener(this);
            weekly.setOnClickListener(this);

            actualPrice = itemView.findViewById(R.id.tvActualPrice);
            actualPriceAnswer = itemView.findViewById(R.id.tvActualPriceAnswer);
            normalPrice = itemView.findViewById(R.id.tvNormalPrice);
            normalPriceAnswer = itemView.findViewById(R.id.tvNormalPriceAnswer);

            loaderConstraint = itemView.findViewById(R.id.loaderConstraint);
            lineChartView = itemView.findViewById(R.id.lineChartView);

        }

        @SuppressLint("SetTextI18n")
        private void bind(int position) {

            callChartDataApi();

            actualPriceAnswer.setText(
                    Common.convertInKFormat((data.getDetails().getPrice())) + SharedPreference.getSimpleString(context, Constants.currency));


        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.btnAnnual) {
                annual.setBackground(context.getResources().getDrawable(R.drawable.round_button));
                monthly.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
                weekly.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));

                normalPrice.setText(context.getResources().getString(R.string.average_price_year));

                if (productDetailChartData != null) {
                    populateChartData(0);
                }


            } else if (view.getId() == R.id.btnMonthly) {
                monthly.setBackground(context.getResources().getDrawable(R.drawable.round_button));
                annual.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
                weekly.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));

                normalPrice.setText(context.getResources().getString(R.string.average_price_month));

                if (productDetailChartData != null) {
                    populateChartData(1);
                }


            } else if (view.getId() == R.id.btnWeekly) {
                weekly.setBackground(context.getResources().getDrawable(R.drawable.round_button));
                annual.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
                monthly.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));

                normalPrice.setText(context.getResources().getString(R.string.average_price_week));

                if (productDetailChartData != null) {
                    populateChartData(2);
                }

            }

        }

        private void callChartDataApi() {

            Api api = RetrofitClient.getClient().create(Api.class);

            Call<ProductDetailChartData> productDetailChartDataCall = api.getChartData("Bearer " +
                    SharedPreference.getSimpleString(context, Constants.accessToken), data.getId());

            productDetailChartDataCall.enqueue(new Callback<ProductDetailChartData>() {
                @Override
                public void onResponse(Call<ProductDetailChartData> call, Response<ProductDetailChartData> response) {

                    try {
                        if (response.isSuccessful()) {

                            productDetailChartData = new ProductDetailChartData(response.body().getStatus(),
                                    response.body().getMessage(), response.body().getData());

                            loaderConstraint.setVisibility(View.GONE);
                            lineChartView.setVisibility(View.VISIBLE);

                            populateChartData(0);
                            annual.setBackground(context.getResources().getDrawable(R.drawable.round_button));
                            monthly.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));
                            weekly.setBackground(context.getResources().getDrawable(R.drawable.round_button_gray));

                        } else {
                            showToast((AppCompatActivity) context, response.body().getMessage(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ProductDetailChartData> call, Throwable t) {
                    Log.d("Tag", "onFailure: " + t.getMessage());
                }
            });

        }

        @SuppressLint("SetTextI18n")
        private void populateChartData(int i) {

            if (i == 0) {
                try {
                    setYearData(productDetailChartData.getData().getYear().getReport());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                normalPriceAnswer.setText(Common.convertInKFormat((productDetailChartData.getData().getYear().getAverageprice())) + SharedPreference.getSimpleString(context, Constants.currency));


            } else if (i == 1) {
                try {
                    setYearData(productDetailChartData.getData().getMonth().getReport());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                normalPriceAnswer.setText(Common.convertInKFormat((productDetailChartData.getData().getMonth().getAverageprice())) + SharedPreference.getSimpleString(context, Constants.currency));
            } else if (i == 2) {
                try {
                    setYearData(productDetailChartData.getData().getWeek().getReport());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                normalPriceAnswer.setText(Common.convertInKFormat((productDetailChartData.getData().getWeek().getAverageprice())) + SharedPreference.getSimpleString(context, Constants.currency));
            }
        }

        private void setYearData(List<Report> reports) throws Exception {

            List<PointValue> yAxisValues = new ArrayList<>();
            List<AxisValue> axisValues = new ArrayList<>();

            for (int i = 0; i < reports.size(); i++) {
                axisValues.add(i, new AxisValue(i).setLabel(reports.get(i).getTitle()));
            }

            double max = 0;

            for (int i = 0; i < reports.size(); i++) {
                yAxisValues.add(new PointValue(i, AppUtils.convertStringToFloat(reports.get(i).getValue())));
                if (AppUtils.convertStringToDouble(reports.get(i).getValue()) > max)
                    max = AppUtils.convertStringToDouble(reports.get(i).getValue());
            }

            Line line = new Line(yAxisValues).setColor(context.getResources().getColor(R.color.signUpButtonColor)).setPointRadius(0).setStrokeWidth(3);

            List<Line> lines = new ArrayList<>();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis axis = new Axis();
            axis.setValues(axisValues);
            axis.setTextSize(14);
            axis.setTextColor(context.getResources().getColor(R.color.black));
            data.setAxisXBottom(axis);


            Axis yAxis = new Axis();
            yAxis.setTextColor(context.getResources().getColor(R.color.black));
            yAxis.setTextSize(14);
            yAxis.setHasLines(true);
            data.setAxisYLeft(yAxis);

            lineChartView.setZoomEnabled(false);
//            lineChartView.setInteractive(true);
            lineChartView.setLineChartData(data);
            lineChartView.setViewportCalculationEnabled(true);
        }


    }

    private class bottomFromTicker extends RecyclerView.ViewHolder {

        private ExpandableListView expandableListView;
        private ExpandableItemProductDetail expandableItemProductDetail;
        private int lastExpandedPosition = -1;


        private bottomFromTicker(@NonNull View itemView) {
            super(itemView);
            expandableListView = itemView.findViewById(R.id.productDetailExpandableListView);
            expandableListView.setGroupIndicator(null);
            expandableListView.setChildIndicator(null);

        }

        private void bind(int position) {

            expandableItemProductDetail = new ExpandableItemProductDetail(context, data, true);
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
