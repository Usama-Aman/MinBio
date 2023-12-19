package com.vic.vicwsp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InternetBCR extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
//        if (status == 0) {
//            showToast((AppCompatActivity) context, "No Internet Connection.", false);
//        } else if (status == 1)
//            showToast((AppCompatActivity) context, "WIFI enabled.", true);
//        else if (status == 2)
//            showToast((AppCompatActivity) context, "Mobile Data enabled.", true);
    }
}
