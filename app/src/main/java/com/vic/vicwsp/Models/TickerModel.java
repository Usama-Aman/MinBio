package com.vic.vicwsp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TickerModel {


    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
//    @SerializedName("product_name_fr")  Uncomment it when fr is added
    @Expose
    private String productNameFr;
    @SerializedName("minprice")
    @Expose
    private Double minprice;
    @Expose
    private String status;

    public TickerModel(Integer productId, String productName, String productNameFr, Double minprice, String status) {
        this.productId = productId;
        this.productName = productName;
        this.productNameFr = productNameFr;
        this.minprice = minprice;
        this.status = status;
    }

    public String getProductNameFr() {
        return productNameFr;
    }

    public void setProductNameFr(String productNameFr) {
        this.productNameFr = productNameFr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getMinprice() {
        return minprice;
    }

    public void setMinprice(Double minprice) {
        this.minprice = minprice;
    }


}
