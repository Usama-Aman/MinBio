package com.vic.vicwsp.Models.Response.Complaint.ComplaintChat;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComplaintChatResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<ComplaintChatData> complaintChatList = null;


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

    public List<ComplaintChatData> getComplaintChatList() {
        return complaintChatList;
    }

    public void setComplaintChatList(List<ComplaintChatData> complaintChatList) {
        this.complaintChatList = complaintChatList;
    }
}