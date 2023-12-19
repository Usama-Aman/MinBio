
package com.vic.vicwsp.Models.Response.ProductsPagination;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Details {

    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("merchants")
    @Expose
    private Integer merchants;
    @SerializedName("is_favourite")
    @Expose
    private Integer isFavourite;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Details() {
    }

    /**
     * 
     * @param merchants
     * @param price
     * @param isFavourite
     */
    public Details(String price, Integer merchants, Integer isFavourite) {
        super();
        this.price = price;
        this.merchants = merchants;
        this.isFavourite = isFavourite;
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

    public Integer getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(Integer isFavourite) {
        this.isFavourite = isFavourite;
    }

}
