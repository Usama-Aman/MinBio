
package com.vic.vicwsp.Models.Request.Cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product_ {

    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("vat")
    @Expose
    private String vat;

    public Product_(String productId, String quantity, String discount, String vat) {
        this.productId = productId;
        this.quantity = quantity;
        this.discount = discount;
        this.vat = vat;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
