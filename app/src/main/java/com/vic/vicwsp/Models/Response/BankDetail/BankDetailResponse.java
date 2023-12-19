package com.vic.vicwsp.Models.Response.BankDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetailResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private BankDetailModel bankDetailModel;

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

    public BankDetailModel getData() {
        return bankDetailModel;
    }

    public void setData(BankDetailModel data) {
        this.bankDetailModel = data;
    }

    public BankDetailResponse(Boolean status, String message, BankDetailModel bankDetailModel) {
        this.status = status;
        this.message = message;
        this.bankDetailModel = bankDetailModel;
    }
}
