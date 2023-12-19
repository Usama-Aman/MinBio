package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.Notifications.Datum;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View v;
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ArrayList<Datum> notificationDatum;
    private MainActivity mainActivity;

    public NotificationAdapter(Context context, ArrayList<Datum> notificationDatum, MainActivity mainActivity) {
        this.context = context;
        this.notificationDatum = notificationDatum;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = inflater.inflate(R.layout.item_notifications, parent, false);
            viewHolder = new Item(v);
        } else {
            v = inflater.inflate(R.layout.item_loader_pagination_products, parent, false);
            viewHolder = new Progress(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof Item) {

            ((Item) holder).titleText.setText(notificationDatum.get(position).getNotification());
            ((Item) holder).timeText.setText(notificationDatum.get(position).getCreated_at());

            ((Item) holder).delete.setOnClickListener(v -> {
                callDeleteNotificationApi(position);
            });

            ((Item) holder).mainLayoutNotification.setOnClickListener(v -> {
                navigate(position);
            });

        }


    }

    @Override
    public int getItemCount() {
        return notificationDatum == null ? 0 : notificationDatum.size();
    }


    @Override
    public int getItemViewType(int position) {
        return notificationDatum.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class Item extends RecyclerView.ViewHolder {

        private ImageView delete, titleImage;

        private TextView titleText, timeText;

        private ConstraintLayout mainLayoutNotification;

        public Item(@NonNull View itemView) {
            super(itemView);

            delete = itemView.findViewById(R.id.notificationDelete);
            timeText = itemView.findViewById(R.id.notificationTime);
            titleText = itemView.findViewById(R.id.notificationTitle);
            titleImage = itemView.findViewById(R.id.notificationImage);
            mainLayoutNotification = itemView.findViewById(R.id.mainLayoutNotification);

        }


    }

    public class Progress extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public Progress(View v) {
            super(v);
            progressBar = itemView.findViewById(R.id.progressBarPagination);
        }


    }

    private void callDeleteNotificationApi(int position) {

        Common.showDialog(context);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.deleteNotification("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), notificationDatum.get(position).getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Common.dissmissDialog();
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast((AppCompatActivity) context, jsonObject.getString("message"), true);

                        notificationDatum.remove(position);
                        notifyDataSetChanged();

                        mainActivity.setNotificationBadge(1, 1);
                        if (notificationDatum.size() == 0) {
                            mainActivity.showingNullMessage();
                        }

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
                Log.d("", "onFailure: " + t.getMessage());
            }
        });
    }

    private void navigate(int position) {

        mainActivity.hideNotificationBar();

        String type = notificationDatum.get(position).getNotificationType();
        int orderId = notificationDatum.get(position).getOrderId();

        if (notificationDatum.get(position).getNotificationType().equals(Constants.ProposalReceived)) {
//            mainActivity.showProposalDialog(context, type, orderId);
            mainActivity.goToOrdersFromNoti(type, orderId);
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.ProposalAccepted)) {
            mainActivity.goToOrdersFromNoti(type, orderId);
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.ProposalAcceptedPreOrder)) {
            mainActivity.goToOrders();
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.ProposalRejected)) {
            mainActivity.goToOrdersFromNoti(type, orderId);
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.OrderExpired)) {
            mainActivity.goToOrdersFromNoti(type, orderId);
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.OrderAcceptedByDriver)) {
            mainActivity.goToTrackOrderScreen(orderId);
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.OrderCompletedByDriver)) {
            mainActivity.goToTrackOrderScreen(orderId);
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.DriverPickedUpProducts)) {
            mainActivity.goToTrackOrderScreen(orderId);
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.DriverReachedAtLocation)) {
            mainActivity.goToTrackOrderScreen(orderId);
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.DriverPickedUpProductsSeller)) {
            mainActivity.goToOrders();
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.AdminCommentOnComplaint)) {
            mainActivity.goToComplaintChatScreen(notificationDatum.get(position).getOrderId());
        } else if (notificationDatum.get(position).getNotificationType().equals(Constants.AdminCommentOnSupport)) {
            mainActivity.goToSupportChatScreen(notificationDatum.get(position).getOrderId());
        }

    }
}
