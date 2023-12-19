
package com.vic.vicwsp.Models.Response.SaveCards;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveCardsModel {

    @SerializedName("data")
    @Expose
    private ArrayList<Datum> data = null;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SaveCardsModel() {
    }

    /**
     * 
     * @param data
     * @param message
     * @param status
     */
    public SaveCardsModel(ArrayList<Datum> data, Boolean status, String message) {
        super();
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
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

}
