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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class OtpConfirmation extends AppCompatActivity implements View.OnClickListener {


    private OtpTextView otpTextView;
    private ConstraintLayout btnToLoginOtp, btnSendMeBack;
    private String numberToSendOTPAgain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_confirmation);
        initViews();
        autoReadOtp();
    }

    //Initializing the views

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        numberToSendOTPAgain = bundle.getString("phoneNo");
        otpTextView = findViewById(R.id.otp_view);
        btnToLoginOtp = findViewById(R.id.btnToLoginOtp);
        btnToLoginOtp.setOnClickListener(this);
        btnSendMeBack = findViewById(R.id.btnSendMeBack);
        btnSendMeBack.setOnClickListener(this);
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            @Override
            public void onOTPComplete(String otp) {
                Common.hideKeyboard(OtpConfirmation.this);
                Common.showDialog(OtpConfirmation.this);
                callOtpApi();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.btnSendMeBack:
                Common.showDialog(OtpConfirmation.this);
                callSendMeBackOptApi();
                break;
            case R.id.btnToLoginOtp:
                if (otpTextView.getOTP().isEmpty() || otpTextView.getOTP().length() < 4) {
                    showToast(OtpConfirmation.this, getResources().getString(R.string.OtpConfirmationErrorMessagePlease_enter_the_verification_code), false);
                } else {
                    Common.showDialog(OtpConfirmation.this);
                    callOtpApi();
                }
                break;
            default:
                break;
        }
    }

    //Calling the resend api for otp

    private void callSendMeBackOptApi() {
        Api api = RetrofitClient.getClient().create(Api.class);
        final Call<ResponseBody> resendOtp = api.resendOtp(Splash.appLanguage, numberToSendOTPAgain);
        resendOtp.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean status;
                        if (jsonObject.has("status")) {
                            status = jsonObject.getBoolean("status");
                            if (status) {
                                String message = jsonObject.getString("message");
                                if (!message.isEmpty())
                                    showToast(OtpConfirmation.this, message, true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });
    }
    //calling the first time api of the otp

    private void callOtpApi() {

        Api api = RetrofitClient.getClient().create(Api.class);
        Log.d("", "callOtpApi: " + otpTextView.getOTP());
        Call<ResponseBody> confirmOtp = api.confirmOtp(Splash.appLanguage, numberToSendOTPAgain,
                otpTextView.getOTP());

        Log.d("OTP", "callOtpApi: " + SharedPreference.getSimpleString(getApplicationContext(), Constants.userPhone) +
                otpTextView.getOTP());

        confirmOtp.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        boolean status = false;
                        if (jsonObject.has("status")) {
                            status = jsonObject.getBoolean("status");
                            if (status) {

                                showToast(OtpConfirmation.this, jsonObject.getString("message"), true);

                                Intent i = new Intent(OtpConfirmation.this, SignIn.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(i);
                                    }
                                }, 1800);
                            }
                        }
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.has("message")) {
                            String message = jsonObject.getString("message");
                            if (!message.isEmpty())
                                showToast(OtpConfirmation.this, jsonObject.getString("message"), false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Otp Confirmation", "onFailure: " + t.getMessage());
            }
        });
    }

    //    Method to start the auto read otp service
    private void autoReadOtp() {

        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent

//                Toast.makeText(OtpConfirmation.this, "Listening", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
//                Toast.makeText(OtpConfirmation.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
