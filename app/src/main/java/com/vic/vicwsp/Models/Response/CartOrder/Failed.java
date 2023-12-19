
package com.vic.vicwsp.Models.Response.CartOrder;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Failed {

    @SerializedName("merchant_id")
    @Expose
    private Integer merchantId;
    @SerializedName("products")
    @Expose
    private List<Product> products = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Failed() {
    }

    /**
     * 
     * @param merchantId
     * @param products
     */
    public Failed(Integer merchantId, List<Product> products) {
        super();
        this.merchantId = merchantId;
        this.products = products;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
