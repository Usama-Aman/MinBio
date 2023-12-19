package com.vic.vicwsp.Utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class NetworkUtil {
    public static int getConnectivityStatusString(Context context) {
        int status = 0;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = 1; // 1 for WIFI
                return status;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = 2;  //2 for Mobile Data
                return status;
            }
        } else {
            status = 0; // 0 for No Internet
            return status;
        }
        return status;
    }
}
