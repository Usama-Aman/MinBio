package com.vic.vicwsp.Models.Response.Support.SupportChat;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SupportChatResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<SupportChatData> supportChatList = null;


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

    public List<SupportChatData> getSupportChatList() {
        return supportChatList;
    }

    public void setSupportChatList(List<SupportChatData> supportChatList) {
        this.supportChatList = supportChatList;
    }
}