package com.vic.vicwsp.Utils;

import android.Manifest;

public class Constants {

    public static boolean isAppRunning = false;
    public static String isFirstTime = "isFirstTime";

    public static String userId = "userId";
    public static String userEmail = "userEmail";
    public static String userPhone = "userPhone";
    public static String companyPhone = "companyPhone";
    public static String countryCode = "countryCode";
    public static String fingerPrintUser = "fingerPrintUser";
    public static String userpassword = "userPassword";
    public static String accessToken = "accessToken";
    public static String isLoggedIn = "isLoggedIn";
    public static String isBiometric = "isBiometric";
    public static String isFirstLogin = "isFirstLogin";
    public static String currency = "currency";
    public static String vat = "vat";
    public static String isMerchant = "isMerchant";
    public static String french = "fr";
    public static String english = "en";
    public static String language = "language";
    public static String channelName = "MINBIO";
    public static String deviceId = "deviceId";
    public static String fcmToken = "fcmToken";
    public static long mLastClickTime = 0;
    public static String fastDelivery = "fast_delivery";
    public static String truckLoadDelivery = "shared_delivery";
    public static String pickupDelivery = "pickup_delivery";
    public static String isBioOn = "isBioOn";
    public static String afterPayementSendToOrders = "afterPayementSendToOrders";

    /*   notifications Types  */
    public static String ProposalReceived = "ProposalReceived";
    public static String ProposalAccepted = "ProposalAccepted";
    public static String ProposalAcceptedPreOrder = "ProposalAcceptedPreOrder";
    public static String ProposalRejected = "ProposalRejected";
    public static String OrderAcceptedByDriver = "OrderAcceptedByDriver";
    public static String OrderCompletedByDriver = "OrderCompletedByDriver";
    public static String OrderExpired = "OrderExpired";
    public static String DriverPickedUpProducts = "DriverPickedUpProducts";
    public static String DriverPickedUpProductsSeller = "DriverPickedUpProductsSeller";
    public static String DriverReachedAtLocation = "DriverReachedAtLocation";
    public static String MessageReceived = "MessageReceived";
    public static String MessageReceivedFromDriverToBuyer = "MessageReceivedFromDriverToBuyer";
    public static String MessageReceivedFromDriverToSeller = "MessageReceivedFromDriverToSeller";
    public static String AdminCommentOnComplaint = "AdminCommentOnComplaint";
    public static String AdminCommentOnSupport = "AdminCommentOnSupport";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int IMAGE_REQUEST_CODE = 2001;
    public static final int AUDIO_REQUEST_CODE = 2002;

    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
}
