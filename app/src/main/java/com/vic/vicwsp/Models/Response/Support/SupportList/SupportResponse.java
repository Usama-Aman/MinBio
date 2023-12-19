package com.vic.vicwsp.Models.Response.Support.SupportList;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupportResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private SupportResponseData supportResponseData;

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

    public SupportResponseData getSupportResponseData() {
        return supportResponseData;
    }

    public void setSupportResponseData(SupportResponseData supportResponseData) {
        this.supportResponseData = supportResponseData;
    }
}