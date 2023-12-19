package com.vic.vicwsp.Views.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.BuildConfig;
import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.Models.Response.PrivacyPolicy.PrivacyPolicyModel;
import com.vic.vicwsp.Models.User;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.FilePath;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.AccountMapsActivity;
import com.vic.vicwsp.Views.Activities.BankDetailActivity;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.SignIn;
import com.vic.vicwsp.Views.Activities.Splash;
import com.vic.vicwsp.Views.Activities.WebViewActivity;
import com.hbb20.CountryCodePicker;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.suke.widget.SwitchButton;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Utils.Constants.mLastClickTime;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

public class Settings extends Fragment implements View.OnClickListener, SocketCallback {


    private View v;
    private ImageView logout, back;
    private TextView AccountAddress, AutoNegoLabel, tvVersionNo;
    private SwitchButton autoNegoSwitch;
    private EditText etBussiness, etSiret, etName, etPreName, etPhone, etEmail, etPassword, etPasswordConfirmation;
    private ConstraintLayout settingSaveChangesButton;
    private ConstraintLayout locationConstraint;
    private AlertDialog alertDialog;
    private CountryCodePicker countryCodePicker;
    private Dialog dialog;
    private ConstraintLayout cartConstraintLayout, cartConstraint, btnBankDetail;
    private ImageView passwordTick, CpassswordTick, bussinessTick, siretTick, phoneTick, emailTick, firstNameTick, lastNameTick, addressTick;
    private String accountAddress = "";
    private Double lat = 1.0, lng = 1.0;
    private User user;
    private int autoNegoKey = 0;
    private ImageView kbisImageSettings, idImageSettings, kbisTick, idTick;
    private String kbisPath = "", idPath = "", idExtension = "", kbisExtension = "";
    private ConstraintLayout changeLanguageLayout, supportConstraint;
    private ImageView ivChangeLanguage;
    private Uri cameraUri;

    private CheckBox privacyCheck;
    private TextView privacyText;


    private Uri destinationUriCropper;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("accountAddressBroadCast")) {
                try {
                    accountAddress = intent.getStringExtra("userAddress");
                    lat = Double.valueOf(intent.getStringExtra("lat"));
                    lng = Double.valueOf(intent.getStringExtra("lng"));

                    if (AccountAddress != null && accountAddress != null && lat != null && lng != null) {
                        if (!accountAddress.equals(""))
                            AccountAddress.setText(accountAddress);
                        else {
                            accountAddress = "";
                            AccountAddress.setText(accountAddress);
                            lat = 1.0;
                            lng = 1.0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initViews();
        sockets.initializeCallback(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("accountAddressBroadCast"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    //Initializing the views
    private void initViews() {
        TextView notificationBadge = getActivity().findViewById(R.id.notificationBadge);
        notificationBadge.setVisibility(View.GONE);
        ImageView ivNotifications = getActivity().findViewById(R.id.ivNotifications);
        ivNotifications.setVisibility(View.GONE);

        supportConstraint = getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.GONE);

        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        logout.setOnClickListener(this);

        etBussiness = v.findViewById(R.id.etBussiness);
        etBussiness.addTextChangedListener(new CustomTextWatcher(etBussiness));

        etSiret = v.findViewById(R.id.etSiret);
        etSiret.addTextChangedListener(new CustomTextWatcher(etSiret));

        etPassword = v.findViewById(R.id.etPassword);
        etPassword.addTextChangedListener(new CustomTextWatcher(etPassword));

        etPasswordConfirmation = v.findViewById(R.id.etPasswordConfirmation);
        etPasswordConfirmation.addTextChangedListener(new CustomTextWatcher(etPasswordConfirmation));

        etPhone = v.findViewById(R.id.etPhone);
        etPhone.addTextChangedListener(new CustomTextWatcher(etPhone));

        etEmail = v.findViewById(R.id.etEmail);
        etEmail.addTextChangedListener(new CustomTextWatcher(etEmail));

        etName = v.findViewById(R.id.etName);
        etName.addTextChangedListener(new CustomTextWatcher(etName));

        etPreName = v.findViewById(R.id.etPreName);
        etPreName.addTextChangedListener(new CustomTextWatcher(etPreName));

        AccountAddress = v.findViewById(R.id.AccountAddress);
        AccountAddress.addTextChangedListener(new CustomTextWatcher(AccountAddress));
        AccountAddress.setOnClickListener(view -> sendingToMapsActivity());

        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraintLayout = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.VISIBLE);
        back.setVisibility(View.GONE);
        cartConstraintLayout.setVisibility(View.GONE);

        passwordTick = v.findViewById(R.id.ivPassword);
        phoneTick = v.findViewById(R.id.ivPhone);
        emailTick = v.findViewById(R.id.ivEmail);
        firstNameTick = v.findViewById(R.id.ivName);
        lastNameTick = v.findViewById(R.id.ivPreName);
        bussinessTick = v.findViewById(R.id.ivBussiness);
        siretTick = v.findViewById(R.id.ivSiret);
        CpassswordTick = v.findViewById(R.id.ivPasswordConfirmation);
        addressTick = v.findViewById(R.id.ivAddress);

        kbisImageSettings = v.findViewById(R.id.kbisImageSettings);
        idImageSettings = v.findViewById(R.id.idImageSettings);
        kbisTick = v.findViewById(R.id.kbisSettingsTick);
        idTick = v.findViewById(R.id.idSettingsTick);

        btnBankDetail = v.findViewById(R.id.btnBankDetail);
        btnBankDetail.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BankDetailActivity.class);
            startActivity(intent);
        });

        kbisImageSettings.setOnClickListener(view -> {

            Intent intent = new Intent(getContext(), FilePickerActivity.class);
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

        idImageSettings.setOnClickListener(view -> {
//            getFileFromDirectory(102);

            Intent intent = new Intent(getContext(), FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowImages(true).setShowAudios(false)
                    .setShowVideos(false)
                    .setSuffixes("pdf", "png", "jpg", "jpeg")
                    .enableImageCapture(true)
                    .setMaxSelection(1)
                    .setSingleChoiceMode(true)
                    .setSkipZeroSizeFiles(true)
                    .setShowFiles(true)
                    .build());
            startActivityForResult(intent, 102);

        });

        autoNegoSwitch = v.findViewById(R.id.autoNegoSwitch);
        AutoNegoLabel = v.findViewById(R.id.autoNegoLabel);

        autoNegoSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked)
                    autoNegoKey = 1;
                else
                    autoNegoKey = 0;
            }
        });

        settingSaveChangesButton = v.findViewById(R.id.settingSaveChangesButton);
        settingSaveChangesButton.setOnClickListener(this);

        ivChangeLanguage = getActivity().findViewById(R.id.ivChangeLanguage);
        changeLanguageLayout = getActivity().findViewById(R.id.changeLanguageLayout);
        changeLanguageLayout.setVisibility(View.VISIBLE);
        changeLanguageLayout.setOnClickListener(this);

        if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("en")) {
            ivChangeLanguage.setImageResource(R.drawable.ic_fr);
        } else if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("fr")) {
            ivChangeLanguage.setImageResource(R.drawable.ic_en);
        }

        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.GONE);

        countryCodePicker = v.findViewById(R.id.settingsCCP);
        countryCodePicker.showFlag(true);
        countryCodePicker.setKeyboardAutoPopOnSearch(false);

        tvVersionNo = v.findViewById(R.id.tvVersionNo);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        tvVersionNo.setText(getContext().getResources().getString(R.string.version) + versionName + "." + String.valueOf(versionCode));

        privacyCheck = v.findViewById(R.id.privacyCheckBox);
        privacyText = v.findViewById(R.id.privacyText);

//        privacyText.setOnClickListener(v -> {
//            callTermsAndConditionApi();
//        });

        getUserDetails();
        setPrivacyText();
    }

    private void setPrivacyText() {

        String text = getResources().getString(R.string.accountPrivacyText);

        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                callTermsAndConditionApi();
            }

            @Override
            public void updateDrawState(TextPaint ds) {// override updateDrawState
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };

        if (SharedPreference.getSimpleString(getContext(), Constants.language).equals(Constants.french)) {
            spannableString.setSpan(clickableSpan1, 23, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.signUpButtonColor)), 23, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(clickableSpan1, 27, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.signUpButtonColor)), 27, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        privacyText.setText(spannableString);
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void callTermsAndConditionApi() {
        Common.showDialog(getContext());
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<PrivacyPolicyModel> call = api.getPrivacyPolicy("terms_and_conditions_page");
        call.enqueue(new Callback<PrivacyPolicyModel>() {
            @Override
            public void onResponse(Call<PrivacyPolicyModel> call, Response<PrivacyPolicyModel> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {

                        String content = "";

                        if (SharedPreference.getSimpleString(getContext(), Constants.language).equals(Constants.french))
                            content = response.body().getData().getContentFr();
                        else
                            content = response.body().getData().getContent();


                        Intent intent = new Intent(getContext(), WebViewActivity.class);
                        intent.putExtra("content", content);
                        startActivity(intent);


                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(getActivity(), jsonObject.getString("message"), false);
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


    private void sendingToMapsActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("userAddress", accountAddress);
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        Intent intent = new Intent(getContext(), AccountMapsActivity.class);
        intent.putExtras(bundle);
        getContext().startActivity(intent);

    }

    //Getting user details form the database

    private void getUserDetails() {
        DatabaseHelper db = new DatabaseHelper(getContext());
        try {
            user = db.getUser(Integer.parseInt(SharedPreference.getSimpleString(getContext(), Constants.userId)));

            if (user.getIs_merchant() == 0) {
                autoNegoKey = 0;
                AutoNegoLabel.setVisibility(View.GONE);
                autoNegoSwitch.setVisibility(View.GONE);
            } else if (user.getIs_merchant() == 1) {
                autoNegoKey = 1;
                AutoNegoLabel.setVisibility(View.VISIBLE);
                autoNegoSwitch.setVisibility(View.VISIBLE);

                if (user.getNegoAutoAccept() == 0)
                    autoNegoSwitch.setChecked(false);
                else if (user.getNegoAutoAccept() == 1)
                    autoNegoSwitch.setChecked(true);

            }

            if (user.getCompanyName().equals("null"))
                etBussiness.setText("");
            else
                etBussiness.setText(user.getCompanyName());

            if (user.getName().equals("null"))
                etName.setText("");
            else
                etName.setText(user.getName());

            if (user.getSiretNo().equals("null"))
                etSiret.setText("");
            else
                etSiret.setText(user.getSiretNo());

            if (user.getEmail().equals("null"))
                etEmail.setText("");
            else
                etEmail.setText(user.getEmail());

            if (user.getCountryCode().equals("null"))
                countryCodePicker.setCountryForPhoneCode(0);
            else
                countryCodePicker.setCountryForPhoneCode(Integer.parseInt(user.getCountryCode()));

            if (user.getPhone().equals("null"))
                etPhone.setText("");
            else
                etPhone.setText(user.getPhone());

            if (user.getLastName() == null)
                etPreName.setText("");
            else
                etPreName.setText(user.getLastName());

            if (user.getLat() == null)
                lat = 1.0;
            else {
                if (user.getLat().isEmpty())
                    lat = 0.0;
                else
                    lat = Double.valueOf(user.getLat());
            }

            if (user.getLng() == null)
                lng = 1.0;
            else {
                if (user.getLng().isEmpty())
                    lng = 0.0;
                else
                    lng = Double.valueOf(user.getLng());
            }
            if (user.getAddress() == null) {
                AccountAddress.setText("");
                accountAddress = "";
            } else {
                AccountAddress.setText(user.getAddress());
                accountAddress = user.getAddress();
            }

            if (user.getKbisPath().equals("")) {
                kbisTick.setVisibility(View.GONE);
            } else {
                kbisTick.setVisibility(View.VISIBLE);
                kbisExtension = user.getKbisPath().substring(user.getKbisPath().lastIndexOf("."));
                if (kbisExtension.equals(".png") || kbisExtension.equals(".jpg") || kbisExtension.equals(".jpeg"))
                    Glide.with(getContext()).load(user.getKbisPath()).into(kbisImageSettings);
                else if (kbisExtension.equals(".pdf"))
                    Glide.with(getContext()).load(R.drawable.ic_pdf).into(kbisImageSettings);

            }

            if (user.getIdPath().equals("")) {
                idTick.setVisibility(View.GONE);
            } else {
                idTick.setVisibility(View.VISIBLE);
                idExtension = user.getIdPath().substring(user.getIdPath().lastIndexOf("."));
                if (idExtension.equalsIgnoreCase(".png") || idExtension.equalsIgnoreCase(".jpg") || idExtension.equalsIgnoreCase(".jpeg"))
                    Glide.with(getContext()).load(user.getIdPath()).into(idImageSettings);
                else if (idExtension.equalsIgnoreCase(".pdf"))
                    Glide.with(getContext()).load(R.drawable.ic_pdf).into(idImageSettings);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.settingSaveChangesButton) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            validate();
        } else if (view.getId() == R.id.ivToolbarLogout) {

            alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(getContext().getResources().getString(R.string.exit_app))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Common.showDialog(getContext());
                            callLogoutApi();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    }).show();


        } else if (view.getId() == R.id.changeLanguageLayout) {

            if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("en")) {
                callChangeLanguageApi(Constants.french);
            } else if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("fr")) {
                callChangeLanguageApi(Constants.english);
            }

        }

    }
    //validating the fields

    private void validate() {

        if (etBussiness.getText().toString().isEmpty()) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessageEnter_company_name), false);
            return;
        }

        if (etSiret.getText().toString().isEmpty()) {

            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessageEnter_siret_number), false);

            return;
        }
        if (etSiret.getText().toString().length() < 9 || etSiret.getText().toString().length() > 9) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessageSiren_numer_must_be_equal_to_9), false);

            return;
        }

        if (etName.getText().toString().isEmpty()) {

            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessageEnter_name), false);

            return;
        }

        if (etPhone.getText().toString().isEmpty()) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessagePlease_enter_phone_number), false);

            return;
        }

        if (etPreName.getText().toString().isEmpty()) {

            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessageEnter_pre_name), false);

            return;
        }
        if (etEmail.getText().toString().isEmpty()) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessageEnterEmail), false);

            return;
        }
        if (!Common.isValidEmailId(etEmail.getText().toString())) {

            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessageInvalidEmail), false);
            return;
        }

        try {
            callSaveChangesApi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //calling the savve changes api
    private void callSaveChangesApi() throws Exception {

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> profile = null;

        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), etEmail.getText().toString());
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), etName.getText().toString());
        RequestBody preName = RequestBody.create(MediaType.parse("text/plain"), etPreName.getText().toString());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), etPhone.getText().toString());
        RequestBody business = RequestBody.create(MediaType.parse("text/plain"), etBussiness.getText().toString());
        RequestBody siret = RequestBody.create(MediaType.parse("text/plain"), etSiret.getText().toString());
        RequestBody countryCode = RequestBody.create(MediaType.parse("text/plain"), countryCodePicker.getSelectedCountryCodeWithPlus());
        RequestBody countryName = RequestBody.create(MediaType.parse("text/plain"), countryCodePicker.getSelectedCountryNameCode());
        RequestBody rbLat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lat));
        RequestBody rbLng = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lng));
        RequestBody rbAccountAddress = RequestBody.create(MediaType.parse("text/plain"), accountAddress);
        RequestBody rbAutoNegoKey = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(autoNegoKey));

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

        if (etPassword.getText().toString().isEmpty() && etPasswordConfirmation.getText().toString().isEmpty()) {
            Common.showDialog(getContext());

            if (kbisPath.equals("") && idPath.equals("")) {
                profile = api.saveProfileWithoutPassword("Bearer " +
                                SharedPreference.getSimpleString(getContext(), Constants.accessToken),
                        email, name, preName, phone, business, siret, countryCode, countryName, rbLat, rbLng, rbAccountAddress, rbAutoNegoKey,
                        null, null);
            } else if (kbisPath.equals("") && !idPath.equals("")) {
                profile = api.saveProfileWithoutPassword("Bearer " +
                                SharedPreference.getSimpleString(getContext(), Constants.accessToken),
                        email, name, preName, phone, business, siret, countryCode, countryName, rbLat, rbLng, rbAccountAddress, rbAutoNegoKey,
                        null, idPart);
            } else if (!kbisPath.equals("") && idPath.equals("")) {
                profile = api.saveProfileWithoutPassword("Bearer " +
                                SharedPreference.getSimpleString(getContext(), Constants.accessToken),
                        email, name, preName, phone, business, siret, countryCode, countryName, rbLat, rbLng, rbAccountAddress, rbAutoNegoKey,
                        kbisPart, null);
            } else if (!kbisPath.equals("") && !idPath.equals("")) {
                profile = api.saveProfileWithoutPassword("Bearer " +
                                SharedPreference.getSimpleString(getContext(), Constants.accessToken),
                        email, name, preName, phone, business, siret, countryCode, countryName, rbLat, rbLng, rbAccountAddress, rbAutoNegoKey,
                        kbisPart, idPart);
            }
        } else {
            if (etPassword.getText().toString().isEmpty()) {
                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessageEnterPassword), false);

                return;
            } else if (etPasswordConfirmation.getText().toString().isEmpty()) {
                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessageEnter_password_confirmation), false);

                return;
            } else if (!etPassword.getText().toString().equals(etPasswordConfirmation.getText().toString())) {

                passwordTick.setVisibility(View.VISIBLE);
                CpassswordTick.setVisibility(View.VISIBLE);
                passwordTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                CpassswordTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));

                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.AccountSettingsErrorMessagePassword_not_same), false);

                return;
            } else {

                passwordTick.setVisibility(View.VISIBLE);
                CpassswordTick.setVisibility(View.VISIBLE);


                passwordTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
                CpassswordTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));

                Common.showDialog(getContext());
                RequestBody rbPassword = RequestBody.create(MediaType.parse("text/plain"), etPassword.getText().toString());
                RequestBody rbCPassword = RequestBody.create(MediaType.parse("text/plain"), etPasswordConfirmation.getText().toString());

                if (kbisPath.equals("") && idPath.equals("")) {
                    profile = api.saveProfile("Bearer " +
                                    SharedPreference.getSimpleString(getContext(), Constants.accessToken),
                            email, name, preName, phone, business, siret, countryCode, countryName, rbLat, rbLng, rbAccountAddress, rbAutoNegoKey,
                            rbPassword, rbCPassword, null, null);
                } else if (kbisPath.equals("") && !idPath.equals("")) {
                    profile = api.saveProfile("Bearer " +
                                    SharedPreference.getSimpleString(getContext(), Constants.accessToken),
                            email, name, preName, phone, business, siret, countryCode, countryName, rbLat, rbLng, rbAccountAddress, rbAutoNegoKey,
                            rbPassword, rbCPassword, null, idPart);
                } else if (!kbisPath.equals("") && idPath.equals("")) {
                    profile = api.saveProfile("Bearer " +
                                    SharedPreference.getSimpleString(getContext(), Constants.accessToken),
                            email, name, preName, phone, business, siret, countryCode, countryName, rbLat, rbLng, rbAccountAddress, rbAutoNegoKey,
                            rbPassword, rbCPassword, kbisPart, null);
                } else if (!kbisPath.equals("") && !idPath.equals("")) {
                    profile = api.saveProfile("Bearer " +
                                    SharedPreference.getSimpleString(getContext(), Constants.accessToken),
                            email, name, preName, phone, business, siret, countryCode, countryName, rbLat, rbLng, rbAccountAddress, rbAutoNegoKey,
                            rbPassword, rbCPassword, kbisPart, idPart);
                } else {
                    showToast(getActivity(), "Some Error occurred", false);
                    return;
                }
            }
        }
        profile.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                settingSaveChangesButton.setEnabled(true);
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("status")) {


                            MainActivity.radiationStatus = jsonObject.getBoolean("radiation_status");

                            DatabaseHelper db = new DatabaseHelper(getContext());
                            if (!jsonObject.getJSONObject("data").equals("{}")) {
                                JSONObject data = jsonObject.getJSONObject("data");

                                SharedPreference.saveSimpleString(getContext(), Constants.userId,
                                        String.valueOf(data.getInt("id")));

                                User user = new User(data.getInt("id"),
                                        data.getString("name"),
                                        data.getString("last_name"),
                                        data.getString("email"),
                                        data.getString("company_name"),
                                        data.getString("siret_no"),
                                        data.getString("phone"),
                                        data.getString("lang"),
                                        data.getString("country_code"),
                                        data.getString("lat"),
                                        data.getString("lng"),
                                        data.getString("address"),
                                        data.getInt("is_merchant"),
                                        data.getInt("nego_auto_accept"),
                                        data.getString("kbis_file_path"),
                                        data.getString("id_file_path"),
                                        0);

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
                                    if (!isPresent) {
                                        db.insertUser(user);
                                    }
                                }
                                if (!etPassword.getText().toString().isEmpty())
                                    SharedPreference.saveSimpleString(getContext(), Constants.userpassword, etPassword.getText().toString());
                                showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), true);
                            }
                        }
                    } else if (response.code() == 401) {
                        SharedPreference.saveSimpleString(getContext(), Constants.isLoggedIn, "false");
                        Intent i = new Intent(getContext(), SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    }

                } catch (Exception e) {
                    showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.something_is_not_right), false);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                settingSaveChangesButton.setEnabled(true);
                Common.dissmissDialog();
            }
        });

    }

    //calling the logout api
    private void callLogoutApi() {

        Log.d(TAG, "Token: " + SharedPreference.getSimpleString(getContext(), Constants.accessToken));
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> logout = api.logout("Bearer " + SharedPreference.getSimpleString(getContext(), Constants.accessToken),
                SharedPreference.getSimpleString(getContext(), Constants.deviceId), "user");

        logout.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());

                        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();

                        SharedPreference.saveSimpleString(getContext(), Constants.isLoggedIn, "false");
                        Intent i = new Intent(getContext(), SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                    } else if (response.code() == 401) {
                        SharedPreference.saveSimpleString(getContext(), Constants.isLoggedIn, "false");
                        Intent i = new Intent(getContext(), SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    //calling the change language api
    private void callChangeLanguageApi(String language) {
        Common.showDialog(getContext());
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> responseBodyCall = api.changeLanguage("Bearer " +
                SharedPreference.getSimpleString(getContext(), Constants.accessToken), language);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        changeLanguage(language);
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }
    //changing the language of the app'

    private void changeLanguage(String language) {
        SharedPreference.saveSimpleString(getContext(), Constants.language, language);
        Splash.appLanguage = language;

        Configuration config = getActivity().getResources().getConfiguration();
        String lang = SharedPreference.getSimpleString(getContext(), Constants.language);

        if (!(config.locale.getLanguage().equals(lang))) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());
        }
        try {
            Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {
        if (tag.equals(update_product)) {
            updateTheProductPrice(jsonObject);
        }
    }

    @Override
    public void onFailure(String message, String tag) {
        Log.d(ContentValues.TAG, "onFailure: " + tag);
    }

    private void updateTheProductPrice(JSONObject jsonObject) {
        try {
            String merchantId = jsonObject.getString("merchant_id");
            String productId = jsonObject.getString("product_id");
            String subProductId = jsonObject.getString("subproduct_id");
            String price = jsonObject.getString("price");
            String minPrice = jsonObject.getString("minprice");
            String subprominprice = jsonObject.getString("subprominprice");

            ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                    (price), (minPrice));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CustomTextWatcher implements TextWatcher {


        private View view;

        private CustomTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (view.getId() == R.id.etPassword) {
                if (etPassword.toString().isEmpty() && etPasswordConfirmation.getText().toString().isEmpty()) {
                    passwordTick.setVisibility(View.GONE);
                    CpassswordTick.setVisibility(View.GONE);
                } else if (etPassword.getText().toString().length() < 8) {
                    passwordTick.setVisibility(View.VISIBLE);
                    passwordTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                } else if (etPasswordConfirmation.getText().toString().equals(etPassword.getText().toString())) {
                    CpassswordTick.setVisibility(View.VISIBLE);
                    passwordTick.setVisibility(View.VISIBLE);
                    CpassswordTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
                    passwordTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
                } else {
                    CpassswordTick.setVisibility(View.VISIBLE);
                    passwordTick.setVisibility(View.VISIBLE);
                    CpassswordTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                    passwordTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                }
            } else if (view.getId() == R.id.etPasswordConfirmation) {
                if (editable.toString().isEmpty() && etPassword.getText().toString().isEmpty()) {
                    passwordTick.setVisibility(View.GONE);
                    CpassswordTick.setVisibility(View.GONE);
                } else if (etPasswordConfirmation.getText().toString().length() < 8) {
                    CpassswordTick.setVisibility(View.VISIBLE);
                    CpassswordTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                } else if (etPasswordConfirmation.getText().toString().equals(etPassword.getText().toString())) {
                    CpassswordTick.setVisibility(View.VISIBLE);
                    passwordTick.setVisibility(View.VISIBLE);
                    CpassswordTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
                    passwordTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
                } else {
                    CpassswordTick.setVisibility(View.VISIBLE);
                    passwordTick.setVisibility(View.VISIBLE);
                    CpassswordTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                    passwordTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                }
            } else if (view.getId() == R.id.etSiret) {
                if (editable.toString().isEmpty() || editable.toString().length() < 9 || editable.toString().length() > 9) {
                    siretTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                } else
                    siretTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
            } else if (view.getId() == R.id.etPhone) {
                if (editable.toString().isEmpty() || editable.toString().length() < 9 || editable.toString().length() > 11) {
                    phoneTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                } else
                    phoneTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
            } else if (view.getId() == R.id.etBussiness) {
                if (editable.toString().isEmpty()) {
                    bussinessTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                } else
                    bussinessTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
            } else if (view.getId() == R.id.AccountAddress) {
                if (editable.toString().isEmpty()) {
                    addressTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                } else
                    addressTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
            } else if (view.getId() == R.id.etName) {
                if (editable.toString().isEmpty()) {
                    firstNameTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                } else
                    firstNameTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
            } else if (view.getId() == R.id.etPreName) {
                if (editable.toString().isEmpty()) {
                    lastNameTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                } else
                    lastNameTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
            } else if (view.getId() == R.id.etEmail) {
                if (editable.toString().isEmpty() || !Common.isValidEmailId(editable.toString())) {
                    emailTick.setBackground(getContext().getResources().getDrawable(R.drawable.cross));
                } else
                    emailTick.setBackground(getContext().getResources().getDrawable(R.drawable.tick));
            }
        }

    }


//    private void getFileFromDirectory(int requestCode) {
//        // Checking if permission is not granted
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
//                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
//                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
//            requestPermissions(Constants.PERMISSIONS_STORAGE, requestCode);
//        } else {
//
//            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                    getContext().getResources().getString(R.string.app_name));
//            if (!imageStorageDir.exists()) {
//                imageStorageDir.mkdirs();
//            }
//            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//            cameraUri = Uri.fromFile(file);
//
//            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
//
//            Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory().getPath().toString());
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//
//            }else{
//
//            }
//
//            Intent chooseFile = new Intent();
////            chooseFile.setType("*/*");
//            chooseFile.addCategory(Intent.CATEGORY_DEFAULT);
//            String[] mimetypes = {"image/*", "application/pdf"};
//            chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//            chooseFile.setAction(Intent.ACTION_GET_CONTENT);
//            chooseFile.setDataAndType(selectedUri, "*/*");
//            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//            chooseFile.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
//            startActivityForResult(chooseFile, requestCode);
//        }
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
////        getFileFromDirectory(requestCode);
//
//    }

    //101 request code for Kbis Image
    //102 request code for id Image
    //201 request code for kbis Image Crop
    //202 request code for id Image Crop
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                        Glide.with(getContext()).load(R.drawable.ic_pdf).into(kbisImageSettings);
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
                        Glide.with(getContext()).load(R.drawable.ic_pdf).into(idImageSettings);
                    }
                }

            } else if (requestCode == 201) {
                kbisTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                kbisPath = FilePath.getPath(getContext(), cropperUri);
                kbisExtension = kbisPath.substring(kbisPath.lastIndexOf("."));

                Glide.with(getContext()).load(kbisPath).into(kbisImageSettings);
            } else if (requestCode == 202) {
                idTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                idPath = FilePath.getPath(getContext(), cropperUri);
                idExtension = idPath.substring(idPath.lastIndexOf("."));

                Glide.with(getContext()).load(idPath).into(idImageSettings);
            }
        }
    }

    private void startCrop(Uri sourceUri, int requestCode) {

        File file = new File(getContext().getCacheDir(), "IMG_" + System.currentTimeMillis() + ".jpg");
        destinationUriCropper = Uri.fromFile(file);

        startActivityForResult(UCrop.of(sourceUri, destinationUriCropper)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(5f, 5f)
                .getIntent(getContext()), requestCode);

    }

    public String getPathFromUri(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
