package com.vic.vicwsp.Views.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.vic.vicwsp.Controllers.Helpers.ChatAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.Chat.ChatModel;
import com.vic.vicwsp.Models.Response.Chat.Datum;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Constants.AUDIO_REQUEST_CODE;
import static com.vic.vicwsp.Utils.Constants.IMAGE_REQUEST_CODE;
import static com.vic.vicwsp.Utils.Constants.mLastClickTime;

public class Seller2Driver extends AppCompatActivity implements View.OnClickListener {

    private RecordButton recordButton;
    private RecordView recordView;
    private ConstraintLayout btnAddPicture, btnSendMessage;
    private EditText etMessage;
    private MediaRecorder mediaRecorder;
    private File audioFile;
    private LinearLayoutManager layoutManager;
    private RecyclerView b2dChatRecycler;
    private ImageView chatBack, ivListNull, headerLogo;
    private TextView tvListNull, chatTitle, chatOrderNumber;
    private ProgressBar sendingProgressBar;
    private ArrayList<Datum> datumArrayList = new ArrayList<>();
    private RecyclerView chatRecycler;
    private ChatAdapter chatAdapter;

    private boolean isLoading = false;
    private int CURRENT_PAGE = 0;
    private int LAST_PAGE = 0;
    public static int sellerDriverOrderId = 0;

    public static boolean iss2dChatActive = false;
    private boolean isSendingMessage = false;
    private BroadcastReceiver chatBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Seller2DriverBroadCast")) {
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("messageData"));

                    if (sellerDriverOrderId == jsonObject.getInt("order_id")) {

                        String orderNumber = intent.getStringExtra("orderNumber");
                        chatOrderNumber.setText(getResources().getString(R.string.chatOrderNumber,orderNumber));

                        Datum d = new Datum(jsonObject.getInt("id"), jsonObject.getInt("chat_id"),
                                jsonObject.getInt("sender_id"), jsonObject.getInt("receiver_id"), jsonObject.getString("type"),
                                jsonObject.getString("message"), jsonObject.getInt("is_delivered"), jsonObject.getInt("is_seen"),
                                jsonObject.getString("date"), jsonObject.getString("file_path"), jsonObject.getString("duration"));

                        if (!jsonObject.getString("user_name").equals(""))
                            chatTitle.setText(jsonObject.getString("user_name"));

                        if (!checkIfAlreadyExist(jsonObject.getInt("id"))) {
                            datumArrayList.add(d);
                            chatAdapter.notifyDataSetChanged();
                            chatRecycler.smoothScrollToPosition(datumArrayList.size());
                        }

                        chatRecycler.setVisibility(View.VISIBLE);
                        tvListNull.setVisibility(View.GONE);
                        ivListNull.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_s2d);

        LocalBroadcastManager.getInstance(Seller2Driver.this).registerReceiver(chatBroadcastReceiver, new IntentFilter("Seller2DriverBroadCast"));
        iss2dChatActive = true;

        sellerDriverOrderId = getIntent().getIntExtra("orderId", 0);

        if (sellerDriverOrderId > 0) {
            initViews();
            setAdapter();
        } else {
            Common.showToast(Seller2Driver.this, getResources().getString(R.string.something_is_not_right), false);
            Handler handler = new Handler();
            handler.postDelayed(() -> finish(), 1200);
        }
    }

    private void initViews() {
        try {
            ImageView headerLogo = findViewById(R.id.headerLogo);
            headerLogo.setOnClickListener(v -> {
                Common.hideKeyboard(this);
                Common.goToMain(this);
            });
            chatTitle = findViewById(R.id.chatTitle);
            chatTitle.setText(getIntent().getStringExtra("userName"));
            chatOrderNumber = findViewById(R.id.chatOrderNo);
            chatOrderNumber.setText(getResources().getString(R.string.chatOrderNumber, getIntent().getStringExtra("orderNumber")));
            chatBack = findViewById(R.id.chatBack);
            chatBack.setOnClickListener(view -> finish());
            chatRecycler = findViewById(R.id.chatRecycler);
            btnAddPicture = findViewById(R.id.btnAddPicture);
            btnAddPicture.setOnClickListener(this);
            btnSendMessage = findViewById(R.id.btnSendMessage);
            btnSendMessage.setOnClickListener(this);
            etMessage = findViewById(R.id.etMessage);
            etMessage.requestFocus();
            ivListNull = findViewById(R.id.ivListNull);
            tvListNull = findViewById(R.id.tvListNull);
            sendingProgressBar = findViewById(R.id.sendingProgressBar);

            recordView = findViewById(R.id.record_view);
            recordButton = findViewById(R.id.btnAudioRecord);
            recordButton.setRecordView(recordView);
            recordView.setSoundEnabled(false);

            settingRecordViewListener();

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
                        btnAddPicture.setVisibility(View.GONE);
                        recordButton.setVisibility(View.GONE);
                        btnSendMessage.setVisibility(View.VISIBLE);
                    } else {
                        btnSendMessage.setVisibility(View.GONE);
                        btnAddPicture.setVisibility(View.VISIBLE);
                        if (!isSendingMessage) {
                            recordButton.setVisibility(View.VISIBLE);
                            sendingProgressBar.setVisibility(View.GONE);
                        } else {
                            recordButton.setVisibility(View.GONE);
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
        chatAdapter = new ChatAdapter(Seller2Driver.this, datumArrayList);
        layoutManager = new LinearLayoutManager(Seller2Driver.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setStackFromEnd(true);
        chatRecycler.setLayoutManager(layoutManager);
        chatRecycler.setAdapter(chatAdapter);
        chatRecycler.smoothScrollToPosition(datumArrayList.size());

        Common.showDialog(Seller2Driver.this);
        getMessageFromApi(CURRENT_PAGE);

        initScrollListener();
    }

    //Initializing the scroll view to check the last vale to show the progress bar for pagination
    private void initScrollListener() {
        chatRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                loadMore();
                            }
                        });
                        isLoading = true;
                    }
                }
            }
        });

    }

    private void loadMore() {
        try {


            if (CURRENT_PAGE != LAST_PAGE) {
                datumArrayList.add(0, null);
                chatAdapter.notifyItemInserted(0);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getMessageFromApi(CURRENT_PAGE + 1);
                    }
                }, 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void settingRecordViewListener() {

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                if (ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED
                        || ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(Seller2Driver.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2003);
                } else {

                    etMessage.setVisibility(View.GONE);
                    btnAddPicture.setVisibility(View.GONE);
                    recordView.setVisibility(View.VISIBLE);

                    long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
                    audioFile = new File(Environment.getExternalStorageDirectory(),
                            "MinBio_" + String.valueOf(number) + ".m4a");
                    mediaRecorder = new MediaRecorder();
                    resetRecorder();
                }

            }

            @Override
            public void onCancel() {

                if (ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    shutDownRecorder();
                }
            }

            @Override
            public void onFinish(long recordTime) {

                if (ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    etMessage.setVisibility(View.VISIBLE);
                    btnAddPicture.setVisibility(View.VISIBLE);
                    recordView.setVisibility(View.GONE);

                    shutDownRecorder();
                    postMessage("", audioFile, "audio", String.valueOf(recordTime));
                    etMessage.requestFocus();
                }

            }

            @Override
            public void onLessThanSecond() {
                etMessage.setVisibility(View.VISIBLE);
                btnAddPicture.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);

                if (ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    shutDownRecorder();
                }
            }
        });

        recordButton.setOnRecordClickListener(v -> {

            if (ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(Seller2Driver.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(Seller2Driver.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2003);
            } else {
                recordButton.setListenForRecord(true);
            }

        });


        recordView.setOnBasketAnimationEndListener(() -> {
            etMessage.setVisibility(View.VISIBLE);
            btnAddPicture.setVisibility(View.VISIBLE);
            recordView.setVisibility(View.GONE);

            shutDownRecorder();
        });

    }

    private void resetRecorder() {
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shutDownRecorder() {
        try {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSendMessage:
                handleSendingMessage();
                break;
            case R.id.btnAddPicture:
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, IMAGE_REQUEST_CODE);
                break;
            case R.id.chatBack:
                finish();
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
            postMessage(etMessage.getText().toString(), null, "text", "");
            isSendingMessage = true;
            etMessage.setText("");
        }
    }


    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(Seller2Driver.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Seller2Driver.this, new String[]{permission}, requestCode);
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
        } else if (requestCode == AUDIO_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recordButton.setListenForRecord(true);
            } else
                recordButton.setListenForRecord(false);
        }
    }

    private void getImage() {
        ImagePicker.Companion.with(this)
                .crop(1f, 1f)
                .compress(800)
                .maxResultSize(1080, 1080)
                .start(2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2000) {
                Uri fileUri = data.getData();
                if (fileUri != null) {

                    File file = new File(fileUri.getPath());
                    postMessage("", file, "image", "");

                }
            }
        }
    }

    private void getMessageFromApi(int c) {
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ChatModel> call = api.getSellerDriverMessages("Bearer " + SharedPreference.getSimpleString(Seller2Driver.this, Constants.accessToken),
                sellerDriverOrderId, c);

        call.enqueue(new Callback<ChatModel>() {
            @Override
            public void onResponse(Call<ChatModel> call, Response<ChatModel> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        if (response.body().getData().size() > 0) {

                            int p = 0;

                            if (datumArrayList.size() > 0) {
                                datumArrayList.remove(0);
                                chatAdapter.notifyItemRemoved(0);

                                p = datumArrayList.size() - 1;
                                if (p < 0) {
                                    p = 0;
                                }

                            }

                            LAST_PAGE = response.body().getMeta().getLastPage();
                            CURRENT_PAGE = response.body().getMeta().getCurrentPage();

                            if (!response.body().getUserName().equals(""))
                                chatTitle.setText(response.body().getUserName());

                            if (datumArrayList.size() > 0)
                                datumArrayList.addAll(0, response.body().getData());
                            else
                                datumArrayList.addAll(response.body().getData());
                            chatAdapter.notifyDataSetChanged();

                            if (c > 0)
                                chatRecycler.scrollToPosition(datumArrayList.size() - p);

                            isLoading = false;
                            chatRecycler.setVisibility(View.VISIBLE);
                            tvListNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                        } else {
                            showNullMessage();
                        }

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(Seller2Driver.this, jsonObject.getString("message"), false);
                        showNullMessage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showNullMessage();
                }

            }

            @Override
            public void onFailure(Call<ChatModel> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Seller2Driver", "onFailure: " + t.getMessage());
                showNullMessage();
            }
        });

    }

    private void showNullMessage() {
        chatRecycler.setVisibility(View.GONE);
        tvListNull.setVisibility(View.VISIBLE);
        ivListNull.setVisibility(View.VISIBLE);
    }

    private void postMessage(String message, File file, String type, String duration) {

        recordButton.setVisibility(View.GONE);
        sendingProgressBar.setVisibility(View.VISIBLE);

        Api api = RetrofitClient.getClient().create(Api.class);
        RequestBody rbMessage = null;
        MultipartBody.Part mbMessage = null;
        Call<okhttp3.ResponseBody> call = null;

        if (duration.equals(""))
            duration = "0";

        RequestBody rbOrderId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sellerDriverOrderId));
        RequestBody rbType = RequestBody.create(MediaType.parse("text/plain"), type);
        RequestBody rbDuration = RequestBody.create(MediaType.parse("text/plain"), milliSecondsToTimer(Long.parseLong(duration)));

        if (type.equals("text")) {
            rbMessage = RequestBody.create(MediaType.parse("text/plain"), message);
            call = api.postTextMessageSellerDriver("Bearer " + SharedPreference.getSimpleString(Seller2Driver.this, Constants.accessToken),
                    rbOrderId, rbType, rbDuration, rbMessage);
        } else {
            if (type.equals("image"))
                rbMessage = RequestBody.create(MediaType.parse("image/*"), file);
            else if (type.equals("audio"))
                rbMessage = RequestBody.create(MediaType.parse("audio/*"), file);

            mbMessage = MultipartBody.Part.createFormData("message", file.getName(), rbMessage);

            call = api.postFileMessageSellerDriver("Bearer " + SharedPreference.getSimpleString(Seller2Driver.this, Constants.accessToken),
                    rbOrderId, rbType, rbDuration, mbMessage);
        }

        call.enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                etMessage.setEnabled(true);
                isSendingMessage = false;
                recordButton.setVisibility(View.VISIBLE);
                sendingProgressBar.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful()) {
                        JSONObject r = new JSONObject(response.body().string());

                        JSONObject jsonObject = r.getJSONObject("data");

                        Datum d = new Datum(jsonObject.getInt("id"), jsonObject.getInt("chat_id"),
                                jsonObject.getInt("sender_id"), jsonObject.getInt("receiver_id"), jsonObject.getString("type"),
                                jsonObject.getString("message"), jsonObject.getInt("is_delivered"), jsonObject.getInt("is_seen"),
                                jsonObject.getString("date"), jsonObject.getString("file_path"), jsonObject.getString("duration"));

                        if (!checkIfAlreadyExist(jsonObject.getInt("id"))) {
                            datumArrayList.add(d);
                            chatAdapter.notifyDataSetChanged();
                            chatRecycler.smoothScrollToPosition(datumArrayList.size());
                        }

                        chatRecycler.setVisibility(View.VISIBLE);
                        tvListNull.setVisibility(View.GONE);
                        ivListNull.setVisibility(View.GONE);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(Seller2Driver.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Seller2Driver", "onFailure: " + t.getMessage());
                etMessage.setEnabled(true);
                isSendingMessage = false;
                recordButton.setVisibility(View.VISIBLE);
                sendingProgressBar.setVisibility(View.GONE);
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
        LocalBroadcastManager.getInstance(Seller2Driver.this).unregisterReceiver(chatBroadcastReceiver);
        stopAudio();
    }

    @Override
    protected void onResume() {
        super.onResume();
        iss2dChatActive = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        iss2dChatActive = false;

        if (mediaRecorder != null) {
            shutDownRecorder();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopAudio();
    }

    public void stopAudio() {
        if (chatAdapter != null)
            chatAdapter.stopMediaPLayer();
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutesString;
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
