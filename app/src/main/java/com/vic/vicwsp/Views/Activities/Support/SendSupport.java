package com.vic.vicwsp.Views.Activities.Support;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.Support.SupportResponse.Datum;
import com.vic.vicwsp.Models.Response.Support.SupportResponse.TopicsModel;
import com.vic.vicwsp.Models.User;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import pl.utkala.searchablespinner.SearchableSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendSupport extends AppCompatActivity {

    private ImageView supportCross;
    private EditText etSupportEmail;
    private ImageView back, supportSend, supportCall;
    private SearchableSpinner topicsSpinner;

    private ArrayList<Datum> topics = new ArrayList<>();
    private int topicId = 0;
    private EditText etComment;
    private AlertDialog alertDialog;
    public static boolean isSendSupportOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        isSendSupportOpen = true;
        initViews();
    }

    private void initViews() {
        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(v -> finish());
        supportCall = findViewById(R.id.supportCall);
        supportCall.setOnClickListener(v -> {

            alertDialog = new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.are_you_to_call) + "  " + SharedPreference.getSimpleString(this, Constants.companyPhone))
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                        checkCallPermission();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
                        alertDialog.dismiss();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
        etComment = findViewById(R.id.etSupportComment);
        etSupportEmail = findViewById(R.id.etSupportEmail);
        supportCross = findViewById(R.id.supportCross);
        supportCross.setOnClickListener(v -> finish());
        supportSend = findViewById(R.id.supportSend);
        supportSend.setOnClickListener(v -> validate());
        topicsSpinner = findViewById(R.id.topicsSpinner);

        ImageView headerLogo = findViewById(R.id.headerLogo);
        headerLogo.setOnClickListener(v -> {
            Common.hideKeyboard(this);
            Common.goToMain(this);
        });
        try {
            DatabaseHelper db = new DatabaseHelper(this);
            User user = db.getUser(Integer.parseInt(SharedPreference.getSimpleString(this, Constants.userId)));
            if (user.getEmail() != null) {
                etSupportEmail.setText(user.getEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        back.setOnClickListener(v -> finish());
        getDatafromApi();
    }

    private void checkCallPermission() {

        if (ContextCompat.checkSelfPermission(SendSupport.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SendSupport.this, new String[]{Manifest.permission.CALL_PHONE}, 299);
        } else {
            openPhone();
        }
    }

    private void openPhone() {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + SharedPreference.getSimpleString(SendSupport.this,
                    Constants.companyPhone)));
            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 299) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPhone();
            }
        }
    }

    /*Getting support data from api*/
    private void getDatafromApi() {
        Common.showDialog(this);
        Call<TopicsModel> call = RetrofitClient.getClient().create(Api.class).getSupportData("Bearer " +
                SharedPreference.getSimpleString(this, Constants.accessToken));

        call.enqueue(new Callback<TopicsModel>() {
            @Override
            public void onResponse(Call<TopicsModel> call, Response<TopicsModel> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        topics.clear();
                        topics.addAll(response.body().getData());

                        setUpTopicSpinner();

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(SendSupport.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TopicsModel> call, Throwable t) {
                Log.d("", "onFailure: " + t.getMessage());
                Common.dissmissDialog();
            }
        });

    }

    /*Setting up support spinner values*/
    private void setUpTopicSpinner() {

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < topics.size(); i++)
            strings.add(topics.get(i).getValue());

        topicsSpinner.setDismissText(getResources().getString(R.string.close_spinner));
        topicsSpinner.setDialogTitle(getResources().getString(R.string.select_topic_support));

        ArrayAdapter topicsSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        topicsSpinner.setAdapter(topicsSpinnerAdapter);

        topicsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                topicId = topics.get(adapterView.getSelectedItemPosition()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    /*Touch event function when click outside hide the keyboard*/
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

    private void validate() {
        if (etComment.getText().toString().isEmpty() || etComment.getText().toString().equals("")) {
            Common.showToast(SendSupport.this, getResources().getString(R.string.enter_issue_support), false);
            return;
        }
        postData();
    }

    private void postData() {
        Common.showDialog(this);
        Call<ResponseBody> call = RetrofitClient.getClient().create(Api.class).postSupportData("Bearer " +
                        SharedPreference.getSimpleString(this, Constants.accessToken), topicId,
                etSupportEmail.getText().toString(), etComment.getText().toString(), "User");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Common.showToast(SendSupport.this, jsonObject.getString("message"), true);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1200);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(SendSupport.this, jsonObject.getString("message"), false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("", "onFailure: " + t.getMessage());
                Common.dissmissDialog();
            }
        });

    }


}
