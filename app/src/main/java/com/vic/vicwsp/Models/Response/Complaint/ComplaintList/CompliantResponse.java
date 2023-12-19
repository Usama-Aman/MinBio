package com.vic.vicwsp.Models.Response.Complaint.ComplaintList;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompliantResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private CompliantResponseData compliantResponseData;

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

    public CompliantResponseData getCompliantResponseData() {
        return compliantResponseData;
    }

    public void setCompliantResponseData(CompliantResponseData compliantResponseData) {
        this.compliantResponseData = compliantResponseData;
    }
}