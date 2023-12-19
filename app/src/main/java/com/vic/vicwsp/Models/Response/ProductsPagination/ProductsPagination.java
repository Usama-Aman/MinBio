
package com.vic.vicwsp.Models.Response.ProductsPagination;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductsPagination  implements Parcelable {

    @SerializedName("data")
    @Expose
    private ArrayList<Datum> data = null;
    @SerializedName("links")
    @Expose
    private Links links;
    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("radiation_status")
    @Expose
    private boolean radiation_status;

    public ProductsPagination(ArrayList<Datum> data, Links links, Meta meta, Boolean status, String message) {
        super();
        this.data = data;
        this.links = links;
        this.meta = meta;
        this.status = status;
        this.message = message;
    }

    protected ProductsPagination(Parcel in) {
        byte tmpStatus = in.readByte();
        status = tmpStatus == 0 ? null : tmpStatus == 1;
        message = in.readString();
        radiation_status = in.readByte() != 0;
    }

    public static final Creator<ProductsPagination> CREATOR = new Creator<ProductsPagination>() {
        @Override
        public ProductsPagination createFromParcel(Parcel in) {
            return new ProductsPagination(in);
        }

        @Override
        public ProductsPagination[] newArray(int size) {
            return new ProductsPagination[size];
        }
    };

    public boolean isRadiation_status() {
        return radiation_status;
    }

    public void setRadiation_status(boolean radiation_status) {
        this.radiation_status = radiation_status;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (status == null ? 0 : status ? 1 : 2));
        dest.writeString(message);
        dest.writeByte((byte) (radiation_status ? 1 : 0));
    }
}
