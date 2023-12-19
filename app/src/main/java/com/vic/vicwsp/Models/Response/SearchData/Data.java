
package com.vic.vicwsp.Models.Response.SearchData;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("products")
    @Expose
    private ArrayList<Product> products = null;
    @SerializedName("merchants")
    @Expose
    private ArrayList<Merchant> merchants = null;
    @SerializedName("countries")
    @Expose
    private ArrayList<Country> countries = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Data() {
    }

    /**
     * 
     * @param countries
     * @param merchants
     * @param products
     */
    public Data(ArrayList<Product> products, ArrayList<Merchant> merchants, ArrayList<Country> countries) {
        super();
        this.products = products;
        this.merchants = merchants;
        this.countries = countries;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Merchant> getMerchants() {
        return merchants;
    }

    public void setMerchants(ArrayList<Merchant> merchants) {
        this.merchants = merchants;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<Country> countries) {
        this.countries = countries;
    }

}
