
package com.vic.vicwsp.Models.Response.Notifications;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("notification")
    @Expose
    private String notification;
    @SerializedName("notification_type")
    @Expose
    private String notificationType;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("created_at")
    @Expose
    private String created_at;

    public Datum(Integer id, String title, String notification, String notificationType, Integer orderId, String notificationType1) {
        this.id = id;
        this.title = title;
        this.notification = notification;
        this.notificationType = notificationType;
        this.orderId = orderId;
        this.notificationType = notificationType1;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

}
