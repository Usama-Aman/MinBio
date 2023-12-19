package com.vic.vicwsp.Controllers.Helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Request.LoginRequest;
import com.vic.vicwsp.Models.Response.Login.Login;
import com.vic.vicwsp.Models.User;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.EmailConfirmation;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.OtpConfirmation;
import com.vic.vicwsp.Views.Activities.Splash;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.dissmissDialog;
import static com.vic.vicwsp.Utils.Common.showToast;

@SuppressLint("NewApi")
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private static final String TAG = FingerprintHandler.class.getSimpleName();
    private Context context;
    private CancellationSignal cancellationSignal;


    public FingerprintHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Common.hideKeyboard((AppCompatActivity) context);
        if (SharedPreference.getSimpleString(context, Constants.isFirstLogin).equals("true")) {
            Common.showDialog(context);
            callLoginApi();
        } else
            showToast((AppCompatActivity) context, context.getResources().getString(R.string.signInErrorMessagePlease_login_at_least_one_time), false);

    }

    //Calling login api on finger print
    private void callLoginApi() {

        Api api = RetrofitClient.getClient().create(Api.class);
        LoginRequest loginRequest = new LoginRequest(SharedPreference.getSimpleString(context, Constants.fingerPrintUser),
                SharedPreference.getSimpleString(context, Constants.userpassword));
        Call<Login> loginCall = api.login(Splash.appLanguage, loginRequest);

        loginCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        Login login = new Login(response.body().getData(), response.body().getAccessToken(), response.body().getStatus(),
                                response.body().getMessage());

                        if (login.getStatus()) {

                            SharedPreference.saveSimpleString(context, Constants.accessToken, login.getAccessToken());

                            if (login.getData() != null) {
                                User user = new User(login.getData().getId(), login.getData().getName(), login.getData().getLastName(),
                                        login.getData().getEmail(), login.getData().getCompanyName(), login.getData().getSiretNo(),
                                        login.getData().getPhone(), login.getData().getLang(), login.getData().getCountryCode(),
                                        login.getData().getLat(), login.getData().getLng(), login.getData().getAddress(),
                                        login.getData().getIs_merchant(), login.getData().getNego_auto_accept(),
                                        login.getData().getKbis_file_path(), login.getData().getId_file_path(), login.getData().getBank_detail_id());

                                DatabaseHelper db = new DatabaseHelper(context);
                                List<User> users = new ArrayList<>();
                                users = db.getAllUsers();

                                boolean isPresent = false;

                                if (users.size() == 0)
                                    db.insertUser(user);
                                else {
                                    for (int i = 0; i < users.size(); i++) {
                                        if (users.get(i).getId().equals(user.getId())) {
                                            db.updateUser(user);
                                            isPresent = true;
                                        }
                                    }

                                    if (!isPresent)
                                        db.insertUser(user);
                                }
                                db.close();
                                SharedPreference.saveSimpleString(context, Constants.userId, String.valueOf(login.getData().getId()));
                                SharedPreference.saveSimpleString(context, Constants.isLoggedIn, "true");
                                SharedPreference.saveSimpleString(context, Constants.isFirstLogin, "true");
                                SharedPreference.saveSimpleString(context, Constants.fingerPrintUser, login.getData().getEmail());
                                SharedPreference.saveSimpleString(context, Constants.countryCode, login.getData().getCountryCode());
                                SharedPreference.saveSimpleString(context, Constants.userPhone, login.getData().getPhone());
                                SharedPreference.saveSimpleString(context, Constants.userEmail, login.getData().getEmail());
                                SharedPreference.saveSimpleString(context, Constants.isMerchant, String.valueOf(login.getData().getIs_merchant()));
                                SharedPreference.saveSimpleString(context, Constants.language, user.getLang());


                                Intent i = new Intent(context, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(i);

                                changeLocale(user.getLang());

//                                SharedPreference.saveSimpleString(context, Constants.language, login.getData().getLang());
//                                changeLocale(login.getData().getLang());

                                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                                databaseHelper.dropCart();
                                databaseHelper.close();
                            } else
                                showToast((AppCompatActivity) context, response.body().getMessage(), true);


                        } else
                            showToast((AppCompatActivity) context, login.getMessage(), false);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (!jsonObject.getJSONObject("data").toString().equals("{}")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            int phone_verified = 0, email_verified = 0, is_active = 0;
                            if (data.has("phone")) {
                                phone_verified = data.getInt("phone_verified");
                                if (phone_verified == 0) {
                                    String phoneNo = data.getString("phone");
                                    Intent i = new Intent(context, OtpConfirmation.class);
                                    i.putExtra("phoneNo", phoneNo);
                                    context.startActivity(i);
                                }
                            } else if (data.has("email")) {
                                email_verified = data.getInt("email_verified");
                                if (email_verified == 0) {
                                    String email = data.getString("email");
                                    Intent i = new Intent(context, EmailConfirmation.class);
                                    i.putExtra("email", email);
                                    context.startActivity(i);
                                }
                            } else if (data.has("is_active")) {
                                is_active = data.getInt("is_active");
                                if (is_active == 0) {
                                    showToast((AppCompatActivity) context, jsonObject.getString("message"), false);
                                }
                            }

                            String message = jsonObject.getString("message");
                            if (!message.isEmpty())
                                showToast((AppCompatActivity) context, message, false);

                        } else {
                            String message = jsonObject.getString("message");
                            if (!message.isEmpty())
                                showToast((AppCompatActivity) context, message, false);
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                dissmissDialog();
                Log.d("Login Failure", "onFailure: " + t.getMessage());
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void changeLocale(String language) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);
    }

    public void StopListener() {
        try {
            if (cancellationSignal != null)
                cancellationSignal.cancel();
            cancellationSignal = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
    }

    public void completeFingerAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        } catch (SecurityException ex) {
            Log.d(TAG, "An error occurred:\n" + ex.getMessage());
        } catch (Exception ex) {
            Log.d(TAG, "An error occurred\n" + ex.getMessage());
        }
    }

}
