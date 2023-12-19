package com.vic.vicwsp.Views.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.PrivacyPolicy.PrivacyPolicyModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.FilePath;
import com.vic.vicwsp.Utils.SharedPreference;
import com.hbb20.CountryCodePicker;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class SignUp extends AppCompatActivity implements View.OnClickListener {


    private ConstraintLayout btnSignUp, btnToLogin;
    private EditText etSignUpName, etSignUpLastName, etSignUpEmail, etSignUpCompanyName, etSiretNo, etSignUpPassword, etSignUpConfirmPassword, etPhoneNumber;
    private LinearLayout phoneLinear;
    private TextView alreadyHaveAnAccount;
    private CountryCodePicker countryCodePicker;
    private ImageView back;
    private ImageView kbisImage, idImage, kbisTick, idTick;
    private String kbisPath = "", idPath = "", kbisExtension = "", idExtension = "";
    private Uri cameraUri;
    private Uri destinationUriCropper;
    private CheckBox privacyCheck;
    private TextView privacyText;

    private boolean isChecked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
        initViews();
    }

    //Initializing the views
    private void initViews() {
        btnSignUp = findViewById(R.id.btnSignUp);
        back = findViewById(R.id.ivSignUpBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        phoneLinear = findViewById(R.id.signUpPhoneLinear);
        alreadyHaveAnAccount = findViewById(R.id.alreadyHaveAnAccount);

        String text = "<font color='black'>" + getResources().getString(R.string.SignUpAlreadyHaveAnAccount) + "</font>" + " " +
                "<font color='#50a936'>" + getResources().getString(R.string.signUpLoginNow) + "</font>";
        alreadyHaveAnAccount.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);


        btnToLogin = findViewById(R.id.btnToLogin);
        etSignUpName = findViewById(R.id.etSignUpName);
        etSignUpLastName = findViewById(R.id.etSignUpLastName);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        etSignUpCompanyName = findViewById(R.id.etSignUpCompanyName);
        etSiretNo = findViewById(R.id.etSiretNo);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        etPhoneNumber = findViewById(R.id.etSignUpPhoneNumber);
        countryCodePicker = findViewById(R.id.ccp);
        etSignUpConfirmPassword = findViewById(R.id.etSignUpConfirmPassword);
        kbisImage = findViewById(R.id.kbisImage);
        idImage = findViewById(R.id.idImage);
        kbisTick = findViewById(R.id.kbisSignUpTick);
        idTick = findViewById(R.id.idSignUpTick);

        privacyCheck = findViewById(R.id.privacyCheckBox);
        privacyText = findViewById(R.id.privacyText);

//        privacyText.setOnClickListener(v -> {
//            callTermsAndConditionApi();
//        });

        privacyCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.isChecked = isChecked;
        });

        if (isChecked)
            privacyCheck.setChecked(true);
        else
            privacyCheck.setChecked(false);

        kbisImage.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowImages(true)
                    .setSuffixes("pdf", "png", "jpg", "jpeg")
                    .enableImageCapture(true)
                    .setMaxSelection(1).setShowAudios(false)
                    .setShowVideos(false)
                    .setSingleChoiceMode(true)
                    .setSkipZeroSizeFiles(true)
                    .setShowFiles(true)
                    .build());
            startActivityForResult(intent, 101);
        });

        idImage.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowImages(true)
                    .setSuffixes("pdf", "png", "jpg", "jpeg")
                    .enableImageCapture(true)
                    .setMaxSelection(1)
                    .setSingleChoiceMode(true).setShowAudios(false)
                    .setShowVideos(false)
                    .setSkipZeroSizeFiles(true)
                    .setShowFiles(true)
                    .build());
            startActivityForResult(intent, 102);
        });


        btnSignUp.setOnClickListener(this);
        btnToLogin.setOnClickListener(this);
        countryCodePicker.setKeyboardAutoPopOnSearch(false);
        setPrivacyText();
    }

    private void setPrivacyText() {

        String text = getResources().getString(R.string.SignUpAcceptTermsText);

        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                callTermsAndConditionApi();
            }

            @Override
            public void updateDrawState(TextPaint ds) {// override updateDrawState
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
        spannableString.setSpan(clickableSpan1, 13, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.signUpButtonColor)), 13, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyText.setText(spannableString);
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

    }


    private void callTermsAndConditionApi() {
        Common.showDialog(SignUp.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<PrivacyPolicyModel> call = api.getPrivacyPolicy("privacy_policy_page");
        call.enqueue(new Callback<PrivacyPolicyModel>() {
            @Override
            public void onResponse(Call<PrivacyPolicyModel> call, Response<PrivacyPolicyModel> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {

                        String content = "";

                        if (SharedPreference.getSimpleString(SignUp.this, Constants.language).equals(Constants.french))
                            content = response.body().getData().getContentFr();
                        else
                            content = response.body().getData().getContent();


                        Intent intent = new Intent(SignUp.this, WebViewActivity.class);
                        intent.putExtra("content", content);
                        startActivity(intent);


                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SignUp.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<PrivacyPolicyModel> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.btnSignUp:
                validate();
                break;
            case R.id.btnToLogin:
                finish();
                break;
            default:
                break;
        }
    }

    //Validating the fields
    private void validate() {


        if (etSignUpName.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessageEnter_name), false);
            return;
        }
        if (etSignUpLastName.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessageEnterLastName), false);
            return;
        }
        if (etSignUpEmail.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessageEnterEmail), false);
            return;
        }

        if (!Common.isValidEmailId(etSignUpEmail.getText().toString())) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessageInvalidEmail), false);
            return;
        }

        if (etPhoneNumber.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessagePlease_enter_phone_number), false);
            return;
        }


        if (etSignUpCompanyName.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessageEnter_company_name), false);
            return;
        }

        if (etSiretNo.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessageEnter_siret_number), false);
            return;
        }

        if (etSiretNo.getText().toString().length() < 9 || etSiretNo.getText().toString().length() > 9) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessageSiren_numer_must_be_equal_to_9), false);
            return;
        }

        if (kbisPath.equals("")) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessagePlease_add_kbis_image_msg), false);
            return;
        }

        if (idPath.equals("")) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessagePlease_add_id_image_msg), false);
            return;
        }

        if (etSignUpPassword.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessageEnterPassword), false);
            return;
        }

        if (etSignUpPassword.getText().toString().length() < 8) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessagePassword_length), false);
            return;
        }


        if (etSignUpConfirmPassword.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessageEnter_password_confirmation), false);
            return;
        }

        if (!etSignUpPassword.getText().toString().equals(etSignUpConfirmPassword.getText().toString())) {
            showToast(this, getResources().getString(R.string.SignUpErrorMessagePassword_not_same), false);
            return;
        }

        if (!isChecked) {
            showToast(this, getResources().getString(R.string.signUp_error_privacy), false);
            return;
        }

        callRegisterApi();
    }

    private void callRegisterApi() {
        Common.showDialog(SignUp.this);

        Api api = RetrofitClient.getClient().create(Api.class);

        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), etSignUpName.getText().toString());
        RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), etSignUpLastName.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), etSignUpEmail.getText().toString());
        RequestBody companyName = RequestBody.create(MediaType.parse("text/plain"), etSignUpCompanyName.getText().toString());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), etSignUpPassword.getText().toString());
        RequestBody confirmPassword = RequestBody.create(MediaType.parse("text/plain"), etSignUpConfirmPassword.getText().toString());
        RequestBody siretNo = RequestBody.create(MediaType.parse("text/plain"), etSiretNo.getText().toString());
        RequestBody countryCode = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(countryCodePicker.getSelectedCountryCodeWithPlus()));
        RequestBody phoneNo = RequestBody.create(MediaType.parse("text/plain"), etPhoneNumber.getText().toString());
        RequestBody iso2 = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(countryCodePicker.getSelectedCountryNameCode()));

        MultipartBody.Part idPart = null, kbisPart = null;
        RequestBody idfileReqBody = null, kbisFileReqBody = null;

        if (!kbisPath.equals("")) {
            File kbisFile = null;
            kbisFile = new File(kbisPath);
            if (kbisExtension.equals(".png") || kbisExtension.equals(".jpg") || kbisExtension.equals(".jpeg"))
                kbisFileReqBody = RequestBody.create(MediaType.parse("image/*"), kbisFile);
            else if (kbisExtension.equals(".pdf"))
                kbisFileReqBody = RequestBody.create(MediaType.parse("application/pdf"), kbisFile);
            kbisPart = MultipartBody.Part.createFormData("kbis_file", kbisFile.getName(), kbisFileReqBody);
        }

        if (!idPath.equals("")) {
            File idFile = null;
            idFile = new File(idPath);
            if (idExtension.equals(".png") || idExtension.equals(".jpg") || idExtension.equals(".jpeg"))
                idfileReqBody = RequestBody.create(MediaType.parse("image/*"), idFile);
            else if (idExtension.equals(".pdf"))
                idfileReqBody = RequestBody.create(MediaType.parse("application/pdf"), idFile);
            idPart = MultipartBody.Part.createFormData("id_file", idFile.getName(), idfileReqBody);
        }

        Call<ResponseBody> register = api.register(Splash.appLanguage, name, lastName, email, companyName, password, confirmPassword, siretNo,
                countryCode, phoneNo, iso2, kbisPart, idPart);


        register.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.has("status")) {
                            boolean status = jsonObject.getBoolean("status");
                            if (status) {

                                idTick.setVisibility(View.VISIBLE);
                                kbisTick.setVisibility(View.VISIBLE);


                                JSONObject data = jsonObject.getJSONObject("data");
                                showToast(SignUp.this, jsonObject.getString("message"), true);

                                if (data.has("phone")) {
                                    Intent i = new Intent(SignUp.this, OtpConfirmation.class);
                                    i.putExtra("phoneNo", data.getString("phone"));

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(i);
                                            finish();
                                        }
                                    }, 2500);
                                } else if (data.has("email")) {
                                    Intent i = new Intent(SignUp.this, EmailConfirmation.class);
                                    i.putExtra("    email", data.getString("email"));

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(i);
                                            finish();
                                        }
                                    }, 2500);
                                }
                            } else
                                showToast(SignUp.this, jsonObject.getString("message"), false);
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SignUp.this, jsonObject.getString("message"), false);

                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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

    private void getFileFromDirectory(int requestCode) {
        if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, Constants.PERMISSIONS_STORAGE, requestCode);
        } else {


            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    getResources().getString(R.string.app_name));
            if (!imageStorageDir.exists()) {
                imageStorageDir.mkdirs();
            }
            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            cameraUri = Uri.fromFile(file);

            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);


            Intent chooseFile = new Intent();
            chooseFile.setType("*/*");
            String[] mimetypes = {"image/*", "application/pdf"};
            chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            chooseFile.setAction(Intent.ACTION_GET_CONTENT);
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            chooseFile.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
            startActivityForResult(chooseFile, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getFileFromDirectory(requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 101) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {

                    Uri fileUri = files.get(0).getUri();

                    kbisPath = files.get(0).getPath();
                    kbisExtension = kbisPath.substring(kbisPath.lastIndexOf("."));
                    if (kbisExtension.equalsIgnoreCase(".png") || kbisExtension.equalsIgnoreCase(".jpg") || kbisExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 201);
                    } else if (kbisExtension.equalsIgnoreCase(".pdf")) {
                        kbisTick.setVisibility(View.VISIBLE);
                        Glide.with(SignUp.this).load(R.drawable.ic_pdf).into(kbisImage);
                    }
                }

            } else if (requestCode == 102) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {
                    Uri fileUri = files.get(0).getUri();
                    idPath = files.get(0).getPath();
                    idExtension = idPath.substring(idPath.lastIndexOf("."));
                    if (idExtension.equalsIgnoreCase(".png") || idExtension.equalsIgnoreCase(".jpg") || idExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 202);
                    } else if (idExtension.equalsIgnoreCase(".pdf")) {
                        idTick.setVisibility(View.VISIBLE);
                        Glide.with(SignUp.this).load(R.drawable.ic_pdf).into(idImage);
                    }
                }
            } else if (requestCode == 201) {
                kbisTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                kbisPath = FilePath.getPath(SignUp.this, cropperUri);
                kbisExtension = kbisPath.substring(kbisPath.lastIndexOf("."));

                Glide.with(SignUp.this).load(kbisPath).into(kbisImage);
            } else if (requestCode == 202) {
                idTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                idPath = FilePath.getPath(SignUp.this, cropperUri);
                idExtension = idPath.substring(idPath.lastIndexOf("."));

                Glide.with(SignUp.this).load(idPath).into(idImage);
            }

        }
    }

    private void startCrop(Uri sourceUri, int requestCode) {

        File file = new File(SignUp.this.getCacheDir(), "IMG_" + System.currentTimeMillis() + ".jpg");
        destinationUriCropper = Uri.fromFile(file);

        startActivityForResult(UCrop.of(sourceUri, destinationUriCropper)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(5f, 5f)
                .getIntent(SignUp.this), requestCode);

    }
}
