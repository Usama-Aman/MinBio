
package com.vic.vicwsp.Models.Request.Cart;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("merchant_id")
    @Expose
    private Integer merchantId;
    @SerializedName("products")
    @Expose
    private List<Product_> products = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Product() {
    }

    /**
     * 
     * @param merchantId
     * @param products
     */
    public Product(Integer merchantId, List<Product_> products) {
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

    public List<Product_> getProducts() {
        return products;
    }

    public void setProducts(List<Product_> products) {
        this.products = products;
    }

}
