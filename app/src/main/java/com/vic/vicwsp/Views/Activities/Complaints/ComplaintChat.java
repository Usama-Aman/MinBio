package com.vic.vicwsp.Views.Activities.Complaints;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.Complaints.ComplaintChatListAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.Complaint.ComplaintChat.ComplaintChatData;
import com.vic.vicwsp.Models.Response.Complaint.ComplaintChat.ComplaintChatResponse;
import com.vic.vicwsp.Models.Response.Complaint.ComplaintChat.ComplaintSingleChatResponse;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Constants.IMAGE_REQUEST_CODE;
import static com.vic.vicwsp.Utils.Constants.mLastClickTime;

public class ComplaintChat extends AppCompatActivity implements View.OnClickListener {


    private ConstraintLayout btnAddPicture, btnSendMessage;
    private EditText etMessage;

    private LinearLayoutManager layoutManager;
    private ArrayList<ComplaintChatData> datumArrayList = new ArrayList<>();
    private RecyclerView complainDetailRecycler;
    private ComplaintChatListAdapter chatAdapter;
    private ImageView chatBack;
    private TextView complainCode, complainDate;
    private ProgressBar sendingProgressBar;


    public static boolean isComplaintChatActive = false;
    private boolean isSendingMessage = false;

    private String strComplaintType = "";
    private int complaintOrderId = 0;

    private TextView tvComplaintType, complaintOrderNo;


    public static int complaintId = 0;
    //  private boolean fromNotification = false;


    private BroadcastReceiver chatBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("ComplaintChatBroadCast")) {
                getMessageFromApi();
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_detail);
        LocalBroadcastManager.getInstance(ComplaintChat.this).registerReceiver(chatBroadcastReceiver, new IntentFilter("ComplaintChatBroadCast"));


        isComplaintChatActive = true;

        if (getIntent().hasExtra("complaintId"))
            if (AppUtils.isSet(getIntent().getStringExtra("complaintId")))
                complaintId = Integer.parseInt(getIntent().getStringExtra("complaintId"));

        if (getIntent().hasExtra("complaintType"))
            if (AppUtils.isSet(getIntent().getStringExtra("complaintType")))
                strComplaintType = (getIntent().getStringExtra("complaintType"));


        if (getIntent().hasExtra("orderId"))
            complaintOrderId = (getIntent().getIntExtra("orderId", 0));


        //fromNotification = getIntent().getBooleanExtra("isComingFromNotification", false);


        if (complaintId > 0) {
            initViews();
            setAdapter();
        } else {
            Common.showToast(ComplaintChat.this, getResources().getString(R.string.something_is_not_right), false);
            Handler handler = new Handler();
            handler.postDelayed(() -> finish(), 1200);
        }
    }

    private void initViews() {

        try {


            complainCode = findViewById(R.id.complainCode);
            complainDate = findViewById(R.id.complainDate);
            tvComplaintType = findViewById(R.id.complainType);
            complaintOrderNo = findViewById(R.id.complainOrderNo);

            complaintOrderNo.setText(getResources().getString(R.string.complaintOrderNo) + " " + String.valueOf(complaintOrderId));
            tvComplaintType.setText(getResources().getString(R.string.complaintType) + " " + strComplaintType);


            chatBack = findViewById(R.id.ivToolbarBack);
            chatBack.setOnClickListener(view -> finish());


            complainDetailRecycler = findViewById(R.id.complainDetailRecycler);
            btnAddPicture = findViewById(R.id.btnAddPicture);
            btnAddPicture.setOnClickListener(this);
            btnSendMessage = findViewById(R.id.btnSendMessage);
            btnSendMessage.setOnClickListener(this);
            btnSendMessage.setVisibility(View.GONE);

            etMessage = findViewById(R.id.etMessage);


            sendingProgressBar = findViewById(R.id.sendingProgressBar);


            etMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (!etMessage.getText().toString().equals("") && !etMessage.getText().toString().isEmpty() && !etMessage.getText().toString().trim().isEmpty()) {
                        btnAddPicture.setVisibility(View.VISIBLE);

                        btnSendMessage.setVisibility(View.VISIBLE);
                    } else {
                        btnSendMessage.setVisibility(View.GONE);
                        btnAddPicture.setVisibility(View.VISIBLE);
                        if (!isSendingMessage) {
                            sendingProgressBar.setVisibility(View.GONE);
                        } else {
                            sendingProgressBar.setVisibility(View.VISIBLE);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        chatAdapter = new ComplaintChatListAdapter(ComplaintChat.this, datumArrayList);
        layoutManager = new LinearLayoutManager(ComplaintChat.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setStackFromEnd(false);
        complainDetailRecycler.setLayoutManager(layoutManager);
        complainDetailRecycler.setAdapter(chatAdapter);
        complainDetailRecycler.smoothScrollToPosition(0);

        Common.showDialog(ComplaintChat.this);
        getMessageFromApi();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSendMessage:
                handleSendingMessage();
                break;
            case R.id.btnAddPicture:

                if (etMessage.getText().toString().isEmpty() || etMessage.getText().toString().equals("") || etMessage.getText().toString().trim().isEmpty()) {
                    Common.showToast(ComplaintChat.this, getResources().getString(R.string.msg_please_add_comment_first), false);
                    return;
                }

                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, IMAGE_REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    private void handleSendingMessage() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (etMessage.getText().toString().isEmpty() || etMessage.getText().toString().equals("") || etMessage.getText().toString().trim().isEmpty())
            return;
        else {
            etMessage.setEnabled(false);
            postMessage(etMessage.getText().toString(), filePic);
            isSendingMessage = true;
            etMessage.setText("");
        }
    }


    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(ComplaintChat.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ComplaintChat.this, new String[]{permission}, requestCode);
        } else {
            if (requestCode == IMAGE_REQUEST_CODE)
                getImage();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImage();
            }
        }
    }

    private void getImage() {
        ImagePicker.Companion.with(this)
                .crop(1f, 1f)
                .compress(800)
                .maxResultSize(1080, 1080)
                .start(2000);
    }

    File filePic = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2000) {
                Uri fileUri = data.getData();
                if (fileUri != null) {

                    filePic = null;
                    filePic = new File(fileUri.getPath());

                    handleSendingMessage();

                }
            }
        }
    }

    private void getMessageFromApi() {
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ComplaintChatResponse> call = api.getComplaintMessages("Bearer " + SharedPreference.getSimpleString(ComplaintChat.this, Constants.accessToken),
                complaintId);

        call.enqueue(new Callback<ComplaintChatResponse>() {
            @Override
            public void onResponse(Call<ComplaintChatResponse> call, Response<ComplaintChatResponse> response) {
                Common.dissmissDialog();
                if (datumArrayList != null && datumArrayList.size() != 0) {
                    datumArrayList.clear();
                    chatAdapter.notifyDataSetChanged();
                }
                try {
                    if (response.isSuccessful()) {

                        if (response.body().getComplaintChatList().size() > 0) {

                            int p = 0;

                            if (datumArrayList.size() > 0) {
                                datumArrayList.remove(0);
                                chatAdapter.notifyItemRemoved(0);

                                p = datumArrayList.size() - 1;
                                if (p < 0) {
                                    p = 0;
                                }

                            }


                            if (datumArrayList.size() > 0)
                                datumArrayList.addAll(0, response.body().getComplaintChatList());
                            else
                                datumArrayList.addAll(response.body().getComplaintChatList());


                            complainCode.setText(datumArrayList.get(0).getComplaintChatDetail().getComplaintNo());
                            complainDate.setText(AppUtils.formatDateFromDateTime(datumArrayList.get(0).getComplaintChatDetail().getCreatedAt()));

                            chatAdapter.notifyDataSetChanged();


                            complainDetailRecycler.scrollToPosition(0);
                            complainDetailRecycler.setVisibility(View.VISIBLE);

                        } else {
                            showNullMessage();
                        }

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(ComplaintChat.this, jsonObject.getString("message"), false);
                        showNullMessage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showNullMessage();
                }

            }

            @Override
            public void onFailure(Call<ComplaintChatResponse> call, Throwable t) {
                Common.dissmissDialog();
                showNullMessage();
            }
        });

    }

    private void showNullMessage() {
        complainDetailRecycler.setVisibility(View.GONE);
    }


    private void postMessage(String message, File file) {


        sendingProgressBar.setVisibility(View.VISIBLE);

        Api api = RetrofitClient.getClient().create(Api.class);
        RequestBody rbMessage = null;

        RequestBody rbImage = null;
        MultipartBody.Part mbImage = null;
        Call<ComplaintSingleChatResponse> call = null;


        RequestBody rbComplaintId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(complaintId));
        rbMessage = RequestBody.create(MediaType.parse("text/plain"), message);

        if (file != null) {
            rbImage = RequestBody.create(MediaType.parse("image/*"), file);
            mbImage = MultipartBody.Part.createFormData("complaint_photo", file.getName(), rbImage);

            call = api.postComplaintMessageFile("Bearer " + SharedPreference.getSimpleString(ComplaintChat.this, Constants.accessToken),
                    rbComplaintId, rbMessage, mbImage);
        } else {
            call = api.postComplaintMessage("Bearer " + SharedPreference.getSimpleString(ComplaintChat.this, Constants.accessToken),
                    rbComplaintId, rbMessage);
        }

        call.enqueue(new Callback<ComplaintSingleChatResponse>() {
            @Override
            public void onResponse(Call<ComplaintSingleChatResponse> call, Response<ComplaintSingleChatResponse> response) {
                etMessage.setEnabled(true);
                isSendingMessage = false;
                sendingProgressBar.setVisibility(View.GONE);
                btnSendMessage.setVisibility(View.GONE);

                try {
                    if (response.isSuccessful()) {

                        etMessage.setText("");
                        filePic = null;
                        ComplaintChatData d = new ComplaintChatData(response.body().getComplaintChatData().getId(), response.body().getComplaintChatData().getComplaintId(), response.body().getComplaintChatData().getComment(),
                                response.body().getComplaintChatData().getUserId(), response.body().getComplaintChatData().getCommentFrom(), response.body().getComplaintChatData().getComplaintPhoto(), response.body().getComplaintChatData().getCreatedAt(),
                                response.body().getComplaintChatData().getUpdatedAt(), response.body().getComplaintChatData().getImagePath(), response.body().getComplaintChatData().getUserDetail(), response.body().getComplaintChatData().getAdminDetail(),
                                response.body().getComplaintChatData().getComplaintChatDetail());

                        if (!checkIfAlreadyExist(response.body().getComplaintChatData().getId())) {
                            datumArrayList.add(d);
                            chatAdapter.notifyDataSetChanged();
                            complainDetailRecycler.smoothScrollToPosition(datumArrayList.size());
                        }

                        complainDetailRecycler.setVisibility(View.VISIBLE);
                        complainDetailRecycler.scrollToPosition(datumArrayList.size() - 1);

                    } else {


                        if (!etMessage.getText().toString().isEmpty() && !etMessage.getText().toString().equals("") && !etMessage.getText().toString().trim().isEmpty())
                            btnSendMessage.setVisibility(View.VISIBLE);


                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(ComplaintChat.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ComplaintSingleChatResponse> call, Throwable t) {
                etMessage.setEnabled(true);
                isSendingMessage = false;
                btnSendMessage.setVisibility(View.GONE);
                sendingProgressBar.setVisibility(View.GONE);

                if (!etMessage.getText().toString().isEmpty() && !etMessage.getText().toString().equals("") && !etMessage.getText().toString().trim().isEmpty())
                    btnSendMessage.setVisibility(View.VISIBLE);

            }
        });

    }

    private boolean checkIfAlreadyExist(int id) {
        boolean isExist = false;
        for (int i = 0; i < datumArrayList.size(); i++) {
            if (datumArrayList.get(i).getId() == id)
                isExist = true;
        }
        return isExist;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(ComplaintChat.this).unregisterReceiver(chatBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isComplaintChatActive = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isComplaintChatActive = false;

    }


    @Override
    protected void onStop() {
        super.onStop();
    }


}
