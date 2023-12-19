package com.vic.vicwsp.Views.Activities.Complaints;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Controllers.Helpers.CustomSearchableSpinner;
import com.vic.vicwsp.Controllers.Helpers.MultiSelectSearchSpinner;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.SendComplaintResponse.ComplaintModel;
import com.vic.vicwsp.Models.Response.SendComplaintResponse.Item;
import com.vic.vicwsp.Models.Response.SendComplaintResponse.Order;
import com.vic.vicwsp.Models.Response.SendComplaintResponse.Type;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendComplaint extends AppCompatActivity implements View.OnClickListener {

    private ImageView photo1, photo2, photo3, upl1, upl2, upl3, cross1, cross2, cross3;
    private AlertDialog alertDialog;
    private String path1 = "", path2 = "", path3 = "";
    private CustomSearchableSpinner orderSpinner, complaintSpinner;

    private ArrayList<Type> complaintTypes = new ArrayList<>();
    private ArrayList<Item> complaintItems = new ArrayList<>();
    private ArrayList<Order> complaintOrders = new ArrayList<>();

    private ConstraintLayout itemsSpinnerConstraint;
    private TextView itemsSpinnerText;
    private EditText etComment;

    private int orderNumberId = 0;
    private String itemsId = "";
    private int complaintId = 0;

    private ImageView sendComplaint, back, complaintCross;
    public static boolean isSendComplaintOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        isSendComplaintOpen = true;
        initViews();

    }

    private void initViews() {
        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(v -> finish());
        complaintCross = findViewById(R.id.complaintCross);
        complaintCross.setOnClickListener(v -> finish());
        sendComplaint = findViewById(R.id.complaintSend);
        sendComplaint.setOnClickListener(v -> validate());
        etComment = findViewById(R.id.etComplaintComment);
        orderSpinner = findViewById(R.id.complaintOrderSpinner);
        complaintSpinner = findViewById(R.id.complaintSpinner);
        itemsSpinnerConstraint = findViewById(R.id.itemsConstraintSpinner);
        itemsSpinnerText = findViewById(R.id.itemsComplaintText);
        itemsSpinnerConstraint.setEnabled(false);

        cross1 = findViewById(R.id.cross1);
        cross1.setOnClickListener(this);
        cross2 = findViewById(R.id.cross2);
        cross2.setOnClickListener(this);
        cross3 = findViewById(R.id.cross3);
        cross3.setOnClickListener(this);

        photo1 = findViewById(R.id.photo1);
        upl1 = findViewById(R.id.upl1);
        photo1.setOnClickListener(this);
        photo2 = findViewById(R.id.photo2);
        upl2 = findViewById(R.id.upl2);
        photo2.setOnClickListener(this);
        photo3 = findViewById(R.id.photo3);
        upl3 = findViewById(R.id.upl3);
        photo3.setOnClickListener(this);

        ImageView headerLogo = findViewById(R.id.headerLogo);
        headerLogo.setOnClickListener(v -> {
            Common.hideKeyboard(this);
            Common.goToMain(this);
        });


        itemsSpinnerConstraint.setOnClickListener(v -> {

            MultiSelectSearchSpinner multiSelectSearchSpinner = new MultiSelectSearchSpinner(this, complaintItems);
            multiSelectSearchSpinner.setOnworkflowlistclicklistener(results -> {
                itemsId = TextUtils.join(",", results);

                ArrayList<String> itemsStr = new ArrayList<>();
                for (int i = 0; i < complaintItems.size(); i++) {
                    for (int j = 0; j < results.size(); j++) {
                        if (results.get(j) == complaintItems.get(i).getId()) {
                            itemsStr.add(complaintItems.get(i).getValue());
                        }
                    }
                }
                itemsSpinnerText.setText(TextUtils.join(",", itemsStr));
            });
            multiSelectSearchSpinner.show();
        });

        getDatafromApi();
    }

    private void getDatafromApi() {
        Common.showDialog(this);
        Call<ComplaintModel> call = RetrofitClient.getClient().create(Api.class).getComplaintData("Bearer " +
                SharedPreference.getSimpleString(this, Constants.accessToken));

        call.enqueue(new Callback<ComplaintModel>() {
            @Override
            public void onResponse(Call<ComplaintModel> call, Response<ComplaintModel> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        complaintOrders = response.body().getData().getOrders();
                        complaintTypes = response.body().getData().getTypes();

                        setUpOrdersSpinner();

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(SendComplaint.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ComplaintModel> call, Throwable t) {
                Log.d("", "onFailure: " + t.getMessage());
                Common.dissmissDialog();
            }
        });

    }

    private void setUpOrdersSpinner() {

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < complaintOrders.size(); i++)
            strings.add(complaintOrders.get(i).getValue());

        orderSpinner.setPositiveButton(getResources().getString(R.string.close_spinner));
        orderSpinner.setTitle(getResources().getString(R.string.select_order_no_complaint));

        ArrayAdapter orderSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        orderSpinner.setAdapter(orderSpinnerAdapter);

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                orderNumberId = complaintOrders.get(adapterView.getSelectedItemPosition()).getId();

                for (int j = 0; j < complaintOrders.size(); j++) {
                    if (orderNumberId == complaintOrders.get(j).getId()) {
                        complaintItems.clear();
                        complaintItems.addAll(complaintOrders.get(j).getItems());
                        itemsSpinnerConstraint.setEnabled(true);

                        itemsSpinnerText.setText("");
                        itemsId = "";

                        for (int k = 0; k < complaintItems.size(); k++) {
                            complaintItems.get(k).setChecked(false);
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> complaints = new ArrayList<>();
        for (int i = 0; i < complaintTypes.size(); i++)
            complaints.add(complaintTypes.get(i).getValue());

        complaintSpinner.setPositiveButton(getResources().getString(R.string.close_spinner));
        complaintSpinner.setTitle(getResources().getString(R.string.select_complaint_type_complaint));

        ArrayAdapter complaintAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, complaints);
        complaintSpinner.setAdapter(complaintAdapter);

        complaintSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                complaintId = complaintTypes.get(adapterView.getSelectedItemPosition()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo1:
                ImagePicker.Companion.with(this)
                        .crop(1f, 1f)
                        .compress(800)
                        .maxResultSize(1080, 1080)
                        .start(201);
                break;
            case R.id.photo2:
                ImagePicker.Companion.with(this)
                        .crop(1f, 1f)
                        .compress(800)
                        .maxResultSize(1080, 1080)
                        .start(202);
                break;
            case R.id.photo3:
                ImagePicker.Companion.with(this)
                        .crop(1f, 1f)
                        .compress(800)
                        .maxResultSize(1080, 1080)
                        .start(203);
                break;
            case R.id.cross1:

                alertDialog = new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.delete_picture))
                        .setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                            path1 = "";
                            cross1.setVisibility(View.GONE);
                            upl1.setVisibility(View.VISIBLE);
                            photo1.setImageDrawable(null);
                            photo1.setEnabled(true);
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> {
                            alertDialog.dismiss();
                        })
                        .show();
                break;
            case R.id.cross2:

                alertDialog = new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.delete_picture))
                        .setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                            path2 = "";
                            cross2.setVisibility(View.GONE);
                            upl2.setVisibility(View.VISIBLE);
                            photo2.setImageDrawable(null);
                            photo2.setEnabled(true);
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> {
                            alertDialog.dismiss();
                        })
                        .show();
                break;
            case R.id.cross3:

                alertDialog = new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.delete_picture))
                        .setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                            path3 = "";
                            cross3.setVisibility(View.GONE);
                            upl3.setVisibility(View.VISIBLE);
                            photo3.setImageDrawable(null);
                            photo3.setEnabled(true);
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> {
                            alertDialog.dismiss();
                        })
                        .show();
                break;
            default:
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 201) {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    path1 = fileUri.getPath();
                    File file = new File(fileUri.getPath());
                    Glide.with(this).load(file).into(photo1);
                    upl1.setVisibility(View.GONE);
                    cross1.setVisibility(View.VISIBLE);
                    photo1.setEnabled(false);
                }
            } else if (requestCode == 202) {
                Uri fileUri = data.getData();
                if (fileUri != null) {

                    path2 = fileUri.getPath();
                    File file = new File(fileUri.getPath());
                    Glide.with(this).load(file).into(photo2);
                    upl2.setVisibility(View.GONE);
                    cross2.setVisibility(View.VISIBLE);
                    photo2.setEnabled(false);

                }
            } else if (requestCode == 203) {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    path3 = fileUri.getPath();
                    File file = new File(fileUri.getPath());
                    Glide.with(this).load(file).into(photo3);

                    upl3.setVisibility(View.GONE);
                    cross3.setVisibility(View.VISIBLE);
                    photo3.setEnabled(false);
                }
            }
        }
    }


    private void validate() {
        if (itemsId.equals("")) {
            Common.showToast(SendComplaint.this, getResources().getString(R.string.select_items_complaint_error), false);
            return;
        }
        if (etComment.getText().toString().isEmpty() || etComment.getText().toString().equals("")) {
            Common.showToast(SendComplaint.this, getResources().getString(R.string.enter_comments_complaint), false);
            return;
        }
        postData();
    }

    /*Posting complaint data to server*/
    private void postData() {
        Common.showDialog(this);

        RequestBody rbOrderId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(orderNumberId));
        RequestBody rbItemIds = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(itemsId));
        RequestBody rbComplaintId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(complaintId));
        RequestBody rbComment = RequestBody.create(MediaType.parse("text/plain"), etComment.getText().toString());
        RequestBody rbIsCreditChecked = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));

        List<String> p = new ArrayList<>();

        if (!path1.equals(""))
            p.add(path1);
        if (!path2.equals(""))
            p.add(path2);
        if (!path3.equals(""))
            p.add(path3);

        MultipartBody.Part[] multipartBodies = new MultipartBody.Part[p.size()];

        if (p.size() > 0)
            for (int i = 0; i < p.size(); i++) {
                if (!p.get(0).equals("")) {
                    File file = new File(p.get(i));
                    RequestBody rbFile = RequestBody.create(MediaType.parse("images/*"), file);
                    multipartBodies[i] = MultipartBody.Part.createFormData("files[]", file.getName(), rbFile);
                }
            }

        Call<ResponseBody> call = RetrofitClient.getClient().create(Api.class).postComplaintData("Bearer " +
                        SharedPreference.getSimpleString(this, Constants.accessToken), rbOrderId, rbItemIds,
                rbComplaintId, rbComment, rbIsCreditChecked, multipartBodies);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Common.showToast(SendComplaint.this, jsonObject.getString("message"), true);

                        final Handler handler = new Handler();
                        handler.postDelayed(() -> finish(), 1200);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(SendComplaint.this, jsonObject.getString("message"), false);
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
