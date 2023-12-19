
package com.vic.vicwsp.Models.Response.NegoUpdated;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Data implements Parcelable {

    @SerializedName("child_order_id")
    @Expose
    private int childOrderId;
    @SerializedName("parent_order_id")
    @Expose
    private int parentOrderId;
    @SerializedName("delivery_charges")
    @Expose
    private String deliveryCharges;
    @SerializedName("company_name")
    @Expose
    private String merchantName;
    @SerializedName("minprice")
    @Expose
    private double minprice;
    @SerializedName("maxprice")
    @Expose
    private double maxprice;
    @SerializedName("expired_at")
    @Expose
    private String expired_at;
    @SerializedName("expired_at_str")
    @Expose
    private String expired_at_str;
    @SerializedName("re_tries")
    @Expose
    private int re_tries;
    @Expose
    private String merchantId;
    @Expose
    private String productId;
    @SerializedName("order_status")
    @Expose
    private String order_status;
    @SerializedName("negotiations")
    @Expose
    private ArrayList<NegotiationList> negotiations = null;

    public Data(int childOrderId, int parentOrderId, String deliveryCharges, String merchantName, double minprice, double maxprice, String expired_at, String expired_at_str, int re_tries, String merchantId, String productId, String order_status, ArrayList<NegotiationList> negotiations) {
        this.childOrderId = childOrderId;
        this.parentOrderId = parentOrderId;
        this.deliveryCharges = deliveryCharges;
        this.merchantName = merchantName;
        this.minprice = minprice;
        this.maxprice = maxprice;
        this.expired_at = expired_at;
        this.expired_at_str = expired_at_str;
        this.re_tries = re_tries;
        this.merchantId = merchantId;
        this.productId = productId;
        this.order_status = order_status;
        this.negotiations = negotiations;
    }

    protected Data(Parcel in) {
        childOrderId = in.readInt();
        parentOrderId = in.readInt();
        deliveryCharges = in.readString();
        merchantName = in.readString();
        minprice = in.readDouble();
        maxprice = in.readDouble();
        expired_at = in.readString();
        expired_at_str = in.readString();
        re_tries = in.readInt();
        merchantId = in.readString();
        productId = in.readString();
        order_status = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public int getChildOrderId() {
        return childOrderId;
    }

    public void setChildOrderId(int childOrderId) {
        this.childOrderId = childOrderId;
    }

    public int getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(int parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(String expired_at) {
        this.expired_at = expired_at;
    }

    public String getExpired_at_str() {
        return expired_at_str;
    }

    public void setExpired_at_str(String expired_at_str) {
        this.expired_at_str = expired_at_str;
    }

    public int getRe_tries() {
        return re_tries;
    }

    public void setRe_tries(int re_tries) {
        this.re_tries = re_tries;
    }

    public double getMinprice() {
        return minprice;
    }

    public void setMinprice(double minprice) {
        this.minprice = minprice;
    }

    public double getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(double maxprice) {
        this.maxprice = maxprice;
    }

    public static Creator<Data> getCREATOR() {
        return CREATOR;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public ArrayList<NegotiationList> getNegotiations() {
        return negotiations;
    }

    public void setNegotiations(ArrayList<NegotiationList> negotiations) {
        this.negotiations = negotiations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(childOrderId);
        dest.writeInt(parentOrderId);
        dest.writeString(deliveryCharges);
        dest.writeString(merchantName);
        dest.writeDouble(minprice);
        dest.writeDouble(maxprice);
        dest.writeString(expired_at);
        dest.writeString(expired_at_str);
        dest.writeInt(re_tries);
        dest.writeString(merchantId);
        dest.writeString(productId);
        dest.writeString(order_status);
    }
}
