package com.vic.vicwsp.Models.Response.Support.SupportChat;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupportSingleChatResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;


    @SerializedName("data")
    @Expose
    private SupportChatData supportChatData = null;

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

    public SupportChatData getSupportChatData() {
        return supportChatData;
    }

    public void setSupportChatData(SupportChatData supportChatData) {
        this.supportChatData = supportChatData;
    }
}