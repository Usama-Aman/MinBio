package com.vic.vicwsp.Views.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import org.json.JSONObject;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class DriverRating extends AppCompatActivity {

    private ImageView back;
    private MaterialRatingBar priceRatingBar;
    private double rating = 0.0;
    private EditText etComments;
    private ConstraintLayout done;
    private int orderId = 0, driverId = 0;
    private String TAG = "RatingActivity";
    private TextView tvEnterRating;
    private int position = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_rating);

        initViews();

    }

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        orderId = bundle.getInt("orderId");
        driverId = bundle.getInt("driverId");
        position = bundle.getInt("position");

        tvEnterRating = findViewById(R.id.tvEnterRating);
        tvEnterRating.setText(getResources().getString(R.string.rate_the_driver));

        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(view -> {
            finish();
        });

        priceRatingBar = findViewById(R.id.ratingbarMerchant);
        priceRatingBar.setOnRatingChangeListener((ratingBar, rating) -> {
            this.rating = (double) rating;
        });

        etComments = findViewById(R.id.etCompliment);
        done = findViewById(R.id.btnRatingDone);

        done.setOnClickListener(view -> validate());
    }

    private void validate() {

        if (etComments.getText().toString().isEmpty()) {
            showToast(DriverRating.this, getResources().getString(R.string.enter_compliment), false);
            return;
        }

        callRatingApi();
    }

    private void callRatingApi() {

        Common.showDialog(DriverRating.this);
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.giveDriverRating("Bearer " +
                        SharedPreference.getSimpleString(DriverRating.this, Constants.accessToken),
                etComments.getText().toString(), orderId, rating, driverId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast(DriverRating.this, jsonObject.getString("message"), true);

                        Intent intent = new Intent("DriverRating");
                        intent.putExtra("is_reviewed", 1);
                        intent.putExtra("position", position);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1500);
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(DriverRating.this, jsonObject.getString("message"), false);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: ." + t.getMessage());
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
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

}
