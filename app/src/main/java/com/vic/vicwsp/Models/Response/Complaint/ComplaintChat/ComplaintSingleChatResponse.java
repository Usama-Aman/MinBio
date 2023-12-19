package com.vic.vicwsp.Models.Response.Complaint.ComplaintChat;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComplaintSingleChatResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;


    @SerializedName("data")
    @Expose
    private ComplaintChatData complaintChatData = null;

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

    public ComplaintChatData getComplaintChatData() {
        return complaintChatData;
    }

    public void setComplaintChatData(ComplaintChatData complaintChatData) {
        this.complaintChatData = complaintChatData;
    }
}