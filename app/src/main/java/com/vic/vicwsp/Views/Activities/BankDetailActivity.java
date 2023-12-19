package com.vic.vicwsp.Views.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.BankDetail.BankDetailModel;
import com.vic.vicwsp.Models.Response.BankDetail.BankDetailResponse;
import com.vic.vicwsp.Models.User;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.CustomTextWatcher;
import com.vic.vicwsp.Utils.FilePath;
import com.vic.vicwsp.Utils.SharedPreference;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class BankDetailActivity extends AppCompatActivity {


    ImageView ivAccountName, ivSwiftNumber, ivIBAN;
    EditText etAccountName, etSwiftNumber, etIBAN;

    ConstraintLayout btnSave;
    LinearLayout imgUploadBankDetailFile;
    ImageView imgBankDetail;
    ImageView ivToolbarBack;
    Context ctx;
    private User user;

    private Uri destinationUriCropper;
    private String bankImagePath = "", bankImageExtension = "";
    private boolean isPathUrl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);

        // image tick and image error
        ivAccountName = findViewById(R.id.ivAccountName);
        ivSwiftNumber = findViewById(R.id.ivSwiftNumber);
        ivIBAN = findViewById(R.id.ivIBAN);

        // edittext
        etAccountName = findViewById(R.id.etAccountName);
        etSwiftNumber = findViewById(R.id.etSwiftNumber);
        etIBAN = findViewById(R.id.etIBAN);

        // button
        btnSave = findViewById(R.id.btnSave);
        // upload bank file
        imgUploadBankDetailFile = findViewById(R.id.imgUploadBankDetailFile);
        imgBankDetail = findViewById(R.id.imgBankDetail);
        ivToolbarBack = findViewById(R.id.ivToolbarBack);


        ctx = getApplicationContext();
        etAccountName.addTextChangedListener(new CustomTextWatcher(ivAccountName, ctx).textWatcher);
        etSwiftNumber.addTextChangedListener(new CustomTextWatcher(ivSwiftNumber, ctx).textWatcher);
        etIBAN.addTextChangedListener(new CustomTextWatcher(ivIBAN, ctx).textWatcher);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        ivToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgUploadBankDetailFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        populateData();


    }

    void populateData() {
        Common.showDialog(BankDetailActivity.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);
        token = "Bearer " + token;

        Call<BankDetailResponse> register = api.getBankDetail(token);
        register.enqueue(new Callback<BankDetailResponse>() {
            @Override
            public void onResponse(Call<BankDetailResponse> call, Response<BankDetailResponse> response) {
                System.out.println("Code   " + response.code());
                if (response.isSuccessful()) {
                    Common.dissmissDialog();
                    if (response.body().getStatus()) {
                        Common.bankDetailtoJson(getApplicationContext(), response.body().getData());
                        if (response.body().getStatus()) {
                            BankDetailModel bankDetailModel = response.body().getData();
                            etAccountName.setText(bankDetailModel.getAccountName());
                            etSwiftNumber.setText(bankDetailModel.getSwiftNumber());
                            etIBAN.setText(bankDetailModel.getIban());

                            if (bankDetailModel.getImagePath() != null && bankDetailModel.getImagePath() != "") {
                                bankImagePath = bankDetailModel.getImagePath();
                                bankImageExtension = bankDetailModel.getBankDetailImageExtension();

                                isPathUrl = true;

                                if (bankImageExtension.equals("png") || bankImageExtension.equals("jpg") || bankImageExtension.equals("jpeg"))
                                    Glide.with(BankDetailActivity.this).load(bankImagePath).into(imgBankDetail);
                                else if (bankImageExtension.equals("pdf"))
                                    Glide.with(BankDetailActivity.this).load(R.drawable.ic_pdf).into(imgBankDetail);
                            }

                        }

                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(BankDetailActivity.this, jsonObject.getString("message"), false);
                        Common.dissmissDialog();
                    } catch (Exception e) {
                        Common.dissmissDialog();
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<BankDetailResponse> call, Throwable t) {
                Common.dissmissDialog();
            }
        });

    }


    void pickImage() {
        Intent intent = new Intent(BankDetailActivity.this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(true)
                .setSuffixes("pdf", "png", "jpg", "jpeg")
                .enableImageCapture(true)
                .setMaxSelection(1)
                .setShowAudios(false)
                .setShowVideos(false)
                .setSingleChoiceMode(true)
                .setShowFiles(true)
                .setSkipZeroSizeFiles(true)
                .build());
        startActivityForResult(intent, 101);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 101) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {
                    Uri fileUri = files.get(0).getUri();

                    bankImagePath = files.get(0).getPath();
                    bankImageExtension = bankImagePath.substring(bankImagePath.lastIndexOf("."));
                    if (bankImageExtension.equalsIgnoreCase(".png") || bankImageExtension.equalsIgnoreCase(".jpg") || bankImageExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 201);
                    } else if (bankImageExtension.equalsIgnoreCase(".pdf")) {
                        Glide.with(BankDetailActivity.this).load(R.drawable.ic_pdf).into(imgBankDetail);
                        isPathUrl = false;
                    }
                }
            }
            if (requestCode == 201) {
                Uri cropperUri = UCrop.getOutput(data);
                bankImagePath = FilePath.getPath(BankDetailActivity.this, cropperUri);
                bankImageExtension = bankImagePath.substring(bankImagePath.lastIndexOf("."));
                Glide.with(BankDetailActivity.this).load(bankImagePath).into(imgBankDetail);
                isPathUrl = false;
            }

        }
    }

    private void startCrop(Uri sourceUri, int requestCode) {

        File file = new File(BankDetailActivity.this.getCacheDir(), "IMG_" + System.currentTimeMillis() + ".jpg");
        destinationUriCropper = Uri.fromFile(file);

        startActivityForResult(UCrop.of(sourceUri, destinationUriCropper)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(5f, 5f)
                .getIntent(BankDetailActivity.this), requestCode);

    }

    void validateData() {

        if (etAccountName.getText().toString().isEmpty()) {
            showToast((BankDetailActivity.this), getResources().getString(R.string.bank_error_enter_account_name), false);
            return;
        }

        if (etSwiftNumber.getText().toString().isEmpty()) {
            showToast((BankDetailActivity.this), getResources().getString(R.string.bank_error_enter_swift_number), false);
            return;
        }
        if (etIBAN.getText().toString().isEmpty()) {
            showToast((BankDetailActivity.this), getResources().getString(R.string.bank_error_enter_iban), false);
            return;
        }

        if (bankImagePath.equals("")) {
            showToast((BankDetailActivity.this), getResources().getString(R.string.bank_error_enter_bank_detail_file), false);
            return;
        }


        SaveBankDetail();
    }

    void SaveBankDetail() {
        Common.showDialog(BankDetailActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);
        token = "Bearer " + token;

        RequestBody account_name = RequestBody.create(MediaType.parse("text/plain"), etAccountName.getText().toString());
        RequestBody swift_number = RequestBody.create(MediaType.parse("text/plain"), etSwiftNumber.getText().toString());
        RequestBody iban = RequestBody.create(MediaType.parse("text/plain"), etIBAN.getText().toString());
        MultipartBody.Part bankDetailFileBodyPart = null;
        RequestBody bank_detail_photo = null;

        if (!bankImagePath.equals("")) {
            if (!isPathUrl) {
                File bankDetailFile = null;
                bankDetailFile = new File(bankImagePath);
                if (bankImageExtension.equals(".png") || bankImageExtension.equals(".jpg") || bankImageExtension.equals(".jpeg"))
                    bank_detail_photo = RequestBody.create(MediaType.parse("image/*"), bankDetailFile);
                else if (bankImageExtension.equals(".pdf"))
                    bank_detail_photo = RequestBody.create(MediaType.parse("application/pdf"), bankDetailFile);
                bankDetailFileBodyPart = MultipartBody.Part.createFormData("bank_detail_photo", bankDetailFile.getName(), bank_detail_photo);
            }
        }

        Call<ResponseBody> register = api.updateBankDetail(token, account_name, swift_number, iban, bankDetailFileBodyPart);

        register.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        showToast(BankDetailActivity.this, jsonObject.getString("message"), true);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(BankDetailActivity.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    Common.dissmissDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
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
