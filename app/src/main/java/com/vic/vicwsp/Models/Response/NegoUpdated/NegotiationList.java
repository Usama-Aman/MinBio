
package com.vic.vicwsp.Models.Response.NegoUpdated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NegotiationList {

    @SerializedName("product_variety")
    @Expose
    private String productName;
    @SerializedName("quantity")
    @Expose
    private float quantity;
    @SerializedName("product_price")
    @Expose
    private String productPrice;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("is_merchant")
    @Expose
    private Integer isMerchant;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("unit")
    @Expose
    private String unit;

    public NegotiationList(String productName, float quantity, String productPrice, String total, Integer isMerchant, String status, String unit) {
        this.productName = productName;
        this.quantity = quantity;
        this.productPrice = productPrice;
        this.total = total;
        this.isMerchant = isMerchant;
        this.status = status;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Integer getIsMerchant() {
        return isMerchant;
    }

    public void setIsMerchant(Integer isMerchant) {
        this.isMerchant = isMerchant;
    }

}
