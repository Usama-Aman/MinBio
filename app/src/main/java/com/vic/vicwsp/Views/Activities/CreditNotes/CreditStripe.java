package com.vic.vicwsp.Views.Activities.CreditNotes;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.vic.vicwsp.Views.Activities.StripeActivity;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Utils.Constants.mLastClickTime;

public class CreditStripe extends AppCompatActivity {

    private Stripe stripe;
    private CardMultilineWidget cardMultilineWidget;
    private ConstraintLayout btnConfirmCard;
    private ImageView back;
    private int creditNoteId = 0;
    private ProgressDialog dialog;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);

        creditNoteId = getIntent().getIntExtra("creditNoteId", 0);

        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(view -> finish());
//        stripe = new Stripe(CreditStripe.this, getResources().getString(R.string.stripe__key));
        stripe = new Stripe(CreditStripe.this, getResources().getString(R.string.test_stripe__key));
        cardMultilineWidget = findViewById(R.id.card_multiline_widget);


        btnConfirmCard = findViewById(R.id.btnOkStripe);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getResources().getString(R.string.loading));
        dialog.setMessage(getResources().getString(R.string.loading_please_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        btnConfirmCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                final Card cardToSave = cardMultilineWidget.getCard();

                if (cardToSave == null) {
                    showToast(CreditStripe.this, getResources().getString(R.string.invalid_card), false);
                    return;
                }
                Common.hideKeyboard(CreditStripe.this);
                dialog.show();

                stripe.createToken(cardToSave, new ApiResultCallback<Token>() {
                    public void onSuccess(Token token) {

                        doPayment(token.getId());
                        token = null;
                    }

                    public void onError(Exception error) {
                        Log.d("Stripe Activity", "onError: " + error.getLocalizedMessage());
                        showToast(CreditStripe.this, error.getLocalizedMessage(), false);
                        dialog.dismiss();
                    }
                });
            }
        });

        ImageView headerLogo = findViewById(R.id.headerLogo);
        headerLogo.setOnClickListener(v -> {
            Common.hideKeyboard(this);
            Common.goToMain(this);
        });
    }

    public void doPayment(String cardId) {

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> call = api.postCreditPayment("Bearer " +
                SharedPreference.getSimpleString(CreditStripe.this, Constants.accessToken), creditNoteId, cardId, 0);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                try {

                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        alertDialog = new AlertDialog.Builder(CreditStripe.this)
                                .setCancelable(false)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();
                                    Common.goToMain(CreditStripe.this);

                                })
                                .show();
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(CreditStripe.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
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
