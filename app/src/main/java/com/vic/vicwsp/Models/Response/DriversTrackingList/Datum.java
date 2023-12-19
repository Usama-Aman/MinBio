
package com.vic.vicwsp.Models.Response.DriversTrackingList;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("merchants")
    @Expose
    private ArrayList<Merchant> merchants = null;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;
    @SerializedName("delivery_lat")
    @Expose
    private String deliveryLat;
    @SerializedName("delivery_lng")
    @Expose
    private String deliveryLng;
    @SerializedName("driver")
    @Expose
    private Driver driver;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Datum() {
    }

    /**
     * 
     * @param deliveryLng
     * @param driver
     * @param orderId
     * @param deliveryLat
     * @param id
     * @param merchants
     * @param deliveryStatus
     */
    public Datum(Integer id, String orderId, ArrayList<Merchant> merchants, String deliveryStatus, String deliveryLat, String deliveryLng, Driver driver) {
        super();
        this.id = id;
        this.orderId = orderId;
        this.merchants = merchants;
        this.deliveryStatus = deliveryStatus;
        this.deliveryLat = deliveryLat;
        this.deliveryLng = deliveryLng;
        this.driver = driver;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ArrayList<Merchant> getMerchants() {
        return merchants;
    }

    public void setMerchants(ArrayList<Merchant> merchants) {
        this.merchants = merchants;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDeliveryLat() {
        return deliveryLat;
    }

    public void setDeliveryLat(String deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    public String getDeliveryLng() {
        return deliveryLng;
    }

    public void setDeliveryLng(String deliveryLng) {
        this.deliveryLng = deliveryLng;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

}
