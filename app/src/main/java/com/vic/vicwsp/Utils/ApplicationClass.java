package com.vic.vicwsp.Utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Locale;

@SuppressLint("Registered")
public class ApplicationClass extends Application {

    //    public String socketUrl = "http://elxdrive.com:4000";
    public String socketUrl = "http://vicwsp.com:3000/";

    private SocketCallback socketCallback;
    private String TAG = "socketCallBack";
    private Locale locale;
    private Context context;
    public static ApplicationClass applicationClassInstance;


    //Socket Receiving Keys
    public static String update_product = "update_product";
    public static String ticker = "ticker";
    private Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        applicationClassInstance = this;
    }

    public static synchronized ApplicationClass getInstance() {
        return applicationClassInstance;
    }

    public void initializeSocket(int id) {
        try {

//            IO.Options opts = new IO.Options();
//            opts.query = "type=" + "user" + "&id=" + id;
//            mSocket = IO.socket(/*socketUrl*/ "http://10.20.30.192:3000/", opts);
            mSocket = IO.socket(socketUrl);
            mSocket.connect();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void switchOffSocket() {
        mSocket.disconnect();
    }

    public void initializeCallback(SocketCallback socketCallback) {
        this.socketCallback = socketCallback;
        listeningForOns();
    }


    public void sendConnection(int userId) {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", userId);
            jsonObject.put("user_type", "users");

            mSocket.emit("connect_user", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void emit(String ticker) {
        getSocket().emit(ticker);
    }

    public void emit(String tag, JSONObject jsonObject, Context context) {
        getSocket().emit(tag, jsonObject);
    }

    private void listeningForOns() {

        Log.e(TAG, "listeningForOns: " + String.valueOf(getSocket().connected()));


        getSocket().on(update_product, args -> {
            try {


                JSONObject jsonObject = (JSONObject) args[0];

                socketCallback.onSuccess(jsonObject, update_product);

            } catch (Exception e) {
                socketCallback.onFailure(e.getLocalizedMessage(), update_product);
                Log.e(TAG, "productListingOn: ", e);
            }
        });

        getSocket().on(ticker, args -> {
            try {
                URLogs.m("ticker socketSocket");
                JSONArray jsonArray = (JSONArray) args[0];

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("array", jsonArray);

                socketCallback.onSuccess(jsonObject, ticker);

            } catch (Exception e) {
                socketCallback.onFailure(e.getLocalizedMessage(), ticker);
                Log.e(TAG, "productListingOn: ", e);
            }
        });

        mSocket.on("update_location", args -> {
            try {
                URLogs.m("update_location socketSocket");
                JSONObject jsonObject = (JSONObject) args[0];

                socketCallback.onSuccess(jsonObject, "update_location");

            } catch (Exception e) {
                socketCallback.onFailure(e.getLocalizedMessage(), "update_location");
            }
        });


        mSocket.on("all_messages", args -> {
            try {

                JSONObject jsonObject = (JSONObject) args[0];

                socketCallback.onSuccess(jsonObject, "all_messages");

            } catch (Exception e) {
                socketCallback.onFailure(e.getLocalizedMessage(), "update_location");
            }
        });

    }


    public void changeLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        String lang = SharedPreference.getSimpleString(context, Constants.language);

        if (!(config.locale.getLanguage().equals(lang))) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            context.getResources().updateConfiguration(newConfig, context.getResources().getDisplayMetrics());
        }
    }

}
