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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText etForgotPasswordEmail;
    private ConstraintLayout btnSendEmailForgotPassword;
    private ImageView ivForgotPasswordBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();
    }

    //Initializing the views
    private void initViews() {
        ivForgotPasswordBack = findViewById(R.id.ivForgotPasswordBack);
        ivForgotPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        etForgotPasswordEmail = findViewById(R.id.etForgotPasswordEmail);
        btnSendEmailForgotPassword = findViewById(R.id.btnSendEmailForgotPassword);
        btnSendEmailForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSendEmailForgotPassword) {
            Common.hideKeyboard(ForgotPassword.this);
            validate();
        }

    }

    private void validate() {

        if (etForgotPasswordEmail.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.forgotPasswordErrorMessageEnterEmail), false);
            return;
        }

        Common.showDialog(ForgotPassword.this);
        callForgotApi();


    }

    //Calling forgot password api
    private void callForgotApi() {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> forgotPassword = api.forgotPassword(Splash.appLanguage, etForgotPasswordEmail.getText().toString());

        forgotPassword.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.has("status")) {
                            boolean status = jsonObject.getBoolean("status");
                            if (status) {
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    showToast(ForgotPassword.this, message, true);

                                    JSONObject data=jsonObject.getJSONObject("data");
                                    String emailOrPhone = data.getString("email");

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            Intent intent = new Intent(ForgotPassword.this, ResetPassword.class);
                                            intent.putExtra("email", emailOrPhone);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 1800);


                                }
                            }
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.has("message")) {
                            String message = jsonObject.getString("message");
                            showToast(ForgotPassword.this, message, false);
                            Common.hideKeyboard(ForgotPassword.this);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("ForgotPassword", "onFailure: " + t.getMessage());
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
