
package com.vic.vicwsp.Models.Response.ProductDetailUpdated;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Seller {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("sub_products")
    @Expose
    private ArrayList<SubProduct> subProducts ;


    public Seller(Integer id, String name, String rating, String price, ArrayList<SubProduct> subProducts) {
        super();
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.subProducts = subProducts;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<SubProduct> getSubProducts() {
        return subProducts;
    }

    public void setSubProducts(ArrayList<SubProduct> subProducts) {
        this.subProducts = subProducts;
    }

}
