package com.vic.vicwsp.Views.Activities;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vic.vicwsp.Controllers.Helpers.DatabaseHelper;
import com.vic.vicwsp.Controllers.Helpers.FingerprintHandler;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Request.LoginRequest;
import com.vic.vicwsp.Models.Response.Login.Login;
import com.vic.vicwsp.Models.User;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.suke.widget.SwitchButton;

import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.dissmissDialog;
import static com.vic.vicwsp.Utils.Common.showToast;

public class SignIn extends AppCompatActivity implements View.OnClickListener {


    private ConstraintLayout btnLoginCreateAccount, btnLogin;
    private TextView tvForgotPasswordSignIn, dontHaveAnAccount;
    private EditText etSignInEmail, etSignInPassword;
        private FingerprintManager fingerprintManager;
        private KeyguardManager keyguardManager;
        private KeyStore keyStore;
        private KeyGenerator keyGenerator;
        private Cipher cipher;
        private FingerprintManager.CryptoObject cryptoObject;
        private FingerprintHandler fingerprintHandler;
        private static final String FINGERPRINT_KEY = "MINBIO";
    private SwitchButton fingerPrintSwitch;
    private AlertDialog alertDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();
        if (SharedPreference.getSimpleString(getApplicationContext(), Constants.isBiometric).equals("true")) {
            alertDialog = new AlertDialog.Builder(SignIn.this)
                    .setMessage(getResources().getString(R.string.signInTouch_rear_sensor))
                    .setPositiveButton("Cancel", (dialog, which) -> alertDialog.dismiss()).show();
            fingerPrint();
        }

        //TO get the app signature programmatically
//        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
//        appSignatureHelper.getAppSignatures();

    }

    //Initializing the views
    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        fingerPrintSwitch = findViewById(R.id.fingerPrintSwitch);
        etSignInEmail = findViewById(R.id.etSignInEmail);
        etSignInPassword = findViewById(R.id.etSignInPassword);
        btnLoginCreateAccount = findViewById(R.id.btnLoginCreateAccount);
        dontHaveAnAccount = findViewById(R.id.dontHaveAnAccount);
        tvForgotPasswordSignIn = findViewById(R.id.tvForgotPasswordSignIn);
        ImageView ivFingerPrint = findViewById(R.id.ivFingerPrint);
        ivFingerPrint.setOnClickListener(view -> {
            alertDialog = new AlertDialog.Builder(SignIn.this)
                    .setMessage(getResources().getString(R.string.signInTouch_rear_sensor))
                    .setPositiveButton("Cancel", (dialog, which) -> alertDialog.dismiss()).show();
        });
        btnLogin.setOnClickListener(this);
        tvForgotPasswordSignIn.setOnClickListener(this);
        btnLoginCreateAccount.setOnClickListener(this);

        String text = "<font color='black'>" + getResources().getString(R.string.signInDontHaveAnAccount) + "</font>" + " " +
                "<font color='#50a936'>" + getResources().getString(R.string.signInRegisterNow) + "</font>";
        dontHaveAnAccount.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

        if (SharedPreference.getSimpleString(SignIn.this, Constants.isBiometric).equals("true"))
            fingerPrintSwitch.setChecked(true);
        fingerPrintSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked)
                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.isBiometric, "true");
                else
                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.isBiometric, "false");
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.btnLogin:
                validate();
                break;
            case R.id.btnLoginCreateAccount:
                i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
                break;
            case R.id.tvForgotPasswordSignIn:
                i = new Intent(SignIn.this, ForgotPassword.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    //Validating the fields

    private void validate() {
        if (etSignInEmail.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.signInErrorMessageEnterEmail), false);
            return;
        }

        if (etSignInPassword.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.signInErrorMessageEnterPassword), false);
            return;
        }

        callLoginApi();


    }
    //calling the login api

    private void callLoginApi() {
        Common.showDialog(SignIn.this);

        Api api = RetrofitClient.getClient().create(Api.class);
        LoginRequest loginRequest = new LoginRequest(etSignInEmail.getText().toString(), etSignInPassword.getText().toString());
        Call<Login> loginCall = api.login(Splash.appLanguage, loginRequest);

        loginCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        Login login = new Login(response.body().getData(), response.body().getAccessToken(), response.body().getStatus(),
                                response.body().getMessage());

                        if (login.getStatus()) {

                            SharedPreference.saveSimpleString(getApplicationContext(), Constants.accessToken, login.getAccessToken());

                            if (login.getData() != null) {
                                if (login.getData().getPhone_verified() == 1 && login.getData().getEmail_verified() == 1 && login.getData().getIs_active() == 1) {

                                    User user = new User(login.getData().getId(), login.getData().getName(), login.getData().getLastName(),
                                            login.getData().getEmail(), login.getData().getCompanyName(), login.getData().getSiretNo(),
                                            login.getData().getPhone(), login.getData().getLang(), login.getData().getCountryCode(),
                                            login.getData().getLat(), login.getData().getLng(), login.getData().getAddress(),
                                            login.getData().getIs_merchant(), login.getData().getNego_auto_accept(),
                                            login.getData().getKbis_file_path(), login.getData().getId_file_path(),
                                            login.getData().getBank_detail_id());

                                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                                    List<User> users;
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
                                    db.close();
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.userId, String.valueOf(login.getData().getId()));
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.isLoggedIn, "true");
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.isFirstLogin, "true");
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.fingerPrintUser, login.getData().getEmail());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.countryCode, login.getData().getCountryCode());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.userPhone, login.getData().getPhone());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.userEmail, login.getData().getEmail());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.userpassword, etSignInPassword.getText().toString());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.isMerchant, String.valueOf(login.getData().getIs_merchant()));
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.language, user.getLang());

                                    DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                                    databaseHelper.dropCart();
                                    databaseHelper.close();

                                    changeLocale(user.getLang());

                                    Intent i = new Intent(SignIn.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }
                            } else
                                showToast(SignIn.this, response.body().getMessage(), false);

                        } else
                            showToast(SignIn.this, login.getMessage(), false);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (!jsonObject.getJSONObject("data").toString().equals("{}")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            int phone_verified = 0, email_verified = 0, is_active = 0;

                            String message = jsonObject.getString("message");
                            if (!message.isEmpty())
                                showToast(SignIn.this, message, false);

                            if (data.has("phone")) {
                                phone_verified = data.getInt("phone_verified");
                                if (phone_verified == 0) {
                                    String phoneNo = data.getString("phone");
                                    Intent i = new Intent(SignIn.this, OtpConfirmation.class);
                                    i.putExtra("phoneNo", phoneNo);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(i);
                                        }
                                    }, 1800);
                                }
                            } else if (data.has("email")) {
                                email_verified = data.getInt("email_verified");
                                if (email_verified == 0) {
                                    String email = data.getString("email");
                                    Intent i = new Intent(SignIn.this, EmailConfirmation.class);
                                    i.putExtra("email", email);

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(i);
                                        }
                                    }, 1800);
                                }
                            } else if (data.has("is_active")) {
                                is_active = data.getInt("is_active");
                                if (is_active == 0) {
                                    showToast(SignIn.this, jsonObject.getString("message"), false);
                                }
                            }

                        } else {
                            String message = jsonObject.getString("message");
                            if (!message.isEmpty())
                                showToast(SignIn.this, message, false);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                dissmissDialog();
                Log.d("Login Failure", "onFailure: " + t.getMessage());
            }
        });

    }


    private void changeLocale(String language) {
        Resources res = SignIn.this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fingerPrint() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintHandler = new FingerprintHandler(SignIn.this);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (fingerprintManager != null) {
                checkDeviceFingerprintSupport();
                generateFingerprintKeyStore();
                Cipher mCipher = instantiateCipher();
                if (mCipher != null) {
                    cryptoObject = new FingerprintManager.CryptoObject(mCipher);
                }

                fingerprintHandler.completeFingerAuthentication(fingerprintManager, cryptoObject);
            }
        }
    }


    //  ------------------ Finger print handler settings -------------------------------
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRestart() {

        if (fingerprintHandler != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintHandler.completeFingerAuthentication(fingerprintManager, cryptoObject);
            }
        super.onRestart();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkDeviceFingerprintSupport() {
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateFingerprintKeyStore() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            keyGenerator.init(new KeyGenParameterSpec.Builder(FINGERPRINT_KEY, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        try {
            keyGenerator.generateKey();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Cipher instantiateCipher() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(FINGERPRINT_KEY, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (fingerprintHandler != null)
                    fingerprintHandler.StopListener();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
