package com.vic.vicwsp.Network;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vic.vicwsp.Models.Response.Login.Login;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.Buyer2Driver;
import com.vic.vicwsp.Views.Activities.Complaints.ComplaintChat;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.NegoChat;
import com.vic.vicwsp.Views.Activities.Seller2Driver;
import com.vic.vicwsp.Views.Activities.SignIn;
import com.vic.vicwsp.Views.Activities.Splash;
import com.vic.vicwsp.Views.Activities.Support.SupportChat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

import static androidx.constraintlayout.widget.Constraints.TAG;

@SuppressLint("Registered")
public class FCMService extends FirebaseMessagingService {

    String GROUP_KEY = "com.elementary.minbio";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("Firebase New Token", "onNewToken: ------------" + s);

        Common.fcmDevices(getApplicationContext(),
                SharedPreference.getSimpleString(getApplicationContext(), Constants.deviceId), s);

    }

    //Function called when new message is received
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = "", body = "", orderId = "", type = "", price = "", quantity = "", unit = "";

        String messageData = "";

        if (remoteMessage == null)
            return;


        Log.e("Text Notificafions: ", "    :    " + remoteMessage);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body:" + remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {

                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(remoteMessage.getData());
                try {
                    if (object.has("body")) {
                        body = object.getString("body");
                    }
                    if (object.has("title")) {
                        title = object.getString("title");
                    }
                    if (object.has("type")) {
                        type = object.getString("type");
                    }
                    if (object.has("order_id")) {
                        orderId = object.getString("order_id");
                    }
                    if (object.has("price")) {
                        price = object.getString("price");
                    }
                    if (object.has("quantity")) {
                        quantity = object.getString("quantity");
                    }
                    if (object.has("unit")) {
                        unit = object.getString("unit");
                    }
                    if (object.has("message")) {
                        messageData = object.getString("message");
                    }

                    if (object.has("complaint_id")) {
                        orderId = object.getString("complaint_id");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        showMessage(type, orderId, body, title, messageData, unit, quantity, price);

    }

    //"Showing the notification
    private void showMessage(String type, String orderId, String body, String title, String messageData, String unit, String quantity, String price) {
        boolean toShowNotification;

        Intent resultIntent = null;

        if (Constants.isAppRunning && SharedPreference.getSimpleString(this, Constants.isLoggedIn).equals("true")) {

            resultIntent = new Intent(this, MainActivity.class);
            toShowNotification = true;

            if (type.equals(Constants.MessageReceived)
                    || type.equals(Constants.MessageReceivedFromDriverToBuyer)
                    || type.equals(Constants.MessageReceivedFromDriverToSeller) // Checking for notification Types
                    || type.equals(Constants.AdminCommentOnComplaint)
                    || type.equals(Constants.AdminCommentOnSupport))    {
                if (NegoChat.isChatActive) {
                    toShowNotification = Integer.parseInt(orderId) != NegoChat.orderId;

                    Intent chatIntent = new Intent("ChatBroadCast");
                    chatIntent.putExtra("messageData", messageData);
                    chatIntent.putExtra("orderNumber", orderId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(chatIntent);
                } else if (Buyer2Driver.isb2dChatActive) {
                    toShowNotification = Integer.parseInt(orderId) != Buyer2Driver.buyerDriverOrderId;

                    Intent chatIntent = new Intent("Buyer2DriverBroadCast");
                    chatIntent.putExtra("messageData", messageData);
                    chatIntent.putExtra("orderNumber", orderId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(chatIntent);
                } else if (Seller2Driver.iss2dChatActive) {
                    toShowNotification = Integer.parseInt(orderId) != Seller2Driver.sellerDriverOrderId;

                    Intent chatIntent = new Intent("Seller2DriverBroadCast");
                    chatIntent.putExtra("messageData", messageData);
                    chatIntent.putExtra("orderNumber", orderId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(chatIntent);
                } else if (ComplaintChat.isComplaintChatActive) {
                    toShowNotification = Integer.parseInt(orderId) != ComplaintChat.complaintId;

                    Intent chatIntent = new Intent("ComplaintChatBroadCast");
                    chatIntent.putExtra("messageData", messageData);
                    chatIntent.putExtra("complaintId", orderId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(chatIntent);

                } else if (type.equals(Constants.AdminCommentOnComplaint) && !ComplaintChat.isComplaintChatActive) {
                    resultIntent = new Intent(this, ComplaintChat.class);
                    toShowNotification = true;
                } else if (SupportChat.isSupportChatActive) {
                    toShowNotification = Integer.parseInt(orderId) != SupportChat.supportId;

                    Intent chatIntent = new Intent("SupportChatBroadCast");
                    chatIntent.putExtra("messageData", messageData);
                    chatIntent.putExtra("supportId", orderId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(chatIntent);

                } else if (type.equals(Constants.AdminCommentOnSupport) && !SupportChat.isSupportChatActive) {

                    resultIntent = new Intent(this, SupportChat.class);
                    toShowNotification = true;
                }


            } else {
                Intent intent = new Intent("UpdateNotificationBadge");
                intent.putExtra("count", 1);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                toShowNotification = true;
            }

        } else if (SharedPreference.getSimpleString(this, Constants.isLoggedIn).equals("false")) {
            toShowNotification = true;
            resultIntent = new Intent(this, SignIn.class);
        } else {
            resultIntent = new Intent(this, Splash.class);
            toShowNotification = true;
        }

        resultIntent.putExtra("supportId", orderId);
        resultIntent.putExtra("complaintId", orderId);
        resultIntent.putExtra("isComingFromNotification", true);
        resultIntent.putExtra("message", messageData);
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("orderId", orderId);
        resultIntent.putExtra("unit", unit);
        resultIntent.putExtra("quantity", quantity);
        resultIntent.putExtra("price", price);
        resultIntent.putExtra("body", body);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), new Random().nextInt(), resultIntent, 0);

        if (toShowNotification)
            sendNotification(title, body, resultPendingIntent);
    }

    private void sendNotification(String title, String body, PendingIntent resultPendingIntent) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(Constants.channelName, Constants.channelName,
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // to display notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(Constants.channelName);
            channel.canBypassDnd();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.channelName);

        notificationBuilder
                .setContentTitle(title)
                .setContentText(body)
                .setGroup(Constants.channelName)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.noti)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setContentIntent(resultPendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (notificationChannel != null) {
                notificationBuilder.setChannelId(Constants.channelName);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
        mNotificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}
