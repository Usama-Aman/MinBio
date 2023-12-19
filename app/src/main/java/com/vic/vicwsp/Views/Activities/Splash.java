package com.vic.vicwsp.Views.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.ApplicationClass;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryPickupDate;
import static com.vic.vicwsp.Views.Activities.MainActivity.deliveryType;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingAddress;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingDeliveryCharges;
import static com.vic.vicwsp.Views.Activities.MainActivity.shippingLatlng;

public class Splash extends AppCompatActivity {

    public static String appLanguage;
    private String type = "", messageData = "", unit = "", price = "", body = "", quantity = "";
    private int orderId = 0;
    boolean isNotification = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Constants.isAppRunning = true;
        shippingLatlng = new LatLng(0, 0);
        shippingAddress = "";
        shippingDeliveryCharges = "0.00";
        deliveryType = "";
        deliveryPickupDate = "";

//        To delete the local database while testing if required
//        Splash.this.deleteDatabase(DatabaseHelper.DATABASE_NAME);

        //To delete the cart session
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.dropCart();
        databaseHelper.close();

        if (!SharedPreference.getBoolean(Splash.this, Constants.isFirstTime)) {
            SharedPreference.saveBoolean(this, Constants.isFirstTime, true);
            SharedPreference.saveSimpleString(this, Constants.language, Constants.english);
            Splash.appLanguage = Constants.english;
            ApplicationClass applicationClass = (ApplicationClass) Splash.this.getApplication();
            applicationClass.changeLocale(Splash.this);
        } else {
            if (SharedPreference.getSimpleString(Splash.this, Constants.language).equals(Constants.french)) {
                appLanguage = Constants.english;
                ApplicationClass applicationClass = (ApplicationClass) Splash.this.getApplication();
                applicationClass.changeLocale(Splash.this);
            } else if (SharedPreference.getSimpleString(Splash.this, Constants.language).equals(Constants.english)) {
                appLanguage = Constants.english;
                ApplicationClass applicationClass = (ApplicationClass) Splash.this.getApplication();
                applicationClass.changeLocale(Splash.this);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String value = SharedPreference.getSimpleString(getApplicationContext(), Constants.isLoggedIn);
                    if (!value.isEmpty()) {
                        if (value.equals("true")) {
                            checkForCOverage();
                        } else if (value.equals("false")) {
                            Intent i = new Intent(Splash.this, SignIn.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Intent i = new Intent(Splash.this, SignIn.class);
                        startActivity(i);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
    }

    private void checkForCOverage() {

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.getPendingPayment("Bearer " +
                SharedPreference.getSimpleString(Splash.this, Constants.accessToken));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = null;

                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());

                        if (getIntent().hasExtra("type")) {
                            type = getIntent().getStringExtra("type");
                            orderId = Integer.parseInt(getIntent().getStringExtra("orderId"));
                            messageData = (getIntent().getStringExtra("message"));
                            isNotification = getIntent().getBooleanExtra("isComingFromNotification", false);
                            price = getIntent().getStringExtra("price");
                            quantity = getIntent().getStringExtra("quantity");
                            body = getIntent().getStringExtra("body");
                            unit = getIntent().getStringExtra("unit");
                        }

                        Intent i = new Intent(Splash.this, MainActivity.class);
                        i.putExtra("type", type);
                        i.putExtra("orderId", orderId);
                        i.putExtra("message", messageData);
                        i.putExtra("price", price);
                        i.putExtra("quantity", quantity);
                        i.putExtra("body", body);
                        i.putExtra("unit", unit);
                        if (isNotification)
                            i.putExtra("isComingFromNotification", true);
                        startActivity(i);
                        finish();

                    } else {

                        if (response.code() == 401) {
                            SharedPreference.saveSimpleString(Splash.this, Constants.isLoggedIn, "false");
                            Intent i = new Intent(Splash.this, SignIn.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            JSONObject errorJson = new JSONObject(response.errorBody().string());
                            Common.showToast(Splash.this, errorJson.getString("message"), false);

                            JSONObject data = errorJson.getJSONObject("data");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Intent intent = new Intent(Splash.this, Coverage.class);
                                        intent.putExtra("message", errorJson.getString("message"));
                                        intent.putExtra("amount", data.getString("coverage_amount"));
                                        intent.putExtra("orderId", data.getInt("order_id"));
                                        intent.putExtra("fromWhere", "Splash");

                                        startActivity(intent);
                                        finish();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 3000);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppTheme);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.AppThemeNight);
                break;
        }
    }
}