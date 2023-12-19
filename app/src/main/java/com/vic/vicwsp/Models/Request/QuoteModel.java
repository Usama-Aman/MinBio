package com.vic.vicwsp.Models.Request;

import com.vic.vicwsp.Models.Request.Cart.Product_;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuoteModel {

    @SerializedName("merchant_id")
    @Expose
    private Integer merchantId;
    @SerializedName("quote_id")
    @Expose
    private String quoteId;
    @SerializedName("products")
    @Expose
    private List<Product_> products = null;

    public QuoteModel(Integer merchantId, String quoteId, List<Product_> products) {
        this.merchantId = merchantId;
        this.quoteId = quoteId;
        this.products = products;
    }

    public QuoteModel() {

    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public List<Product_> getProducts() {
        return products;
    }

    public void setProducts(List<Product_> products) {
        this.products = products;
    }
}
