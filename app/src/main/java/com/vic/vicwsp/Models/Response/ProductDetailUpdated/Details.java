
package com.vic.vicwsp.Models.Response.ProductDetailUpdated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Details {

    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("merchants")
    @Expose
    private Integer merchants;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Details() {
    }

    /**
     * 
     * @param price
     * @param merchants
     */
    public Details(String price, Integer merchants) {
        super();
        this.price = price;
        this.merchants = merchants;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getMerchants() {
        return merchants;
    }

    public void setMerchants(Integer merchants) {
        this.merchants = merchants;
    }

}
