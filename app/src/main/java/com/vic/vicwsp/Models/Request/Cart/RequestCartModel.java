
package com.vic.vicwsp.Models.Request.Cart;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestCartModel {

    @SerializedName("products")
    @Expose
    private List<Product> products = null;

    public RequestCartModel(List<Product> products) {
        super();
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
